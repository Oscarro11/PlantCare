package plat.plantcare.app.diagnosis

import plat.plantcare.app.diagnosis.domain.*
import plat.plantcare.app.diagnosis.data.firebase.FirestoreDiagnosisRepository
import plat.plantcare.app.diagnosis.data.gemini.toGeminiPrompt

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.authentication.domain.AuthUser
import plat.plantcare.app.core.data.plant.general.domain.Plant
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

/** Tests local Firestore, not the external Gemini service. No image leaves the device. */
@RunWith(AndroidJUnit4::class)
class DiagnosisRepositoryInstrumentedTest {
    @Test fun savesLatestIdempotentlyAndOtherUsersCannotReadIt() = runBlocking {
        withTimeout(45_000.milliseconds) {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val options = FirebaseOptions.Builder().setProjectId("plantcare-3a58c")
                .setApplicationId("1:123:android:diagnosis-test").setApiKey("fake-api-key").build()
            val app = FirebaseApp.initializeApp(context, options, "diagnosis-test-" + UUID.randomUUID())
            val otherApp = FirebaseApp.initializeApp(context, options, "diagnosis-other-" + UUID.randomUUID())
            val auth = FirebaseAuth.getInstance(app).apply { useEmulator("127.0.0.1", 9099) }
            val otherAuth = FirebaseAuth.getInstance(otherApp).apply { useEmulator("127.0.0.1", 9099) }
            val firestore = FirebaseFirestore.getInstance(app).apply { useEmulator("127.0.0.1", 8080) }
            val otherStore = FirebaseFirestore.getInstance(otherApp).apply { useEmulator("127.0.0.1", 8080) }
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
            var plantPath: String? = null
            var characteristicPath: String? = null
            val written = mutableListOf<String>()
            try {
                val user = auth.signInAnonymously().await().user!!
                val session = DiagnosisTestSession(user.uid)
                val id = "diagnosis-test-" + UUID.randomUUID()
                plantPath = "usuarios/${user.uid}/plantas/$id"
                characteristicPath = "usuarios/${user.uid}/caracteristicas/$id"
                firestore.document(plantPath).set(mapOf("name" to "Planta de prueba", "favorite" to false)).await()
                firestore.document(characteristicPath).set(mapOf("requiredLight" to "Media indirecta", "wateringFrequencyDays" to 7,
                    "humidityLevel" to 55, "preferredClimate" to "Tropical", "pot" to "Con drenaje", "substrate" to "Registrado")).await()
                val repository = FirestoreDiagnosisRepository(firestore, session)
                val plant = Plant(id, "Planta de prueba", null, "Sala", null, user.uid, null)
                val data = repository.loadContext(plant)
                assertEquals(7, data.characteristics?.wateringFrequencyDays)
                assertTrue(data.toGeminiPrompt().contains("Media indirecta"))
                val answer = DiagnosisAnswer(true, true, "NO_EVALUABLE", "Resultado exclusivo de prueba",
                    "Follaje visible", "Crecimiento no evaluable", "Sin evidencia suficiente", "No concluyente", null,
                    emptyList(), listOf("Comprobar sustrato"), listOf("No se observan las raíces"))
                val first = answer.toDiagnostic(id, "test-model")
                written += "$plantPath/diagnosticos/${first.id}"
                repository.save(data, first)
                repository.save(data, first)
                assertEquals(1, firestore.collection("$plantPath/diagnosticos").get(Source.SERVER).await().size())
                val second = answer.toDiagnostic(id, "test-model").copy(analyzedAtEpochMillis = first.analyzedAtEpochMillis + 2000)
                written += "$plantPath/diagnosticos/${second.id}"
                repository.save(data, second)
                val stream = repository.observeDetails(id).stateIn(scope, SharingStarted.Eagerly, DiagnosisDetails())
                val latest = stream.first { it.latest?.id == second.id }
                assertEquals("Tropical", latest.characteristics?.preferredClimate)
                assertNull(latest.latest?.score)
                otherAuth.signInAnonymously().await()
                try {
                    otherStore.document(written.first()).get(Source.SERVER).await()
                    fail("Another account must not read the diagnosis")
                } catch (e: FirebaseFirestoreException) { assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code) }
            } finally {
                scope.cancel()
                for (doc in written) firestore.document(doc).delete().await()
                characteristicPath?.let { firestore.document(it).delete().await() }
                plantPath?.let { firestore.document(it).delete().await() }
                auth.currentUser?.delete()?.await()
                otherAuth.currentUser?.delete()?.await()
                firestore.terminate().await()
                otherStore.terminate().await()
                Unit /* Firebase Auth retains emulator callbacks until process exit. */
                Unit
            }
        }
    }
}

private class DiagnosisTestSession(uid: String) : AuthRepository {
    override val sessionUid = MutableStateFlow<String?>(uid)
    override val currentUid get() = sessionUid.value
    override val currentUser = sessionUid.map { it?.let { id -> AuthUser(id, null, false) } }
    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> = error("Unused")
    override suspend fun registerWithEmail(email: String, password: String): Result<Unit> = error("Unused")
    override suspend fun signInWithGoogle(idToken: String): Result<Unit> = error("Unused")
    override suspend fun signOut() { sessionUid.value = null }
}
