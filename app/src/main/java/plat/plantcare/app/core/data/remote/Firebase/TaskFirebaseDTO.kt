package plat.plantcare.app.core.data.remote.Firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.core.data.models.toLocalDate
import java.time.ZoneId

data class TaskFirebaseDTO(
    @DocumentId val id: String = "",
    val plantId: String = "",
    val dueDate: String? = null,      // Fecha límite (es un string de YYYY-MM-DD, mas facil de manejar)
    val action: String = "",             // Stored as String so unknown values don't crash
    val description: String = "",
    val completed: Boolean = false,
    val completedAt: Timestamp? = null
    // `urgency` is intentionally NOT stored: it is derived from dueDate
)

/**
 * Returns null when the document has no due date, so callers can use `mapNotNull`.
 * Pass [zone] explicitly in tests; it defaults to the device zone.
 */
fun TaskFirebaseDTO.toModel(zone: ZoneId = ZoneId.systemDefault()): Task? {
    if (dueDate == null) return null
    val completionDate = completedAt?.toLocalDate(zone)
    return Task(
        id = id,
        plantId = plantId,
        dueDate = dueDate.toLocalDate(),
        action = TaskAction.fromName(action),
        description = description,
        completed = completed,
        completedAt = completionDate
    )
}