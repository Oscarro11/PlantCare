package plat.plantcare.app.core.data.plant.general.remote

import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.core.data.plant.general.domain.Plant
import java.io.File

interface PlantIdentificationAPI {
    //suspend fun identify(image: File):
}

interface PlantStorageApi {
    suspend fun upload(ownerId: String, plantId: String, image: File): String
    suspend fun delete(ownerId: String, plantId: String)
}

interface PlantDataSourceApi {
    fun newId(ownerId: String): String

    suspend fun create(ownerId: String, plant: Plant)

    fun observeAll(ownerId: String): Flow<List<Plant>>

    /** Emits null when the plant doesn't exist. */
    fun observe(ownerId: String, plantId: String): Flow<Plant?>

    suspend fun get(ownerId: String, plantId: String): Plant?

    /** Writes only the editable fields; fails if the plant no longer exists. */
    suspend fun update(ownerId: String, plant: Plant)

    suspend fun setFavorite(ownerId: String, plantId: String, favorite: Boolean)

    suspend fun delete(ownerId: String, plantId: String)
}