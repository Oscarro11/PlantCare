package plat.plantcare.app.collection.allPlants.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.core.data.plant.PlantRepository
import plat.plantcare.app.core.data.repositories.TaskRepository
import plat.plantcare.app.core.data.repositories.WeatherRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.core.preferences.AppSettings
import plat.plantcare.app.core.preferences.DataStorePreferences
import javax.inject.Inject

sealed interface CollectionEvent{
    data class ChangeSearchbar (
        val value: String
    ) : CollectionEvent
    data object ToggleDisplay : CollectionEvent
}

private data class CollectionLocalState(
    val searchQuery: String = "",
    val preferencesError: String? = null
)

@HiltViewModel
class CollectionViewModel @Inject constructor (
    private val tasksRepository: TaskRepository,
    private val plantRepository: PlantRepository,
    private val weatherRepository: WeatherRepository,
    private val authRepository: AuthRepository,
    private val preferences: DataStorePreferences
) : ViewModel() {

    private val _localState = MutableStateFlow(CollectionLocalState())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val appSettings = authRepository.sessionUid.flatMapLatest { uid ->
        if (uid == null) flowOf(AppSettings())
        else preferences.observeAppSettings(uid).catch {
            _localState.update { it.copy(preferencesError = "No se pudo cargar tu vista de Colección.") }
            emit(AppSettings())
        }
    }

    val state: StateFlow<CollectionState> = combine(
        tasksRepository.observeAllTasks(),
        plantRepository.observePlants(),
        weatherRepository.getTodayClimate(),
        _localState,
        appSettings
    ) { tasks, plants, weather, local, settings ->
        val query = local.searchQuery.trim()
        val tasksByPlant = tasks.groupBy { it.plantId }
        val visiblePlants = if (query.isEmpty()) plants
        else plants.filter { it.name.contains(query, ignoreCase = true) }

        val plantsWithPending = tasks
            .filter { !it.completed }
            .distinctBy { it.plantId }
            .size

        CollectionState(
            pendingWatering = tasks
                .filter { it.action == TaskAction.WATER }
                .size,
            todayClimate = weather,
            healthPercentage = if (plants.isEmpty()) 1f else 1 - (plantsWithPending / plants.size.toFloat()),
            plantList = visiblePlants.map { plant ->
                CollectionCardInfo(
                    id = plant.id,
                    name = plant.name,
                    image = plant.imageUrl,
                    location = plant.location,
                    tasks = tasksByPlant[plant.id].orEmpty()
                )
            },
            searchQuery = local.searchQuery,
            preferencesError = local.preferencesError,
            selectedLayout = CollectionLayout.valueOf(settings.collectionLayout.name)
        )
    }
        .catch {  }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CollectionState()
        )

    fun onEvent(event: CollectionEvent) {
        when (event) {
            is CollectionEvent.ToggleDisplay -> changeToggle()
            is CollectionEvent.ChangeSearchbar -> changeSearchbar(event.value)
        }
    }

    private fun changeToggle() {
        val uid = authRepository.currentUid ?: return
        viewModelScope.launch {
            try {
                preferences.toggleCollectionLayout(uid)
                if (authRepository.currentUid == uid) _localState.update { it.copy(preferencesError = null) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                if (authRepository.currentUid == uid) _localState.update {
                    it.copy(preferencesError = "No se pudo guardar la vista de Colección. Inténtalo de nuevo.")
                }
            }
        }
    }

    private fun changeSearchbar(value: String) {
        _localState.update {
            it.copy(
                searchQuery = value
            )
        }
    }
}