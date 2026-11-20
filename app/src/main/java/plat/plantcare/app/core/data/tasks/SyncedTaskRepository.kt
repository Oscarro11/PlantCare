package plat.plantcare.app.core.data.tasks

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.toTimestamp
import plat.plantcare.app.core.data.remote.Firebase.TaskFirebaseDTO
import plat.plantcare.app.core.data.remote.Firebase.toModel
import plat.plantcare.app.core.data.repositories.TaskRepository
import plat.plantcare.app.notifications.TaskWorkScheduler
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

fun currentDay(): Flow<LocalDate> = flow {
    while (currentCoroutineContext().isActive) { emit(LocalDate.now()); delay(60_000) }
}.distinctUntilChanged()

@Singleton
class SyncedTaskRepository @Inject constructor(
    private val store: LocalTaskStore,
    private val firestore: FirebaseFirestore,
    private val auth: AuthRepository,
    private val scheduler: TaskWorkScheduler
) : TaskRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val started = AtomicBoolean(false)
    private val syncMutex = Mutex()

    fun start() {
        if (!started.compareAndSet(false, true)) return
        scope.launch {
            auth.currentUser.collectLatest { user ->
                if (user != null) {
                    scheduler.sync(user.uid)
                    remoteSnapshots(user.uid).retryWhen { error, _ ->
                        Log.w("TaskSync", "No se pudieron leer las tareas", error)
                        delay(30_000); true
                    }.collect { snapshot ->
                        for (change in snapshot.documentChanges) {
                            if (change.type == com.google.firebase.firestore.DocumentChange.Type.REMOVED)
                                store.dao.deleteClean(user.uid, change.document.id)
                            else {
                                val task = runCatching { change.document.toObject(TaskFirebaseDTO::class.java).toModel() }.getOrNull()
                                if (task != null) store.mergeRemote(user.uid, task)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun remoteSnapshots(uid: String): Flow<QuerySnapshot> = callbackFlow {
        val listener = firestore.collection("usuarios").document(uid).collection("tareas")
            .addSnapshotListener { snapshot, error ->
                if (error != null) close(error) else if (snapshot != null) trySend(snapshot)
            }
        awaitClose { listener.remove() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeAllTasks(): Flow<List<Task>> = combine(
        auth.sessionUid.flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList()) else store.dao.observe(uid).map { rows -> rows.map { it.toModel() } }
        }, currentDay()
    ) { tasks, _ -> tasks }

    override fun observeTodayTasks(): Flow<List<Task>> = combine(observeAllTasks(), currentDay()) { tasks, today ->
        tasks.filter { if (it.completed) it.completedAt == today else !it.dueDate.isAfter(today) }
    }
    override suspend fun deleteForPlant(plantId: String) = syncMutex.withLock {
        val uid = requireNotNull(auth.currentUid) { "Inicia sesión para eliminar la planta." }
        val documents = firestore.collection("usuarios").document(uid).collection("tareas")
            .whereEqualTo("plantId", plantId).get(com.google.firebase.firestore.Source.SERVER).await().documents
        for (chunk in documents.chunked(400)) {
            val batch = firestore.batch()
            chunk.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
        store.purgeForPlant(uid, plantId)
    }
    override suspend fun createTask(task: Task) {
        val uid = requireNotNull(auth.sessionUid.first()) { "Inicia sesión para crear tareas." }
        store.create(uid, task)
        scheduler.sync(uid)
    }
    override suspend fun completeTask(id: String, completedOn: LocalDate) {
        val uid = requireNotNull(auth.sessionUid.first()) { "Inicia sesión para completar tareas." }
        store.complete(uid, id, completedOn)
        scheduler.sync(uid)
    }
    override suspend fun setTaskCompleted(id: String, completed: Boolean) {
        if (completed) completeTask(id)
    }

    /** Only acknowledged remote operations leave the durable Room outbox. */
    suspend fun syncPending(uid: String) = syncMutex.withLock {
        while (true) {
            if (auth.currentUid != uid) return@withLock
            val writes = store.dao.writes(uid)
            if (writes.isEmpty()) return@withLock
            val operation = writes.firstOrNull { write ->
                val parent = store.dao.find(uid, write.taskId)?.parentTaskId
                write.kind == "CREATE" || parent == null || writes.none { it.taskId == parent && it.kind == "COMPLETE" }
            } ?: return@withLock
            val local = requireNotNull(store.dao.find(uid, operation.taskId)).toModel()
            val collection = firestore.collection("usuarios").document(uid).collection("tareas")
            val canonical = firestore.runTransaction { transaction ->
                val ref = collection.document(local.id)
                val snapshot = transaction.get(ref)
                val remote = snapshot.toObject(TaskFirebaseDTO::class.java)?.toModel()
                if (operation.kind == "CREATE") {
                    if (remote != null) listOf(remote)
                    else {
                        val original = local.copy(completed = false, completedAt = null)
                        transaction.set(ref, original.remoteFields())
                        listOf(original)
                    }
                } else {
                    val date = LocalDate.parse(operation.completedOn)
                    val original = remote ?: local.copy(completed = false, completedAt = null)
                    // The first server completion wins if two devices completed the same task offline.
                    val completed = original.copy(completed = true, completedAt = if (original.completed) original.completedAt ?: date else date)
                    val next = completed.nextWatering(requireNotNull(completed.completedAt))
                    val existingNext = next?.let { transaction.get(collection.document(it.id)).toObject(TaskFirebaseDTO::class.java)?.toModel() }
                    if (remote?.completed != true) transaction.set(ref, completed.remoteFields())
                    if (next != null && existingNext == null) transaction.set(collection.document(next.id), next.remoteFields())
                    listOfNotNull(completed, existingNext ?: next)
                }
            }.await()
            store.acknowledge(operation, canonical)
        }
    }
}

private fun Task.remoteFields(): Map<String, Any?> = mapOf(
    "plantId" to plantId, "dueDate" to dueDate.toString(), "action" to action.name,
    "description" to description, "completed" to completed, "completedOn" to completedAt?.toString(),
    "completedAt" to completedAt?.toTimestamp(), "recurrenceDays" to recurrenceDays,
    "parentTaskId" to parentTaskId, "plantName" to plantName, "plantImageUrl" to plantImageUrl,
    "plantLocation" to plantLocation, "updatedAt" to FieldValue.serverTimestamp()
)
