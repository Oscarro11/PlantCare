package plat.plantcare.app.discover

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.authentication.domain.AuthUser
import plat.plantcare.app.discover.data.CatalogSnapshot
import plat.plantcare.app.discover.data.DiscoverRepository
import java.util.UUID

/** Requires local Auth (9099) and Firestore (Android 8080 -> host 8081). */
@RunWith(AndroidJUnit4::class)
class DiscoverRepositoryInstrumentedTest {
    @Test
    fun wishesPersistAndSessionChangesClearThePreviousUser() = runBlocking {
        val app = FirebaseApp.initializeApp(
            InstrumentationRegistry.getInstrumentation().targetContext,
            FirebaseOptions.Builder().setProjectId("plantcare-3a58c")
                .setApplicationId("1:123:android:discover-test").setApiKey("fake-api-key").build(),
            "discover-test-" + UUID.randomUUID()
        )
        val firebaseAuth = FirebaseAuth.getInstance(app).apply { useEmulator("127.0.0.1", 9099) }
        val firestore = FirebaseFirestore.getInstance(app).apply { useEmulator("127.0.0.1", 8080) }
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val session = TestSession()
        val referenceId = "test-wish-" + UUID.randomUUID()
        try {
            val user = withTimeout(15_000) { firebaseAuth.signInAnonymously().await().user!! }
            session.sessionUid.value = user.uid
            val repository = DiscoverRepository(firestore, session)
            val states = repository.observeCatalog().stateIn(scope, SharingStarted.Eagerly, CatalogSnapshot())
            withTimeout(15_000) { states.first { !it.isLoading } }
            assertFalse(states.value.wishedIds.contains(referenceId))
            repeat(3) {
                withTimeout(15_000) { repository.setWished(referenceId, true) }
                withTimeout(15_000) { states.first { referenceId in it.wishedIds } }
                val document = firestore.collection("usuarios").document(user.uid)
                    .collection("deseadas").document(referenceId).get().await()
                assertEquals(user.uid, document.getString("userId"))
                assertEquals(referenceId, document.getString("referenceId"))
                assertTrue(document.getTimestamp("updatedAt") != null)
                withTimeout(15_000) { repository.setWished(referenceId, false) }
                withTimeout(15_000) { states.first { referenceId !in it.wishedIds } }
            }
            session.sessionUid.value = null
            withTimeout(15_000) { states.first { !it.isSignedIn } }
            assertTrue(states.value.plants.isEmpty())
            assertTrue(states.value.wishedIds.isEmpty())
        } finally {
            scope.cancel()
            firebaseAuth.currentUser?.let { user ->
                firestore.collection("usuarios").document(user.uid).collection("deseadas").document(referenceId).delete().await()
                user.delete().await()
            }
            firestore.terminate().await()
            app.delete()
        }
    }
}

private class TestSession : AuthRepository {
    override val sessionUid = MutableStateFlow<String?>(null)
    override val currentUid: String? get() = sessionUid.value
    override val currentUser = sessionUid.map { uid -> uid?.let { AuthUser(it, null, false) } }
    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> = error("Not used")
    override suspend fun registerWithEmail(email: String, password: String): Result<Unit> = error("Not used")
    override suspend fun signInWithGoogle(idToken: String): Result<Unit> = error("Not used")
    override suspend fun signOut() { sessionUid.value = null }
}
