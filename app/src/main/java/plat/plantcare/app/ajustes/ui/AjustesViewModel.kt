package plat.plantcare.app.settings.data

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import plat.plantcare.app.collection.data.CollectionLayout
import javax.inject.Inject

sealed interface AjustesEvent {
    data class ChangeRecordatorios(
        val enabled: Boolean
    ) : AjustesEvent

    data class ChangeAlertasClimaticas(
        val enabled: Boolean
    ) : AjustesEvent

    data class ChangeVistaColeccion(
        val layout: CollectionLayout
    ) : AjustesEvent
}

@HiltViewModel
class AjustesViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AjustesState())
    val state = _state.asStateFlow()

    fun onEvent(event: AjustesEvent) {
        when (event) {
            is AjustesEvent.ChangeRecordatorios -> {
                changeRecordatorios(event.enabled)
            }

            is AjustesEvent.ChangeAlertasClimaticas -> {
                changeAlertasClimaticas(event.enabled)
            }

            is AjustesEvent.ChangeVistaColeccion -> {
                changeVistaColeccion(event.layout)
            }
        }
    }

    private fun changeRecordatorios(enabled: Boolean) {
        _state.update {
            it.copy(
                recordatoriosInteligentes = enabled
            )
        }
    }

    private fun changeAlertasClimaticas(enabled: Boolean) {
        _state.update {
            it.copy(
                alertasClimaticas = enabled
            )
        }
    }

    private fun changeVistaColeccion(layout: CollectionLayout) {
        _state.update {
            it.copy(
                vistaColeccion = layout
            )
        }
    }
}