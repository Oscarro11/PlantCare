package plat.plantcare.app.home.allTasks.data

import plat.plantcare.app.home.home.data.HomeTaskCardUiState

data class AllTasksState(
    val tasks: List<HomeTaskCardUiState> = emptyList()
)