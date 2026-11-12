package plat.plantcare.app.home.allTasks.data

import plat.plantcare.app.core.data.models.Plant
import plat.plantcare.app.home.home.data.HomeTaskCardUiState

data class AllTasksState(
    val tasks: List<HomeTaskCardUiState> = emptyList(),
    val plants: List<Plant> = emptyList(), val showCompleted: Boolean = false,
    val error: String? = null, val isLoading: Boolean = true,
    val saving: Boolean = false, val created: Int = 0
) {
    val visibleTasks get() = tasks.filter { it.completed == showCompleted }
    val pendingSync get() = tasks.count { !it.isSynced }
}
