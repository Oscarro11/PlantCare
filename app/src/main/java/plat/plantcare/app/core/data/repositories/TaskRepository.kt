package plat.plantcare.app.core.data.repositories

import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.core.data.models.Task

interface TaskRepository {
    fun observeAllTasks(): Flow<List<Task>>
    fun observeTodayTasks(): Flow<List<Task>>
    suspend fun setTaskCompleted(id: String, completed: Boolean)
}