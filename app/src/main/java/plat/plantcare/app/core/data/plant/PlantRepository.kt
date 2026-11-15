package plat.plantcare.app.core.data.plant

import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantCharacteristics
import plat.plantcare.app.core.data.plant.general.domain.NewPlant
import plat.plantcare.app.core.data.plant.general.domain.Plant

interface PlantRepository {
    fun observePlants(): Flow<List<Plant>>
    fun observePlant(id: String): Flow<Plant?>
    fun observeCharacteristics(plantId: String): Flow<PlantCharacteristics?>

    suspend fun addPlant(newPlant: NewPlant, characteristics: PlantCharacteristics): String

    suspend fun updatePlant(plant: Plant)

    suspend fun setFavorite(plantId: String, favorite: Boolean)
}