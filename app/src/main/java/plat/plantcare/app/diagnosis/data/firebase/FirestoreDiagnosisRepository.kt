package plat.plantcare.app.diagnosis.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import plat.plantcare.app.core.data.firebase.observeSnapshots
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.core.data.plant.domain.Plant
import plat.plantcare.app.core.data.remote.Firebase.PlantCharacteristicsFirebaseDTO
import plat.plantcare.app.diagnosis.domain.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDiagnosisRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AuthRepository
) : DiagnosisRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeDetails(plantId: String): Flow<DiagnosisDetails> = auth.sessionUid.flatMapLatest { uid ->
        if (uid == null) flowOf(DiagnosisDetails()) else {
            val characteristics = firestore.collection("usuarios").document(uid).collection("caracteristicas")
                .document(plantId).observeSnapshots { it.toObject(PlantCharacteristicsFirebaseDTO::class.java)?.toDiagnosisRequirements() }
            val latest = diagnostics(uid, plantId).orderBy("date", Query.Direction.DESCENDING).limit(1)
                .observeSnapshots { it.documents.firstOrNull()?.toObject(DiagnosticFirebaseDTO::class.java)?.toModel() }
            combine(characteristics, latest) { c, d -> DiagnosisDetails(c, d) }
        }
    }

    override suspend fun loadContext(plant: Plant): DiagnosisContext = withTimeout(15_000) {
        val uid = requireNotNull(auth.currentUid) { "Inicia sesión para diagnosticar una planta." }
        require(plant.ownerId == uid) { "Esta planta no pertenece a tu cuenta." }
        val ownerPlant = firestore.collection("usuarios").document(uid).collection("plantas").document(plant.id)
            .get(Source.SERVER).await()
        require(ownerPlant.exists()) { "No se encontró esta planta en tu cuenta. Espera a que termine de sincronizarse." }
        val c = firestore.collection("usuarios").document(uid).collection("caracteristicas").document(plant.id)
            .get(Source.SERVER).await().toObject(PlantCharacteristicsFirebaseDTO::class.java)?.toDiagnosisRequirements()
        val scientificName = plant.referenceId?.takeIf { it.isNotBlank() }?.let {
            firestore.collection("referencias").document(it).get(Source.SERVER).await().getString("scientificName")
        }
        check(auth.currentUid == uid) { "La sesión cambió. Vuelve a abrir el diagnóstico." }
        DiagnosisContext(uid, plant, c, scientificName)
    }

    override suspend fun save(context: DiagnosisContext, diagnostic: Diagnostic) = withTimeout(15_000) {
        check(auth.currentUid == context.uid && diagnostic.plantId == context.plant.id) {
            "La sesión cambió. Vuelve a iniciar el diagnóstico."
        }
        // The draft keeps its ID and timestamp across retries, so a repeated save is idempotent.
        diagnostics(context.uid, diagnostic.plantId).document(diagnostic.id).set(diagnostic.toFirebaseDTO()).await()
        Unit
    }

    private fun diagnostics(uid: String, plantId: String) = firestore.collection("usuarios").document(uid)
        .collection("plantas").document(plantId).collection("diagnosticos")
}
