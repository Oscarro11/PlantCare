package plat.plantcare.app.discover.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DescubrirEvent {
    data class ToggleListaDeseos(val plantaId: String) : DescubrirEvent
    data class ChangeBusqueda(val value: String) : DescubrirEvent
    data object ToggleFiltro : DescubrirEvent
    data object Retry : DescubrirEvent
}

@HiltViewModel
class DescubrirViewModel @Inject constructor(private val repository: DiscoverRepository) : ViewModel() {
    private val _state = MutableStateFlow(DescubrirState())
    val state = _state.asStateFlow()
    private var observation: Job? = null
    init { observe() }

    private fun observe() {
        observation?.cancel()
        _state.update { it.copy(isLoading = true, error = null) }
        observation = viewModelScope.launch {
            repository.observeCatalog().catch {
                _state.update { it.copy(isLoading = false, error = "No se pudo cargar el catálogo. Revisa tu conexión e inténtalo de nuevo.") }
            }.collect { catalog ->
                _state.update { current -> current.copy(
                    plantasRecomendadas = catalog.plants.map { plant -> PlantaRecomendada(
                        plant.id, plant.imageUrl, plant.displayName, plant.scientificName,
                        plant.feature, plant.id in catalog.wishedIds
                    ) },
                    isLoading = catalog.isLoading,
                    error = if (catalog.isSignedIn) null else "Inicia sesión para explorar el catálogo."
                ) }
            }
        }
    }

    fun onEvent(event: DescubrirEvent) {
        when (event) {
            is DescubrirEvent.ChangeBusqueda -> _state.update { it.copy(busqueda = event.value) }
            DescubrirEvent.ToggleFiltro -> _state.update { it.copy(soloDeseadas = !it.soloDeseadas) }
            DescubrirEvent.Retry -> observe()
            is DescubrirEvent.ToggleListaDeseos -> {
                val plant = state.value.plantasRecomendadas.firstOrNull { it.id == event.plantaId } ?: return
                if (plant.id in state.value.pendingIds) return
                _state.update { it.copy(pendingIds = it.pendingIds + plant.id, error = null) }
                viewModelScope.launch {
                    try { repository.setWished(plant.id, !plant.enListaDeseos) }
                    catch (cancelled: CancellationException) { throw cancelled }
                    catch (exception: Exception) { _state.update { it.copy(error = "No se pudo actualizar tu lista de deseos.") } }
                    finally { _state.update { it.copy(pendingIds = it.pendingIds - plant.id) } }
                }
            }
        }
    }
}
