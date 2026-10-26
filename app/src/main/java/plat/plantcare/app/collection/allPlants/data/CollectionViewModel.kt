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
import plat.plantcare.app.R
import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.core.data.repositories.PlantRepository
import plat.plantcare.app.core.data.repositories.TaskRepository
import plat.plantcare.app.core.data.repositories.WeatherRepository
import javax.inject.Inject

sealed interface CollectionEvent{
    data class ChangeSearchbar (
        val value: String
    ) : CollectionEvent
    data object ToggleDisplay : CollectionEvent
}

private data class CollectionLocalState(
    val searchQuery: String = "",
    val collectionLayout: CollectionLayout = CollectionLayout.GRID
)

@HiltViewModel
class CollectionViewModel @Inject constructor (
    private val tasksRepository: TaskRepository,
    private val plantRepository: PlantRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _localState = MutableStateFlow(CollectionLocalState())

    val state: StateFlow<CollectionState> = combine(
        tasksRepository.observeAllTasks(),
        plantRepository.observePlants(),
        weatherRepository.getTodayClimate(),
        _localState
    ) { tasks, plants, weather, local ->
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
                    image = R.drawable.plant_img_example,
                    location = plant.location,
                    tasks = tasksByPlant[plant.id].orEmpty()
                )
            },
            searchQuery = local.searchQuery,
            selectedLayout = local.collectionLayout
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
        _localState.update {
            it.copy(
                collectionLayout = if (it.collectionLayout == CollectionLayout.GRID)
                    CollectionLayout.LIST else CollectionLayout.GRID
            )
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