package plat.plantcare.app.favorites.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ListaDeseosEvent {
    data class ChangeBusqueda(
        val busqueda: String
    ) : ListaDeseosEvent

    data class EliminarPlanta(
        val plantaId: String
    ) : ListaDeseosEvent

    data class MoverAColeccion(
        val plantaId: String
    ) : ListaDeseosEvent
}

@HiltViewModel
class ListaDeseosViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ListaDeseosState())
    val state = _state.asStateFlow()

    fun onEvent(event: ListaDeseosEvent) {
        when (event) {
            is ListaDeseosEvent.ChangeBusqueda -> {
                changeBusqueda(event.busqueda)
            }

            is ListaDeseosEvent.EliminarPlanta -> {
                eliminarPlanta(event.plantaId)
            }
            is ListaDeseosEvent.MoverAColeccion -> {
                viewModelScope.launch {
                    moverAColeccion(event.plantaId)
                }
            }
        }
    }

    private fun changeBusqueda(busqueda: String) {
        _state.update {
            it.copy(
                busqueda = busqueda
            )
        }
    }

    private fun eliminarPlanta(plantaId: String) {
        _state.update {
            it.copy(
                plantasDeseadas = it.plantasDeseadas.filterNot { planta ->
                    planta.id == plantaId
                }
            )
        }
    }
}