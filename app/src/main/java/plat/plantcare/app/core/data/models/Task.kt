package plat.plantcare.app.core.data.models

import androidx.annotation.DrawableRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import plat.plantcare.app.R
import plat.plantcare.app.core.data.remote.Firebase.TaskFirebaseDTO
import plat.plantcare.app.core.ui.darkenColor
import java.time.LocalDate
import java.time.ZoneId

data class Task(
    val id: String,
    val plantId: String,
    val dueDate: LocalDate,              // Calendar date in the device's time zone
    val action: TaskAction,
    val description: String,
    val completed: Boolean,
    val completedAt: LocalDate?
) {
    /** Derived, never stored. Pass [today] to make it testable. */
    fun urgency(today: LocalDate = LocalDate.now()): TaskUrgency = dueDate.toUrgency(today)
}

fun Task.toFirebaseDTO(zone: ZoneId = ZoneId.systemDefault()): TaskFirebaseDTO =
    TaskFirebaseDTO(
        id = id,
        plantId = plantId,
        dueDate = dueDate.toString(),
        action = action.name,
        description = description,
        completed = completed
    )

enum class TaskUrgency {
    ATRASADO,
    PROXIMO,
    PRIORITARIO,
    HOY
}

//TODO: update icons for each task action
enum class TaskAction(
    @DrawableRes val iconId: Int,
    val displayName: String
) {
    WATER(
        iconId = R.drawable.water_drop,
        displayName = "Regar"
    ),
    MIST(
        iconId = R.drawable.water_drop,
        displayName = "Atomizar"
    ),
    CLEAN_LEAVES(
        iconId = R.drawable.water_drop,
        displayName = "Limpiar hojas"
    ),
    PRUNE(
        iconId = R.drawable.water_drop,
        displayName = "Quitar excedentes"
    ),
    FERTILIZE(
        iconId = R.drawable.water_drop,
        displayName = "Fertilizar"
    ),
    ROTATE(
        iconId = R.drawable.water_drop,
        displayName = "Rotar"
    ),
    REPOT(
        iconId = R.drawable.water_drop,
        displayName = "Cambiar de maceta"
    ),
    CHECK_SOIL_MOISTURE(
        iconId = R.drawable.water_drop,
        displayName = "Revisar sustrato"
    ),
    CHECK_FOR_PESTS(
        iconId = R.drawable.water_drop,
        displayName = "Revisar pestes"
    ),
    TREAT_FOR_PESTS(
        iconId = R.drawable.water_drop,
        displayName = "Tratar pestes"
    ),
    PROPAGATE(
        iconId = R.drawable.water_drop,
        displayName = "Propagar"
    ),
    OTHER(
        iconId = R.drawable.water_drop,
        displayName = "Otro"
    );

    companion object {
        /** Safe lookup: unknown or missing values fall back to [OTHER]. */
        fun fromName(name: String?): TaskAction =
            entries.firstOrNull { it.name == name } ?: OTHER
    }
}

@Composable
fun TaskUrgency.backgroundColor(): Color = when (this) {
    TaskUrgency.ATRASADO -> darkenColor(Color.Red, 0.4f)
    TaskUrgency.PRIORITARIO -> MaterialTheme.colorScheme.secondary
    TaskUrgency.HOY -> Color.Red
    TaskUrgency.PROXIMO -> MaterialTheme.colorScheme.tertiary
}

@Composable
fun TaskUrgency.contentColor(): Color = when (this) {
    TaskUrgency.ATRASADO -> Color.White
    TaskUrgency.PRIORITARIO,
    TaskUrgency.HOY -> MaterialTheme.colorScheme.onPrimary
    TaskUrgency.PROXIMO -> darkenColor(MaterialTheme.colorScheme.onPrimary)
}