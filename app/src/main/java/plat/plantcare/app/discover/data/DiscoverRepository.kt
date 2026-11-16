package plat.plantcare.app.discover.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.core.data.remote.Firebase.PlantReferenceConsiderationsFirebaseDTO
import javax.inject.Inject
import javax.inject.Singleton

/** Optional fields keep the existing reference documents compatible. */
data class CatalogPlant(
    @DocumentId val id: String = "",
    val scientificName: String = "",
    val commonName: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val family: String = "",
    val origin: String = "",
    val feature: String = "",
    val light: String = "",
    val watering: String = "",
    val humidity: String = "",
    val petSafety: String = "",
    val petWarning: Boolean = false
) {
    @get:Exclude
    val displayName: String get() = commonName.ifBlank { scientificName.ifBlank { "Planta sin nombre" } }
}

data class CatalogSnapshot(
    val plants: List<CatalogPlant> = emptyList(),
    val wishedIds: Set<String> = emptySet(),
    val care: Map<String, PlantReferenceConsiderationsFirebaseDTO> = emptyMap(),
    val isLoading: Boolean = true,
    val isSignedIn: Boolean = true
)

@Singleton
class DiscoverRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AuthRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeCatalog(): Flow<CatalogSnapshot> = auth.sessionUid.flatMapLatest { uid ->
        if (uid == null) flowOf(CatalogSnapshot(isLoading = false, isSignedIn = false))
        else combine(
            firestore.collection("referencias").listen { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(CatalogPlant::class.java) }
            },
            firestore.collection("usuarios").document(uid).collection("deseadas").listen { snapshot ->
                snapshot.documents.map { it.getString("referenceId") ?: it.id }.toSet()
            },
            firestore.collection("pautasCuidado").listen { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    document.toObject(PlantReferenceConsiderationsFirebaseDTO::class.java)?.let { document.id to it }
                }.toMap()
            }
        ) { plants, wishedIds, care ->
            CatalogSnapshot(plants.sortedBy { it.displayName.lowercase() }, wishedIds, care, isLoading = false)
        }.onStart { emit(CatalogSnapshot()) }
    }

    suspend fun setWished(referenceId: String, wished: Boolean) {
        val uid = requireNotNull(auth.currentUid) { "Inicia sesión para guardar plantas." }
        require(referenceId.isNotBlank())
        val document = firestore.collection("usuarios").document(uid).collection("deseadas").document(referenceId)
        if (wished) document.set(mapOf(
            "userId" to uid, "referenceId" to referenceId, "updatedAt" to FieldValue.serverTimestamp()
        )).await()
        else document.delete().await()
    }
}

private fun <T> Query.listen(map: (QuerySnapshot) -> T): Flow<T> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) close(error)
        else if (snapshot != null) {
            try { trySend(map(snapshot)) } catch (exception: Exception) { close(exception) }
        }
    }
    awaitClose { registration.remove() }
}
