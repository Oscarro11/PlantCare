package plat.plantcare.app.core.data.plant

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import plat.plantcare.app.authentication.SessionProvider
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantCharacteristics
import plat.plantcare.app.core.data.plant.characteristics.local.PlantCharacteristicsDAO
import plat.plantcare.app.core.data.plant.characteristics.local.toDomain
import plat.plantcare.app.core.data.plant.characteristics.local.toEntity
import plat.plantcare.app.core.data.plant.characteristics.remote.PlantCharacteristicsApi
import plat.plantcare.app.core.data.plant.characteristics.remote.toCommonDTO
import plat.plantcare.app.core.data.plant.general.domain.NewPlant
import plat.plantcare.app.core.data.plant.general.domain.Plant
import plat.plantcare.app.core.data.plant.general.domain.validate
import plat.plantcare.app.core.data.plant.general.local.PlantDAO
import plat.plantcare.app.core.data.plant.general.local.toDomain
import plat.plantcare.app.core.data.plant.general.local.toEntity
import plat.plantcare.app.core.data.plant.general.remote.PlantDataSourceApi
import plat.plantcare.app.core.data.plant.general.remote.toCommonDTO
import plat.plantcare.app.core.photos.local.LocalImageStorage
import plat.plantcare.app.core.photos.local.isRemoteImage
import plat.plantcare.app.core.photos.remote.OnlineImageStorage
import javax.inject.Inject
import javax.inject.Singleton

class PlantImageException(cause: Throwable) : Exception("No se pudo subir la foto", cause)

@Singleton
class PlantRepositoryImpl @Inject constructor(
    private val session: SessionProvider,
    private val remoteImages: OnlineImageStorage,
    private val localImages: LocalImageStorage,
    private val plantApi: PlantDataSourceApi,
    private val plantCharacteristicsApi: PlantCharacteristicsApi,
    private val plantDao: PlantDAO,
    private val plantCharacteristicsDao: PlantCharacteristicsDAO
) : PlantRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observePlants(): Flow<List<Plant>> =
        session.sessionUid.flatMapLatest { uid ->
            if (uid.isNullOrBlank()) {
                flowOf(emptyList())
            } else {
                plantDao.observeUserPlants(uid).map { list -> list.map { it.toDomain() } }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observePlant(id: String): Flow<Plant?> =
        session.sessionUid.flatMapLatest { uid ->
            if (uid.isNullOrBlank()) {
                flowOf(null)
            } else {
                plantDao.observePlant(id).map { entity -> entity?.toDomain() }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCharacteristics(plantId: String): Flow<PlantCharacteristics?> =
        session.sessionUid.flatMapLatest { uid ->
            if (uid.isNullOrBlank()) {
                flowOf(null)
            } else {
                plantCharacteristicsDao.observePlantCharacteristics(plantId)
                    .map { entity -> entity?.toDomain() }
            }
        }

    override suspend fun addPlant(newPlant: NewPlant, characteristics: PlantCharacteristics): String {
        val errors = newPlant.validate()
        require(errors.isEmpty()) { "Planta invalida: $errors" }

        val ownerId = requireOwnerId()
        val id = plantApi.newId(ownerId)

        val localImage = newPlant.imageUrl.localImageOrNull()
        val uploadedUrl = localImage?.let { uploadImage(ownerId, id, it) }

        val plant = Plant(
            id = id,
            ownerId = ownerId,
            name = newPlant.name.trim(),
            imageUrl = uploadedUrl ?: newPlant.imageUrl.takeIf { it.isNotBlank() },
            location = newPlant.location.trim(),
            birthDate = newPlant.birthDate,
            referenceId = null,
            favorite = false
        )

        deleteUploadedPhotoIfFails(uploadedUrl) { plantApi.create(ownerId, plant.toCommonDTO()) }
        plantDao.updateOrInsertPlant(plant.toEntity())

        plantCharacteristicsApi.addToPlant(ownerId, id, characteristics.toCommonDTO())
        plantCharacteristicsDao.updateOrInsertPlantCharacteristics(characteristics.copy( plantId = id ).toEntity())

        localImage?.let { localImages.delete(it) }
        return id
    }

    override suspend fun updatePlant(plant: Plant) {
        val ownerId = requireOwnerId()
        val previousUrl = plantDao.observePlant(plant.id).first()?.toDomain()?.imageUrl

        val localImage = plant.imageUrl.localImageOrNull()
        val uploadedUrl = localImage?.let { uploadImage(ownerId, plant.id, it) }
        val updated = plant.copy(
            imageUrl = uploadedUrl ?: plant.imageUrl?.takeIf { it.isNotBlank() }
        )

        deleteUploadedPhotoIfFails(uploadedUrl) { plantApi.update(ownerId, updated.toCommonDTO()) }
        plantDao.updateOrInsertPlant(updated.toEntity())

        if (previousUrl != null && previousUrl != updated.imageUrl && previousUrl.isRemoteImage()) {
            bestEffort { remoteImages.delete(previousUrl) }
        }
        localImage?.let { localImages.delete(it) }
    }

    override suspend fun setFavorite(plantId: String, favorite: Boolean) {
        val ownerId = requireOwnerId()

        plantDao.togglePlantFavorite(plantId)
        try {
            plantApi.setFavorite(ownerId, plantId, favorite)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            withContext(NonCancellable) { plantDao.togglePlantFavorite(plantId) }
            throw e
        }
    }

    private suspend fun requireOwnerId(): String =
        session.sessionUid.first()?.takeIf { it.isNotBlank() }
            ?: error("No hay usuario con sesión iniciada")

    private fun String?.localImageOrNull(): String? =
        this?.takeIf { it.isNotBlank() && !it.isRemoteImage() }

    private suspend fun uploadImage(ownerId: String, plantId: String, localImage: String): String =
        try {
            val bytes = localImages.readForUpload(localImage)
            remoteImages.upload(
                path = "usuarios/$ownerId/plantas/$plantId-${System.currentTimeMillis()}.jpg",
                bytes = bytes
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw PlantImageException(e)
        }

    private suspend fun deleteUploadedPhotoIfFails(uploadedUrl: String?, block: suspend () -> Unit) {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (uploadedUrl != null) {
                withContext(NonCancellable) { bestEffort { remoteImages.delete(uploadedUrl) } }
            }
            throw e
        }
    }

    private suspend fun bestEffort(block: suspend () -> Unit) {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Cleanup failed, leaving an orphan file", e)
        }
    }

    private companion object {
        const val TAG = "PlantRepository"
    }
}