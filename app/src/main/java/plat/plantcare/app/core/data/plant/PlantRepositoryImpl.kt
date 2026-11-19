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
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
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
import plat.plantcare.app.core.data.plant.identification.PlantIdentificationApi
import plat.plantcare.app.core.network.NetworkMonitor
import plat.plantcare.app.core.photos.local.LocalImageStorage
import plat.plantcare.app.core.photos.local.isRemoteImage
import plat.plantcare.app.core.photos.remote.OnlineImageStorage
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

class PlantImageException(cause: Throwable) : Exception("No se pudo subir la foto", cause)

@Singleton
class PlantRepositoryImpl @Inject constructor(
    private val session: SessionProvider,
    private val network: NetworkMonitor,
    private val remoteImages: OnlineImageStorage,
    private val localImages: LocalImageStorage,
    private val plantApi: PlantDataSourceApi,
    private val plantCharacteristicsApi: PlantCharacteristicsApi,
    private val plantConsiderationsStorageApi: PlantConsiderationsStorageApi,
    private val plantDao: PlantDAO,
    private val plantCharacteristicsDao: PlantCharacteristicsDAO,
    private val plantConsiderationsDao: PlantConsiderationsDAO,
    private val plantConsiderationsAiApi: PlantConsiderationsAiApi,
    private val plantIdentificationApi: PlantIdentificationApi
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
        val ownCharacteristics = characteristics.copy(plantId = id)

        //Local storage, whether device is online or not
        val localImage = newPlant.imageUrl.localImageOrNull()
        val keptImage = localImage?.let { localImages.keep(it) }
        val plant = Plant(
            id = id,
            ownerId = ownerId,
            name = newPlant.name.trim(),
            imageUrl = keptImage ?: newPlant.imageUrl.takeIf { it.isNotBlank() },
            location = newPlant.location.trim(),
            birthDate = newPlant.birthDate,
            referenceId = null,
            favorite = false
        )
        plantDao.updateOrInsertPlant(plant.toEntity())
        plantCharacteristicsDao.updateOrInsertPlantCharacteristics(ownCharacteristics.toEntity())

        //Online features, they are skipped if device is offline
        if (network.isOnline()) {
            val photo = keptImage?.let { localImages.readForUpload(it) }

            remote("plant", PUSH_TIMEOUT_MS) {
                pushNewPlant(ownerId, plant, ownCharacteristics, photo, keptImage)
            }

            if (photo != null) {
                remote("considerations", AI_TIMEOUT_MS) {
                    addConsiderations(ownerId, id, photo)
                }
            }
        }
        return PlantSaveResult(id, careUnavailable = localImage != null && plantConsiderationsDao.observePlantConsiderations(id).first() == null)
    }

    override suspend fun updatePlant(plant: Plant) {
        val ownerId = requireOwnerId()
        val previousUrl = plantDao.observePlant(plant.id).first()?.toDomain()?.imageUrl

        //Local storage
        val localImage = plant.imageUrl.localImageOrNull()
        val keptImage = localImage?.let { localImages.keep(it) }

        val updated = plant.copy(
            imageUrl = keptImage ?: plant.imageUrl?.takeIf { it.isNotBlank() }
        )
        plantDao.updateOrInsertPlant(updated.toEntity())

        if (previousUrl != null && previousUrl != updated.imageUrl && !previousUrl.isRemoteImage()) {
            localImages.delete(previousUrl)
        }

        //Online storage
        if (network.isOnline()) {
            remote("update plant", PUSH_TIMEOUT_MS) { pushUpdate(ownerId, updated, previousUrl) }
        }
    }

    override suspend fun setFavorite(plantId: String, favorite: Boolean) {
        val ownerId = requireOwnerId()

        plantDao.setPlantFavorite(plantId, favorite)
        if (network.isOnline()) {
            remote("favorite", REMOTE_TIMEOUT_MS) {
                plantApi.setFavorite(ownerId, plantId, favorite)
            }
        }
    }

    //--------Cloud functions---------------
    private suspend fun pushNewPlant(
        ownerId: String,
        plant: Plant,
        characteristics: PlantCharacteristics,
        photo: ByteArray?,
        keptImage: String?
    ) {
        val uploadedUrl = photo?.let { uploadImage(ownerId, plant.id, it) }
        val remotePlant = plant.copy(imageUrl = uploadedUrl ?: plant.imageUrl)
        deleteUploadedPhotoIfFails(uploadedUrl) { plantApi.create(ownerId, remotePlant.toCommonDTO(), characteristics.toCommonDTO()) }

        plantDao.updateOrInsertPlant(remotePlant.toEntity())
        keptImage?.let { localImages.delete(it) }

        remote("characteristics", REMOTE_TIMEOUT_MS) {
            plantCharacteristicsApi.addToPlant(ownerId, plant.id, characteristics.toCommonDTO())
        }
    }

    private suspend fun pushUpdate(ownerId: String, updated: Plant, previousUrl: String?) {
        val localImage = updated.imageUrl.localImageOrNull()
        val uploadedUrl = localImage?.let {
            uploadImage(ownerId, updated.id, localImages.readForUpload(it))
        }
        val remotePlant = updated.copy(imageUrl = uploadedUrl ?: updated.imageUrl)
        deleteUploadedPhotoIfFails(uploadedUrl) { plantApi.update(ownerId, remotePlant.toCommonDTO()) }

        plantDao.updateOrInsertPlant(remotePlant.toEntity())
        localImage?.let { localImages.delete(it) }

        if (previousUrl != null && previousUrl != remotePlant.imageUrl && previousUrl.isRemoteImage()) {
            bestEffort { remoteImages.delete(previousUrl) }
        }
    }

    private suspend fun addConsiderations(ownerId: String, plantId: String, photo: ByteArray) {
        val species = plantIdentificationApi.identify(photo).maxByOrNull { it.score }
        if (species == null || species.score < MIN_SPECIES_SCORE) {
            Log.i(TAG, "Species not recognized with enough confidence, skipping considerations")
            return
        }

        val considerations = plantConsiderationsAiApi.generateConsiderations(
            speciesName = species.scientificName,
            photo = photo
        )

        plantConsiderationsDao.updateOrInsertPlantConsiderations(
            considerations
                .toCommonDTO()
                .copy(
                    plantId = plantId,
                    scientificName = species.scientificName
                )
                .toDomain()
                .toEntity()
        )
        remote("considerations upload", REMOTE_TIMEOUT_MS) {
            plantConsiderationsStorageApi.addToPlant(ownerId, plantId, considerations.toCommonDTO())
        }
    }


    //--------Helper functions---------------
    private suspend fun requireOwnerId(): String =
        session.sessionUid.first()?.takeIf { it.isNotBlank() }
            ?: error("No hay usuario con sesión iniciada")

    private fun String?.localImageOrNull(): String? =
        this?.takeIf { it.isNotBlank() && !it.isRemoteImage() }

    private suspend fun uploadImage(ownerId: String, plantId: String, bytes: ByteArray, version: String = System.currentTimeMillis().toString()): String =
        try {
            remoteImages.upload(
                path = "usuarios/$ownerId/plantas/$plantId-$version.jpg",
                bytes = bytes
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw PlantImageException(e)
        }

    private suspend fun remote(operation: String, timeoutMs: Long, block: suspend () -> Unit): Boolean =
        try {
            withTimeout(timeoutMs.milliseconds) { block() }
            true
        } catch (e: TimeoutCancellationException) {
            Log.w(TAG, "Cloud step timed out, keeping local data: $operation")
            false
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Cloud step failed, keeping local data: $operation", e)
            false
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
        const val REMOTE_TIMEOUT_MS = 10_000L
        const val PUSH_TIMEOUT_MS = 30_000L
        const val AI_TIMEOUT_MS = 30_000L
        const val MIN_SPECIES_SCORE = 0.5f
    }
}