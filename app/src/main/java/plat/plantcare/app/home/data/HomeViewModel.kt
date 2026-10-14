package plat.plantcare.app.home.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import plat.plantcare.app.core.data.models.toUrgency
import plat.plantcare.app.core.data.repositories.PlantRepository
import plat.plantcare.app.core.data.repositories.TaskRepository
import plat.plantcare.app.core.data.repositories.UserRepository

sealed interface HomeEvent{
    data class CompleteTask( val id: String ): HomeEvent
}

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val plantRepository: PlantRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val state: StateFlow<HomeState> = combine(
        taskRepository.observeAllTasks(),
        plantRepository.observePlants(),
        userRepository.observeUserName()
    ) { tasks, plants, userName ->
        val plantsById = plants.associateBy { it.id }
        val pending = tasks.filter { !it.completed }

        HomeState(
            userName = userName,
            plantTotal = plants.size,
            remainingPlants = pending.map { it.plantId }.distinct().size,
            pendingTasks = pending.size,
            tasks = tasks.mapNotNull { task ->
                val plant = plantsById[task.plantId] ?: return@mapNotNull null
                HomeTaskCardUiState(
                    id = task.id,
                    urgency = task.dueDate.toUrgency(),
                    completed = task.completed,
                    action = task.action,
                    description = task.description,
                    plantName = plant.name,
                    plantImageUrl = plant.imageUrl,
                    location = plant.location
                )
            }
        )
    }
        .catch { /* log, or emit an error state */ }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeState()
        )

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.CompleteTask -> viewModelScope.launch { completeTask(event.id) }
        }
    }

    private suspend fun completeTask(id: String) {
        val current = state.value.tasks.firstOrNull { it.id == id }
            ?: throw IllegalStateException("Id of task $id doesn't exist")

        taskRepository.setTaskCompleted(id, !current.completed)
    }
}

