package plat.plantcare.app.core.data.tasks

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import java.time.LocalDate

@Entity(tableName = "tasks", primaryKeys = ["userId", "id"], indices = [Index(value = ["userId", "dueDate"])])
data class TaskEntity(
    val userId: String, val id: String, val plantId: String, val dueDate: String,
    val action: String, val description: String, val completed: Boolean,
    val completedOn: String?, val recurrenceDays: Int?, val parentTaskId: String?,
    val plantName: String, val plantImageUrl: String, val plantLocation: String,
    val pending: Boolean
) {
    fun toModel() = Task(id, plantId, LocalDate.parse(dueDate), TaskAction.fromName(action), description,
        completed, completedOn?.let(LocalDate::parse), recurrenceDays, parentTaskId,
        plantName, plantImageUrl, plantLocation, !pending)
}
fun Task.toEntity(uid: String, pending: Boolean = false) = TaskEntity(uid, id, plantId, dueDate.toString(),
    action.name, description, completed, completedAt?.toString(), recurrenceDays, parentTaskId,
    plantName, plantImageUrl, plantLocation, pending)

@Entity(tableName = "task_writes", primaryKeys = ["userId", "operationId"])
data class TaskWrite(val userId: String, val operationId: String, val taskId: String, val kind: String, val completedOn: String?)

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :uid ORDER BY completed ASC, dueDate ASC, id ASC")
    fun observe(uid: String): Flow<List<TaskEntity>>
    @Query("SELECT * FROM tasks WHERE userId = :uid AND id = :id")
    suspend fun find(uid: String, id: String): TaskEntity?
    @Upsert suspend fun put(task: TaskEntity)
    @Query("DELETE FROM tasks WHERE userId = :uid AND id = :id AND pending = 0")
    suspend fun deleteClean(uid: String, id: String)
    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :uid AND completed = 0 AND action = 'WATER' AND dueDate <= :today")
    suspend fun pendingWaterings(uid: String, today: String): Int
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun enqueue(write: TaskWrite)
    @Query("SELECT * FROM task_writes WHERE userId = :uid ORDER BY CASE kind WHEN 'CREATE' THEN 0 ELSE 1 END, operationId")
    suspend fun writes(uid: String): List<TaskWrite>
    @Query("DELETE FROM task_writes WHERE userId = :uid AND operationId = :operationId")
    suspend fun acknowledge(uid: String, operationId: String)
    @Query("SELECT COUNT(*) FROM task_writes WHERE userId = :uid AND taskId = :id")
    suspend fun writeCount(uid: String, id: String): Int
}
