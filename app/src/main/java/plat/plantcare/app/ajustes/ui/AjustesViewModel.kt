package plat.plantcare.app.settings.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.plantcare.app.ajustes.data.AjustesState
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.collection.allPlants.data.CollectionLayout
import plat.plantcare.app.core.preferences.DataStorePreferences
import plat.plantcare.app.core.preferences.PreferredCollectionLayout
import plat.plantcare.app.notifications.ReminderPreferences
import plat.plantcare.app.core.data.plant.PlantRepository
import plat.plantcare.app.core.data.repositories.TaskRepository
import plat.plantcare.app.core.data.user.UserRepository
import plat.plantcare.app.core.data.models.TaskAction
import javax.inject.Inject

sealed interface AjustesEvent {
    data class ChangeReminderTime(val hour: Int, val minute: Int) : AjustesEvent
    data class ChangeRecordatorios(val enabled: Boolean) : AjustesEvent
    data class ChangeAlertasClimaticas(val enabled: Boolean) : AjustesEvent
    data class ChangeVistaColeccion(val layout: CollectionLayout) : AjustesEvent
    data object DeleteData : AjustesEvent
}

@HiltViewModel
class AjustesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val reminderPreferences: ReminderPreferences,
    private val preferences: DataStorePreferences,
    private val plants: PlantRepository,
    private val tasks: TaskRepository,
    private val users: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AjustesState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(plants.observePlants(), tasks.observeAllTasks(), users.observeCurrentUser()) { collection, care, user ->
                Triple(collection.size, care.count { it.completed && it.action == TaskAction.WATER }, user)
            }.catch {
                _state.update { it.copy(preferencesError = "No se pudieron cargar los datos del perfil.") }
            }.collect { (count, watered, user) ->
                _state.update { it.copy(plantasVivas = count, riegosHechos = watered,
                    diasRacha = user?.streakDays ?: 0, fotoUrl = user?.imageUrl?.takeIf { value -> value.isNotBlank() },
                    ubicacion = user?.location?.takeIf { value -> value.isNotBlank() } ?: "Sin ubicación registrada") }
            }
        }
        viewModelScope.launch {
            authRepository.sessionUid.collectLatest { uid ->
                // Reset immediately on account changes, before loading the next user's settings.
                _state.update { it.copy(recordatoriosInteligentes = false, horaAviso = "09:00",
                    alertasClimaticas = true, vistaColeccion = CollectionLayout.GRID, preferencesError = null) }
                if (uid != null) {
                    combine(reminderPreferences.observe(uid), preferences.observeAppSettings(uid)) { reminders, app ->
                        reminders to app
                    }.catch {
                        _state.update { it.copy(preferencesError = "No se pudieron cargar tus preferencias. Vuelve a abrir Ajustes.") }
                    }.collect { (reminders, app) ->
                        _state.update { it.copy(recordatoriosInteligentes = reminders.enabled,
                            horaAviso = reminders.displayTime, alertasClimaticas = app.weatherAlerts,
                            vistaColeccion = CollectionLayout.valueOf(app.collectionLayout.name), preferencesError = null) }
                    }
                }
            }
        }
    }

    fun onEvent(event: AjustesEvent) {
        val uid = authRepository.currentUid ?: return
        viewModelScope.launch {
            try {
                when (event) {
                    is AjustesEvent.ChangeReminderTime -> reminderPreferences.time(uid, event.hour, event.minute)
                    is AjustesEvent.ChangeRecordatorios -> reminderPreferences.enabled(uid, event.enabled)
                    is AjustesEvent.ChangeAlertasClimaticas -> preferences.setWeatherAlerts(uid, event.enabled)
                    is AjustesEvent.ChangeVistaColeccion -> preferences.setCollectionLayout(uid,
                        PreferredCollectionLayout.valueOf(event.layout.name))
                    is AjustesEvent.DeleteData -> deleteData()
                }
                if (authRepository.currentUid == uid) _state.update { it.copy(preferencesError = null) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                if (authRepository.currentUid == uid) _state.update {
                    it.copy(preferencesError = "No se pudo guardar la preferencia. Inténtalo de nuevo.")
                }
            }
        }
    }

    fun logOut(onDone: () -> Unit) {
        viewModelScope.launch { authRepository.signOut(); onDone() }
    }

    private fun deleteData() {
        viewModelScope.launch {
            plants
        }
    }
}
