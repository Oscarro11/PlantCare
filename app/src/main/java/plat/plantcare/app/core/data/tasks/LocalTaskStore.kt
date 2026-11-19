package plat.plantcare.app.core.data.tasks

import androidx.room.withTransaction
import plat.plantcare.app.core.data.AppDatabase
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import java.security.MessageDigest
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

fun nextTaskId(id: String): String = MessageDigest.getInstance("SHA-256")
    .digest((id + ":next").toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

fun Task.nextWatering(completedOn: LocalDate): Task? =
    if (action == TaskAction.WATER && recurrenceDays != null && recurrenceDays > 0)
        copy(id = nextTaskId(id), dueDate = completedOn.plusDays(recurrenceDays.toLong()),
            completed = false, completedAt = null, parentTaskId = id, isSynced = false)
    else null

@Singleton
class LocalTaskStore @Inject constructor(private val database: AppDatabase) {
    val dao get() = database.taskDao()
    suspend fun create(uid: String, task: Task) = database.withTransaction {
        require(task.id.isNotBlank() && task.plantId.isNotBlank())
        require(task.recurrenceDays == null || task.recurrenceDays in 1..365)
        require(!task.completed) { "Una tarea nueva debe estar pendiente." }
        if (dao.find(uid, task.id) == null) {
            dao.put(task.toEntity(uid, pending = true))
            dao.enqueue(TaskWrite(uid, "CREATE:" + task.id, task.id, "CREATE", null))
        }
    }
    suspend fun complete(uid: String, id: String, date: LocalDate, today: LocalDate = LocalDate.now()) = database.withTransaction {
        require(!date.isAfter(today)) { "La fecha de realización no puede estar en el futuro." }
        val existing = requireNotNull(dao.find(uid, id)) { "Esta tarea ya no está disponible." }
        if (!existing.completed) {
            val completed = existing.toModel().copy(completed = true, completedAt = date)
            dao.put(completed.toEntity(uid, pending = true))
            completed.nextWatering(date)?.let { next ->
                if (dao.find(uid, next.id) == null) dao.put(next.toEntity(uid, pending = true))
            }
            dao.enqueue(TaskWrite(uid, "COMPLETE:" + id, id, "COMPLETE", date.toString()))
        }
    }
    suspend fun purgeForPlant(uid: String, plantId: String) = database.withTransaction {
        dao.clearWritesForPlant(uid, plantId)
        dao.deleteForPlant(uid, plantId)
    }
    suspend fun mergeRemote(uid: String, task: Task) = database.withTransaction {
        if (dao.find(uid, task.id)?.pending != true) dao.put(task.toEntity(uid))
    }
    suspend fun acknowledge(write: TaskWrite, canonical: List<Task>) = database.withTransaction {
        dao.acknowledge(write.userId, write.operationId)
        canonical.forEach { remote ->
            val local = dao.find(write.userId, remote.id)
            if (dao.writeCount(write.userId, remote.id) == 0) dao.put(remote.toEntity(write.userId))
            else if (local != null) dao.put(local.copy(dueDate = remote.dueDate.toString()))
        }
    }
}
