package plat.plantcare.app.core.data.plant.characteristics.remote

import kotlinx.coroutines.flow.Flow

interface PlantCharacteristicsApi {
    suspend fun addToPlant(ownerId: String, plantId: String, characteristics: PlantCharacteristicsDTO)
    fun observeCharacteristics(ownerId: String, plantId: String): Flow<PlantCharacteristicsDTO>
    suspend fun getCharacteristics(ownerId: String, plantId: String): PlantCharacteristicsDTO
    suspend fun delete(ownerId: String, plantId: String)
}