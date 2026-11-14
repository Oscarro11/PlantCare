package plat.plantcare.app.core.data.plant

import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.core.data.plant.domain.NewPlant
import plat.plantcare.app.core.data.plant.domain.Plant

interface PlantRepository {
    fun observePlants(): Flow<List<Plant>>
    fun observePlant(id: String): Flow<Plant?>

    suspend fun addPlant(newPlant: NewPlant): String

    suspend fun updatePlant(plant: Plant)

    suspend fun setFavorite(plantId: String, favorite: Boolean)
}