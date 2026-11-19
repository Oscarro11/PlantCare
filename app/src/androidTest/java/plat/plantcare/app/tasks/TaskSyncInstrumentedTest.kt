package plat.plantcare.app.tasks

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.authentication.domain.AuthUser
import plat.plantcare.app.core.data.AppDatabase
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.core.data.tasks.*
import plat.plantcare.app.notifications.TaskWorkScheduler
import java.time.LocalDate
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class TaskSyncInstrumentedTest {
    @Test fun twoOfflineDevicesCreateOneSuccessorAndKeepTheFirstServerCompletion() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val app = FirebaseApp.initializeApp(context, FirebaseOptions.Builder().setProjectId("plantcare-3a58c")
            .setApplicationId("1:123:android:task-test").setApiKey("fake-api-key").build(), "task-test-" + UUID.randomUUID())
        val auth = FirebaseAuth.getInstance(app).apply { useEmulator("127.0.0.1", 9099) }
        val remote = FirebaseFirestore.getInstance(app).apply { useEmulator("127.0.0.1", 8080) }
        val dbA = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val dbB = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val id = "task-test-" + UUID.randomUUID()
        try {
            withTimeout(15_000) { auth.signInAnonymously().await() }
            val uid = auth.currentUser!!.uid
            val session = TaskSession(uid)
            val storeA = LocalTaskStore(dbA)
            val storeB = LocalTaskStore(dbB)
            val scheduler = TaskWorkScheduler(context)
            val repoA = SyncedTaskRepository(storeA, remote, session, scheduler)
            val repoB = SyncedTaskRepository(storeB, remote, session, scheduler)
            val today = LocalDate.now()
            val task = Task(id, "plant", today.minusDays(3), TaskAction.WATER, "Riego de prueba", false, null, 7)
            storeA.create(uid, task)
            withTimeout(15_000) { repoA.syncPending(uid) }
            storeB.mergeRemote(uid, task)
            storeA.complete(uid, id, today.minusDays(1))
            storeB.complete(uid, id, today)
            withTimeout(15_000) { repoA.syncPending(uid); repoB.syncPending(uid) }
            val documents = remote.collection("usuarios").document(uid).collection("tareas").get().await()
            assertEquals(2, documents.size())
            assertEquals(today.minusDays(1).toString(), documents.documents.first { it.id == id }.getString("completedOn"))
            assertEquals(today.plusDays(6).toString(), documents.documents.first { it.id == nextTaskId(id) }.getString("dueDate"))
            assertEquals(today.minusDays(1).toString(), storeB.dao.find(uid, id)!!.completedOn)
            assertEquals(today.plusDays(6).toString(), storeB.dao.find(uid, nextTaskId(id))!!.dueDate)
            assertTrue(storeA.dao.writes(uid).isEmpty())
            assertTrue(storeB.dao.writes(uid).isEmpty())
        } finally {
            auth.currentUser?.let { user ->
                remote.collection("usuarios").document(user.uid).collection("tareas").document(id).delete().await()
                remote.collection("usuarios").document(user.uid).collection("tareas").document(nextTaskId(id)).delete().await()
                user.delete().await()
            }
            remote.terminate().await(); Unit /* Firebase Auth retains emulator callbacks until process exit. */; dbA.close(); dbB.close()
        }
    }
}
private class TaskSession(uid: String) : AuthRepository {
    override val sessionUid = MutableStateFlow<String?>(uid)
    override val currentUid get() = sessionUid.value
    override val currentUser = flowOf(AuthUser(uid, null, false))
    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> = error("Not used")
    override suspend fun registerWithEmail(email: String, password: String): Result<Unit> = error("Not used")
    override suspend fun signInWithGoogle(idToken: String): Result<Unit> = error("Not used")
    override suspend fun signOut() { sessionUid.value = null }
}
