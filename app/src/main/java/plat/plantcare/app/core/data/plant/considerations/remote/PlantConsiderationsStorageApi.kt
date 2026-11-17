package plat.plantcare.app.core.data.plant.considerations.remote

import kotlinx.coroutines.flow.Flow

interface PlantConsiderationsStorageApi {
    suspend fun addToPlant(ownerId: String, plantId: String, considerations: PlantConsiderationsDTO)
    fun observeConsiderations(ownerId: String, plantId: String): Flow<PlantConsiderationsDTO?>
    suspend fun getCharacteristics(ownerId: String, plantId: String): PlantConsiderationsDTO
    suspend fun delete(ownerId: String, plantId: String)
}