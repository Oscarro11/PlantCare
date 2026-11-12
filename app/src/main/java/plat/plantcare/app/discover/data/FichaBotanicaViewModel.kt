package plat.plantcare.app.discover.data

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.plantcare.app.FichaBotanicaDestination
import javax.inject.Inject

sealed interface FichaBotanicaEvent {
    data object ToggleListaDeseos : FichaBotanicaEvent
    data object Retry : FichaBotanicaEvent
}

@HiltViewModel
class FichaBotanicaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DiscoverRepository
) : ViewModel() {
    private val plantId = savedStateHandle.toRoute<FichaBotanicaDestination>().plantaId
    private val _state = MutableStateFlow(FichaBotanicaState())
    val state = _state.asStateFlow()
    private var observation: Job? = null
    init { observe() }

    private fun observe() {
        observation?.cancel()
        _state.update { it.copy(isLoading = true, error = null) }
        observation = viewModelScope.launch {
            repository.observeCatalog().catch {
                _state.update { it.copy(isLoading = false, error = "No se pudo cargar esta planta. Revisa tu conexión.") }
            }.collect { catalog ->
                val plant = catalog.plants.firstOrNull { it.id == plantId }
                val care = catalog.care[plantId]
                _state.update { current ->
                    if (plant == null) FichaBotanicaState(
                        isLoading = catalog.isLoading,
                        notFound = !catalog.isLoading && catalog.isSignedIn,
                        error = if (catalog.isSignedIn) null else "Inicia sesión para ver esta planta."
                    ) else current.copy(
                        planta = PlantaDetalle(plant.imageUrl, plant.displayName, plant.scientificName,
                            plant.family.ifBlank { "Sin información" }, plant.origin.ifBlank { "Sin información" }, plant.description),
                        caracteristicas = listOf(
                            CaracteristicaPlanta("☀", "LUZ", plant.light),
                            CaracteristicaPlanta("♧", "RIEGO", plant.watering),
                            CaracteristicaPlanta("◉", "HUMEDAD", plant.humidity),
                            CaracteristicaPlanta("♣", "MASCOTAS", plant.petSafety, advertencia = plant.petWarning)
                        ).filter { it.valor.isNotBlank() },
                        pasos = listOf(
                            PasoCuidado("Riego", care?.wateringGuide.orEmpty()),
                            PasoCuidado("Ubicación y luz", care?.recommendedLocationAndLight.orEmpty()),
                            PasoCuidado("Nutrición", care?.nutrition.orEmpty()),
                            PasoCuidado("Limpieza y pulverización", care?.sprayingAndCleaning.orEmpty())
                        ).filter { it.descripcion.isNotBlank() },
                        enListaDeseos = plantId in catalog.wishedIds,
                        isLoading = false, notFound = false, error = null
                    )
                }
            }
        }
    }

    fun onEvent(event: FichaBotanicaEvent) {
        when (event) {
            FichaBotanicaEvent.Retry -> observe()
            FichaBotanicaEvent.ToggleListaDeseos -> {
                if (state.value.isSaving || state.value.isLoading || state.value.notFound) return
                val wished = !state.value.enListaDeseos
                _state.update { it.copy(isSaving = true, error = null) }
                viewModelScope.launch {
                    try { repository.setWished(plantId, wished) }
                    catch (cancelled: CancellationException) { throw cancelled }
                    catch (exception: Exception) { _state.update { it.copy(error = "No se pudo actualizar tu lista de deseos.") } }
                    finally { _state.update { it.copy(isSaving = false) } }
                }
            }
        }
    }
}
