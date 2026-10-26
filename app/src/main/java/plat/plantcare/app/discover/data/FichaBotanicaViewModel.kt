package plat.plantcare.app.discover.data

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed interface FichaBotanicaEvent {
    data object ToggleListaDeseos : FichaBotanicaEvent
}

@HiltViewModel
class FichaBotanicaViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(FichaBotanicaState())
    val state = _state.asStateFlow()

    fun onEvent(event: FichaBotanicaEvent) {
        when (event) {
            FichaBotanicaEvent.ToggleListaDeseos -> toggleListaDeseos()
        }
    }

    private fun toggleListaDeseos() {
        _state.update {
            it.copy(
                enListaDeseos = !it.enListaDeseos
            )
        }
    }
}