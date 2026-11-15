package plat.plantcare.app.core.data.plant.general.remote

import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.core.data.plant.general.domain.Plant
import java.io.File

interface PlantIdentificationAPI {
    //suspend fun identify(image: File):
}

interface PlantDataSourceApi {
    fun newId(ownerId: String): String

    suspend fun create(ownerId: String, plant: PlantDTO)

    fun observeAll(ownerId: String): Flow<List<PlantDTO>>

    /** Emits null when the plant doesn't exist. */
    fun observe(ownerId: String, plantId: String): Flow<PlantDTO?>

    suspend fun get(ownerId: String, plantId: String): PlantDTO?

    /** Writes only the editable fields; fails if the plant no longer exists. */
    suspend fun update(ownerId: String, plant: PlantDTO)

    suspend fun setFavorite(ownerId: String, plantId: String, favorite: Boolean)

    suspend fun delete(ownerId: String, plantId: String)
}