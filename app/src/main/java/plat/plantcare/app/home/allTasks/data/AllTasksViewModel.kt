package plat.plantcare.app.home.allTasks.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.core.data.repositories.PlantRepository
import plat.plantcare.app.core.data.repositories.TaskRepository
import plat.plantcare.app.home.home.data.toTaskCard
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AllTasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository, plantRepository: PlantRepository
) : ViewModel() {
    private val ui = MutableStateFlow(AllTasksState())
    val state = combine(taskRepository.observeAllTasks(), plantRepository.observePlants(), ui) { tasks, plants, local ->
        local.copy(tasks = tasks.map { it.toTaskCard(plants.associateBy { plant -> plant.id }) }, plants = plants, isLoading = false)
    }.catch { emit(AllTasksState(error = "No se pudieron cargar las tareas.", isLoading = false)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AllTasksState())
    fun history(completed: Boolean) { ui.update { it.copy(showCompleted = completed) } }
    fun complete(id: String, date: LocalDate) { viewModelScope.launch {
        try { taskRepository.completeTask(id, date); ui.update { it.copy(error = null) } }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (exception: Exception) { ui.update { it.copy(error = exception.message ?: "No se pudo completar la tarea.") } }
    } }
    fun create(plantId: String, action: TaskAction, due: LocalDate, interval: String, description: String) {
        if (ui.value.saving) return
        val plant = state.value.plants.firstOrNull { it.id == plantId } ?: return
        val days = if (action == TaskAction.WATER && interval.isNotBlank()) interval.toIntOrNull() else null
        if (action == TaskAction.WATER && interval.isNotBlank() && (days == null || days !in 1..365)) {
            ui.update { it.copy(error = "El intervalo debe ser de 1 a 365 días.") }; return
        }
        ui.update { it.copy(saving = true, error = null) }
        viewModelScope.launch {
            try {
                taskRepository.createTask(Task(UUID.randomUUID().toString(), plantId, due, action,
                    description.trim().ifBlank { action.displayName }, false, null, days,
                    plantName = plant.name, plantImageUrl = plant.imageUrl, plantLocation = plant.location))
                ui.update { it.copy(saving = false, created = it.created + 1) }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (exception: Exception) { ui.update { it.copy(saving = false, error = exception.message ?: "No se pudo guardar la tarea.") } }
        }
    }
}
