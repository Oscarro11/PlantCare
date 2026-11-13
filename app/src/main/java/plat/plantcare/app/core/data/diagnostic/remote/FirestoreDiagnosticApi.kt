package plat.plantcare.app.core.data.diagnostic.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import plat.plantcare.app.core.data.diagnostic.domain.Diagnostic
import plat.plantcare.app.core.data.snapshotFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDiagnosticApi @Inject constructor(
    private val firestore: FirebaseFirestore
) : DiagnosticApi {

    private fun collection(uid: String, plantId: String) =
        firestore.collection("usuarios")
            .document(uid)
            .collection("plantas")
            .document(plantId)
            .collection("diagnosticos")

    private fun historyQuery(uid: String, plantId: String) =
        collection(uid, plantId)
            .orderBy("createdAt", Query.Direction.DESCENDING)

    override fun newId(uid: String, plantId: String): String =
        collection(uid, plantId).document().id

    override fun observeByPlant(uid: String, plantId: String): Flow<List<DiagnosticDTO>> =
        historyQuery(uid, plantId).snapshotFlow()

    override suspend fun getByPlant(uid: String, plantId: String): List<DiagnosticDTO> =
        historyQuery(uid, plantId).get().await()
            .toObjects(DiagnosticFirebaseDTO::class.java)
            .mapNotNull { it.toCommonDTO() }

    override suspend fun get(uid: String, plantId: String, id: String): DiagnosticDTO? =
        collection(uid, plantId).document(id).get().await()
            .toObject(DiagnosticFirebaseDTO::class.java)
            ?.toCommonDTO()

    override suspend fun create(uid: String, plantId: String, diagnostic: DiagnosticDTO) {
        collection(uid, plantId).document(diagnostic.id).set(diagnostic).await()
    }

    override suspend fun delete(uid: String, plantId: String, id: String) {
        collection(uid, plantId).document(id).delete().await()
    }

    override suspend fun deleteAll(uid: String, plantId: String, ids: List<String>) {
        ids.chunked(500).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { batch.delete(collection(uid, plantId).document(it)) }
            batch.commit().await()
        }
    }
}
