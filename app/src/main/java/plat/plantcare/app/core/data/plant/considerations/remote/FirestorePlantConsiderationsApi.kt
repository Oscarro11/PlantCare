package plat.plantcare.app.core.data.plant.considerations.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import plat.plantcare.app.core.data.documentFlow
import plat.plantcare.app.core.data.models.awaitOrQueued
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestorePlantConsiderationsApi @Inject constructor(
    private val firestore: FirebaseFirestore
) : PlantConsiderationsStorageApi {

    private fun considerationsReference(ownerId: String, plantId: String) =
        firestore
            .collection("usuarios")
            .document(ownerId)
            .collection("plantas")
            .document(plantId)
            .collection("consideraciones")
            .document("principal")

    override suspend fun addToPlant(
        ownerId: String,
        plantId: String,
        considerations: PlantConsiderationsDTO
    ) {
        val dto = considerations.toFirebaseDTO()

        considerationsReference(ownerId, plantId)
            .set(dto)
            .await()
    }

    override fun observeConsiderations(
        ownerId: String,
        plantId: String
    ): Flow<PlantConsiderationsDTO> =
        considerationsReference(ownerId, plantId)
            .documentFlow<PlantConsiderationsFirebaseDTO>()
            .map { it!!.toCommonDTO() }

    override suspend fun getCharacteristics(
        ownerId: String,
        plantId: String
    ): PlantConsiderationsDTO {
        val firebaseDTO = considerationsReference(ownerId, plantId)
            .get().await()
            .toObject(PlantConsiderationsFirebaseDTO::class.java)
            ?: throw NoSuchElementException("Plant considerations not found")

        return firebaseDTO.toCommonDTO()
    }

    override suspend fun delete(ownerId: String, plantId: String) {
        considerationsReference(ownerId, plantId).delete().awaitOrQueued()
    }
}