package plat.plantcare.app.favorites.data

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
import plat.plantcare.app.discover.data.DiscoverRepository
import javax.inject.Inject

sealed interface ListaDeseosEvent {
    data class ChangeBusqueda(val busqueda: String) : ListaDeseosEvent
    data class EliminarPlanta(val plantaId: String) : ListaDeseosEvent
    data object ToggleOrden : ListaDeseosEvent
    data object Retry : ListaDeseosEvent
}

@HiltViewModel
class ListaDeseosViewModel @Inject constructor(private val repository: DiscoverRepository) : ViewModel() {
    private val _state = MutableStateFlow(ListaDeseosState())
    val state = _state.asStateFlow()
    private var observation: Job? = null
    init { observe() }

    private fun observe() {
        observation?.cancel()
        _state.update { it.copy(isLoading = true, error = null) }
        observation = viewModelScope.launch {
            repository.observeCatalog().catch {
                _state.update { it.copy(isLoading = false, error = "No se pudo cargar tu lista de deseos. Inténtalo de nuevo.") }
            }.collect { catalog ->
                _state.update { current -> current.copy(
                    plantasDeseadas = catalog.wishedIds.map { id ->
                        val plant = catalog.plants.firstOrNull { it.id == id }
                        PlantaDeseada(id, plant?.imageUrl.orEmpty(), plant?.displayName ?: "Especie no disponible", plant?.scientificName.orEmpty())
                    }.sortedBy { it.nombre.lowercase() },
                    isLoading = catalog.isLoading,
                    error = if (catalog.isSignedIn) null else "Inicia sesión para consultar tu lista de deseos."
                ) }
            }
        }
    }

    fun onEvent(event: ListaDeseosEvent) {
        when (event) {
            is ListaDeseosEvent.ChangeBusqueda -> _state.update { it.copy(busqueda = event.busqueda) }
            ListaDeseosEvent.ToggleOrden -> _state.update { it.copy(ordenInverso = !it.ordenInverso) }
            ListaDeseosEvent.Retry -> observe()
            is ListaDeseosEvent.EliminarPlanta -> {
                if (event.plantaId in state.value.pendingIds) return
                _state.update { it.copy(pendingIds = it.pendingIds + event.plantaId, error = null) }
                viewModelScope.launch {
                    try { repository.setWished(event.plantaId, false) }
                    catch (cancelled: CancellationException) { throw cancelled }
                    catch (exception: Exception) { _state.update { it.copy(error = "No se pudo quitar esta planta de tu lista.") } }
                    finally { _state.update { it.copy(pendingIds = it.pendingIds - event.plantaId) } }
                }
            }
        }
    }
}
