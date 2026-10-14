package plat.plantcare.app.home.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import plat.plantcare.app.core.data.models.toUrgency
import plat.plantcare.app.core.data.repositories.PlantRepository
import plat.plantcare.app.core.data.repositories.TaskRepository

class AllTasksViewModel(
    private val taskRepository: TaskRepository,
    private val plantRepository: PlantRepository
) : ViewModel() {

    val state: StateFlow<AllTasksState> = combine(
        taskRepository.observeAllTasks(),
        plantRepository.observePlants()
    ) { tasks, plants ->
        val plantsById = plants.associateBy { it.id }

        AllTasksState(
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
        .catch {  }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AllTasksState()
        )
}