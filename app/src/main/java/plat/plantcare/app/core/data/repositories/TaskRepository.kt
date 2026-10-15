package plat.plantcare.app.core.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import plat.plantcare.app.core.data.models.Task
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
    override fun observeAllTasks(): Flow<List<Task>> = flowOf(emptyList())
    override fun observeTodayTasks(): Flow<List<Task>> = flowOf(emptyList())
    override suspend fun setTaskCompleted(id: String, completed: Boolean) {}
}