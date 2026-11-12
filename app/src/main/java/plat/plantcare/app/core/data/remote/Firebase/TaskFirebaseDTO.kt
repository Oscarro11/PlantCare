package plat.plantcare.app.core.data.remote.Firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.core.data.models.toLocalDate
import java.time.LocalDate
import java.time.ZoneId

@IgnoreExtraProperties
data class TaskFirebaseDTO(
    @DocumentId val id: String = "", val plantId: String = "", val dueDate: String? = null,
    val action: String = "", val description: String = "", val completed: Boolean = false,
    val completedAt: Timestamp? = null, val completedOn: String? = null,
    val recurrenceDays: Int? = null, val parentTaskId: String? = null,
    val plantName: String = "", val plantImageUrl: String = "", val plantLocation: String = ""
)
fun TaskFirebaseDTO.toModel(zone: ZoneId = ZoneId.systemDefault()): Task? {
    val due = runCatching { LocalDate.parse(dueDate) }.getOrNull() ?: return null
    val completion = completedOn?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        ?: completedAt?.toLocalDate(zone)
    return Task(id, plantId, due, TaskAction.fromName(action), description, completed,
        completion, recurrenceDays?.takeIf { it > 0 }, parentTaskId, plantName, plantImageUrl, plantLocation)
}
