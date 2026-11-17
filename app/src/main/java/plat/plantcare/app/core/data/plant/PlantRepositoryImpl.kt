package plat.plantcare.app.core.data.plant

import android.util.Log
import plat.plantcare.app.core.data.plant.general.domain.PlantSaveResult
import plat.plantcare.app.core.data.plant.general.remote.toDomain
import plat.plantcare.app.core.data.plant.characteristics.remote.toDomain
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
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
import plat.plantcare.app.core.data.plant.considerations.domain.PlantConsiderations
import plat.plantcare.app.core.data.plant.considerations.local.PlantConsiderationsDAO
import plat.plantcare.app.core.data.plant.considerations.local.toDomain
import plat.plantcare.app.core.data.plant.considerations.local.toEntity
import plat.plantcare.app.core.data.plant.considerations.remote.PlantConsiderationsAiApi
import plat.plantcare.app.core.data.plant.considerations.remote.PlantConsiderationsStorageApi
import plat.plantcare.app.core.data.plant.considerations.remote.toCommonDTO
import plat.plantcare.app.core.data.plant.considerations.remote.toDomain
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
    private val plantConsiderationsStorageApi: PlantConsiderationsStorageApi,
    private val plantDao: PlantDAO,
    private val plantCharacteristicsDao: PlantCharacteristicsDAO,
    private val plantConsiderationsDao: PlantConsiderationsDAO,
    private val plantConsiderationsAiApi: PlantConsiderationsAiApi
) : PlantRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observePlants(): Flow<List<Plant>> =
        session.sessionUid.flatMapLatest { uid ->
            if (uid.isNullOrBlank()) {
                flowOf(emptyList())
            } else {
                syncedCache(
                    local = plantDao.observeUserPlants(uid).map { list -> list.map { it.toDomain() } },
                    remote = plantApi.observeAll(uid)
                ) { plants ->
                    plants.forEach { plant -> plantDao.updateOrInsertPlant(plant.toDomain().copy(ownerId = uid).toEntity()) }
                }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observePlant(id: String): Flow<Plant?> =
        session.sessionUid.flatMapLatest { uid ->
            if (uid.isNullOrBlank()) {
                flowOf(null)
            } else {
                syncedCache(
                    local = plantDao.observePlant(id).map { entity -> entity?.takeIf { it.ownerId == uid }?.toDomain() },
                    remote = plantApi.observe(uid, id)
                ) { plant -> plant?.let { plantDao.updateOrInsertPlant(it.toDomain().copy(ownerId = uid).toEntity()) } }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCharacteristics(plantId: String): Flow<PlantCharacteristics?> =
        session.sessionUid.flatMapLatest { uid ->
            if (uid.isNullOrBlank()) {
                flowOf(null)
            } else {
                syncedCache(
                    local = combine(plantDao.observePlant(plantId), plantCharacteristicsDao.observePlantCharacteristics(plantId)) {
                        plant, details -> details?.takeIf { plant?.ownerId == uid }?.toDomain()
                    },
                    remote = plantCharacteristicsApi.observeCharacteristics(uid, plantId)
                ) { details -> details?.let {
                    plantCharacteristicsDao.updateOrInsertPlantCharacteristics(it.toDomain().copy(plantId = plantId).toEntity())
                } }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeConsiderations(plantId: String): Flow<PlantConsiderations?> =
        session.sessionUid.flatMapLatest { uid ->
            if (uid.isNullOrBlank()) {
                flowOf(null)
            } else {
                syncedCache(
                    local = combine(plantDao.observePlant(plantId), plantConsiderationsDao.observePlantConsiderations(plantId)) {
                        plant, details -> details?.takeIf { plant?.ownerId == uid }?.toDomain()
                    },
                    remote = plantConsiderationsStorageApi.observeConsiderations(uid, plantId)
                ) { details -> details?.let {
                    plantConsiderationsDao.updateOrInsertPlantConsiderations(it.toDomain().copy(plantId = plantId).toEntity())
                } }
            }
        }

    override suspend fun addPlant(newPlant: NewPlant, characteristics: PlantCharacteristics, plantId: String?): PlantSaveResult {
        val errors = newPlant.validate()
        require(errors.isEmpty()) { "Planta invalida: $errors" }

        val ownerId = requireOwnerId()
        val id = plantId ?: plantApi.newId(ownerId)
        require(id.isNotBlank() && !id.contains("/"))

        val localImage = newPlant.imageUrl.localImageOrNull()
        val uploadedUrl = localImage?.let { uploadImage(ownerId, id, it, version = "registration") }

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

        plantApi.create(ownerId, plant.toCommonDTO(), characteristics.copy(plantId = id).toCommonDTO())
        plantDao.updateOrInsertPlant(plant.toEntity())

        plantCharacteristicsDao.updateOrInsertPlantCharacteristics(characteristics.copy( plantId = id ).toEntity())

        var careUnavailable = false
        if (localImage != null) {
            try {
                val considerations = plantConsiderationsAiApi.generateConsiderations(
                    speciesName = null, photo = localImages.readForUpload(localImage)
                ).toCommonDTO().copy(plantId = id)
                plantConsiderationsStorageApi.addToPlant(ownerId, id, considerations)
                plantConsiderationsDao.updateOrInsertPlantConsiderations(considerations.toDomain().toEntity())
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                // The acknowledged registration must remain a success when optional AI is unavailable.
                careUnavailable = true
                Log.w(TAG, "Planta guardada; no se pudieron generar las pautas de cuidado.", error)
            }
        }
        localImage?.let { bestEffort { localImages.delete(it) } }
        return PlantSaveResult(id, careUnavailable)
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

    private suspend fun uploadImage(ownerId: String, plantId: String, localImage: String, version: String = System.currentTimeMillis().toString()): String =
        try {
            val bytes = localImages.readForUpload(localImage)
            remoteImages.upload(
                path = "usuarios/$ownerId/plantas/$plantId-$version.jpg",
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

    private fun <T, R> syncedCache(local: Flow<T>, remote: Flow<R>, persist: suspend (R) -> Unit): Flow<T> = channelFlow {
        launch { local.collect { send(it) } }
        launch {
            remote.onEach { persist(it) }
                .catch { Log.w(TAG, "No se pudo actualizar la colección; se conservan los datos locales.", it) }
                .collect()
        }
    }

    private companion object {
        const val TAG = "PlantRepository"
    }
}