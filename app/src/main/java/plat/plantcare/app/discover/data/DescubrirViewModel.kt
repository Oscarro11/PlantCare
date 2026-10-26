package plat.plantcare.app.discover.data

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed interface DescubrirEvent {
    data class ToggleListaDeseos(
        val plantaId: String
    ) : DescubrirEvent
}

@HiltViewModel
class DescubrirViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(DescubrirState())
    val state = _state.asStateFlow()

    fun onEvent(event: DescubrirEvent) {
        when (event) {
            is DescubrirEvent.ToggleListaDeseos -> {
                toggleListaDeseos(event.plantaId)
            }
        }
    }

    private fun toggleListaDeseos(plantaId: String) {
        _state.update { currentState ->
            currentState.copy(
                plantasRecomendadas =
                    currentState.plantasRecomendadas.map { planta ->
                        if (planta.id == plantaId) {
                            planta.copy(
                                enListaDeseos = !planta.enListaDeseos
                            )
                        } else {
                            planta
                        }
                    }
            )
        }
    }
}