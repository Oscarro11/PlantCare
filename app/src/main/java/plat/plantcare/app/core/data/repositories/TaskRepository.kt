package plat.plantcare.app.core.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

interface TaskRepository {
    fun observeAllTasks(): Flow<List<Task>>
    fun observeTodayTasks(): Flow<List<Task>>
    suspend fun setTaskCompleted(id: String, completed: Boolean)
}

//TODO: remove fake repositories once data retrieval from firebase is defined
@Singleton
class FakeTaskRepository @Inject constructor(): TaskRepository {
    val fakeTasks = MutableStateFlow (
        listOf(
            Task(
                id = "task-1",
                plantId = "plant-1",
                dueDate = LocalDate.now().plusDays(2),
                action = TaskAction.WATER, // Replace with your enum value
                description = "Water the monstera",
                completed = false
                ),
                Task(
                id = "task-2",
                plantId = "plant-2",
                dueDate = LocalDate.now(),
                action = TaskAction.CHECK_SOIL_MOISTURE,
                description = "Check whether the soil is dry",
                completed = false
            ),
            Task(
                id = "task-3",
                plantId = "plant-3",
                dueDate = LocalDate.now().plusDays(1),
                action = TaskAction.FERTILIZE,
                description = "Add a small amount of fertilizer",
                completed = false
            ),
            Task(
                id = "task-2",
                plantId = "plant-4",
                dueDate = LocalDate.now().plusDays(5),
                action = TaskAction.PRUNE,
                description = "Trim dry leaves",
                completed = false
            ),
            Task(
                id = "task-5",
                plantId = "plant-1",
                dueDate = LocalDate.now().plusDays(1),
                action = TaskAction.WATER,
                description = "Water the monstera",
                completed = false
            )
        )
    )

    override fun observeAllTasks(): Flow<List<Task>> = fakeTasks
    override fun observeTodayTasks(): Flow<List<Task>> = flowOf(emptyList())
    override suspend fun setTaskCompleted(id: String, completed: Boolean) {
        fakeTasks.update { currentTasks ->
            currentTasks.map { task ->
                if (task.id == id) task.copy(completed = completed) else task
            }
        }
    }
}