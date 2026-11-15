package plat.plantcare.app.core.data.plant.general.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import plat.plantcare.app.core.data.documentFlow
import plat.plantcare.app.core.data.models.awaitOrQueued
import plat.plantcare.app.core.data.plant.general.domain.Plant
import plat.plantcare.app.core.data.snapshotFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestorePlantApi @Inject constructor(
    private val firestore: FirebaseFirestore
) : PlantDataSourceApi {

    private fun plantReference(ownerId: String) =
        firestore
            .collection("usuarios")
            .document(ownerId)
            .collection("plantas")

    override fun newId(ownerId: String): String = plantReference(ownerId).document().id

    override suspend fun create(
        ownerId: String,
        plant: PlantDTO
    ) {
        val dto = plant.toFirebaseDTO()

        if (dto != null) {
            plantReference(ownerId)
                .document(plant.id)
                .set(dto).awaitOrQueued()
        }
    }

    override fun observeAll(ownerId: String): Flow<List<PlantDTO>> =
        plantReference(ownerId)
            .snapshotFlow<PlantFirebaseDTO>()
            .map { dtos -> dtos.map { it.toCommonDTO() } }

    override fun observe(
        ownerId: String,
        plantId: String
    ): Flow<PlantDTO?> =
        plantReference(ownerId).document(plantId)
            .documentFlow<PlantFirebaseDTO>()
            .map { it?.toCommonDTO() }

    override suspend fun get(
        ownerId: String,
        plantId: String
    ): PlantDTO? =
        plantReference(ownerId)
            .document(plantId)
            .get().await()
            .toObject(PlantFirebaseDTO::class.java)
            ?.toCommonDTO()

    override suspend fun update(
        ownerId: String,
        plant: PlantDTO
    ) {
        val dto = plant.toFirebaseDTO()

        if (dto != null) {
            plantReference(ownerId).document(plant.id).update(
                mapOf(
                    "name" to dto.name,
                    "image" to dto.image,
                    "location" to dto.location,
                    "birthDate" to dto.birthDate,
                    "referenceId" to dto.referenceId,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).awaitOrQueued()
        }
    }

    override suspend fun setFavorite(
        ownerId: String,
        plantId: String,
        favorite: Boolean
    ) {
        plantReference(ownerId).document(plantId).update(
            mapOf(
                "favorite" to favorite,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).awaitOrQueued()
    }

    override suspend fun delete(ownerId: String, plantId: String) {
        plantReference(ownerId).document(plantId).delete().awaitOrQueued()
    }
}