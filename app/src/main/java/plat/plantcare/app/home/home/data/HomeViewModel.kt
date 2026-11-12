package plat.plantcare.app.home.home.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import plat.plantcare.app.core.data.repositories.PlantRepository
import plat.plantcare.app.core.data.repositories.TaskRepository
import plat.plantcare.app.core.data.user.UserRepository
import javax.inject.Inject

sealed interface HomeEvent { data class CompleteTask(val id: String) : HomeEvent }
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository, plantRepository: PlantRepository, userRepository: UserRepository
) : ViewModel() {
    private val error = MutableStateFlow<String?>(null)
    val state = combine(taskRepository.observeTodayTasks(), plantRepository.observePlants(), userRepository.observeCurrentUser(), error) { tasks, plants, user, message ->
        val pending = tasks.filter { !it.completed }
        HomeState(userName = user?.name.orEmpty(), plantTotal = plants.size,
            remainingPlants = pending.map { it.plantId }.distinct().size, pendingTasks = pending.size,
            tasks = tasks.map { it.toTaskCard(plants.associateBy { plant -> plant.id }) }, error = message)
    }.catch { emit(HomeState(error = "No se pudieron cargar las tareas.")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeState())
    fun onEvent(event: HomeEvent) {
        if (event is HomeEvent.CompleteTask && state.value.tasks.any { it.id == event.id && !it.completed }) viewModelScope.launch {
            try { taskRepository.completeTask(event.id); error.value = null }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (exception: Exception) { error.value = exception.message ?: "No se pudo completar la tarea." }
        }
    }
}
