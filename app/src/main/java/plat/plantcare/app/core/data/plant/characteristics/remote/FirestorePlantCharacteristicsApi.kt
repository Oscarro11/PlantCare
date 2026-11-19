package plat.plantcare.app.core.data.plant.characteristics.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import plat.plantcare.app.core.data.documentFlow
import plat.plantcare.app.core.data.models.awaitOrQueued
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestorePlantCharacteristicsApi @Inject constructor(
    private val firestore: FirebaseFirestore
) : PlantCharacteristicsApi {

    private fun characteristicsReference(ownerId: String, plantId: String) =
        firestore
            .collection("usuarios")
            .document(ownerId)
            .collection("plantas")
            .document(plantId)
            .collection("caracteristicas")
            .document("principal")

    override suspend fun addToPlant(
        ownerId: String,
        plantId: String,
        characteristics: PlantCharacteristicsDTO
    ) {
        val dto = characteristics.toFirebaseDTO()

        characteristicsReference(ownerId, plantId)
            .set(dto)
            .await()
    }

    override fun observeCharacteristics(
        ownerId: String,
        plantId: String
    ): Flow<PlantCharacteristicsDTO?> =
        characteristicsReference(ownerId, plantId)
            .documentFlow<PlantCharacteristicsFirebaseDTO>()
            .map { it?.toCommonDTO() }

    override suspend fun getCharacteristics(
        ownerId: String,
        plantId: String
    ): PlantCharacteristicsDTO {
        val firebaseDTO = characteristicsReference(ownerId, plantId)
            .get().await()
            .toObject(PlantCharacteristicsFirebaseDTO::class.java)
            ?: throw NoSuchElementException("Plant characteristics not found")

        return firebaseDTO.toCommonDTO()
    }

    override suspend fun delete(ownerId: String, plantId: String) {
        characteristicsReference(ownerId, plantId).delete().awaitOrQueued()
    }
}