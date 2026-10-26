package plat.plantcare.app.home.data

import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.core.data.models.TaskUrgency
import java.time.LocalDate

//This is an auxiliary class for UI in HomeScreen and AllTasks.
data class HomeTaskCardUiState(
    val id: String = "",
    val urgency: TaskUrgency = TaskUrgency.HOY,
    val completed: Boolean = false,
    val action: TaskAction = TaskAction.OTHER,
    val description: String = "",
    val plantName: String = "",
    val plantImageUrl: String = "",
    val location: String = ""
)

data class HomeState (
    val userName: String = "",
    val date: LocalDate = LocalDate.now(),
    val plantTotal: Int = 0,
    val remainingPlants: Int = 0,
    val pendingTasks: Int = 0,
    val tasks: List<HomeTaskCardUiState> = emptyList()
)