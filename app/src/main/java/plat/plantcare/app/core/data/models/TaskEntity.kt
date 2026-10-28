package plat.plantcare.app.core.data.models

import androidx.annotation.DrawableRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.R
import plat.plantcare.app.core.ui.darkenColor
import java.time.LocalDate
import java.time.ZoneId

// ---------------------------------------------------------------------------
// Data layer: what is stored in Firestore
// ---------------------------------------------------------------------------

/**
 * Firestore representation of a task.
 * Colección: plantas/{plantId}/tareas/{id}
 *
 * Only the data layer (repositories) should know about this class.
 * Every property needs a default value so Firestore can deserialize it.
 */
data class TaskEntity(
    @DocumentId val id: String = "",
    val plantId: String = "",
    val dueDate: Timestamp? = null,      // Fecha límite (an instant in time)
    val action: String = "",             // Stored as String so unknown values don't crash
    val description: String = "",
    val completed: Boolean = false
    // `urgency` is intentionally NOT stored: it is derived from dueDate
)

// ---------------------------------------------------------------------------
// Domain layer: what the rest of the app uses
// ---------------------------------------------------------------------------

data class Task(
    val id: String,
    val plantId: String,
    val dueDate: LocalDate,              // Calendar date in the device's time zone
    val action: TaskAction,
    val description: String,
    val completed: Boolean
) {
    /** Derived, never stored. Pass [today] to make it testable. */
    fun urgency(today: LocalDate = LocalDate.now()): TaskUrgency = dueDate.toUrgency(today)
}

// ---------------------------------------------------------------------------
// Conversions (Entity <-> domain)
// ---------------------------------------------------------------------------

/**
 * Returns null when the document has no due date, so callers can use `mapNotNull`.
 * Pass [zone] explicitly in tests; it defaults to the device zone.
 */
fun TaskEntity.toDomain(zone: ZoneId = ZoneId.systemDefault()): Task? {
    val date = dueDate?.toLocalDate(zone) ?: return null
    return Task(
        id = id,
        plantId = plantId,
        dueDate = date,
        action = TaskAction.fromName(action),
        description = description,
        completed = completed
    )
}

fun Task.toEntity(zone: ZoneId = ZoneId.systemDefault()): TaskEntity =
    TaskEntity(
        id = id,
        plantId = plantId,
        dueDate = dueDate.toTimestamp(zone),
        action = action.name,
        description = description,
        completed = completed
    )

// ---------------------------------------------------------------------------
// Urgency
// ---------------------------------------------------------------------------

enum class TaskUrgency {
    ATRASADO,
    PROXIMO,
    PRIORITARIO,
    HOY
}

// ---------------------------------------------------------------------------
// Actions
// ---------------------------------------------------------------------------

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

// ---------------------------------------------------------------------------
// UI helpers
// ---------------------------------------------------------------------------

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