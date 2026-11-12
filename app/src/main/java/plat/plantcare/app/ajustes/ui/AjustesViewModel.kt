package plat.plantcare.app.settings.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import plat.plantcare.app.notifications.ReminderPreferences
import plat.plantcare.app.ajustes.data.AjustesState
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.collection.allPlants.data.CollectionLayout
import javax.inject.Inject

sealed interface AjustesEvent {
    data class ChangeReminderTime(val hour: Int, val minute: Int) : AjustesEvent
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
class AjustesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val reminderPreferences: ReminderPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(AjustesState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.sessionUid.collectLatest { uid ->
                if (uid == null) _state.update { it.copy(recordatoriosInteligentes = false) }
                else reminderPreferences.observe(uid).collect { settings ->
                    _state.update { it.copy(recordatoriosInteligentes = settings.enabled, horaAviso = settings.displayTime) }
                }
            }
        }
    }
    fun onEvent(event: AjustesEvent) {
        when (event) {
            is AjustesEvent.ChangeReminderTime -> viewModelScope.launch {
                authRepository.sessionUid.first()?.let { reminderPreferences.time(it, event.hour, event.minute) }
            }
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
        viewModelScope.launch { authRepository.sessionUid.first()?.let { reminderPreferences.enabled(it, enabled) } }
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

    fun logOut(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()

            onDone()
        }
    }
}