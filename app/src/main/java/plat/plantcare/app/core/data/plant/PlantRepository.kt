package plat.plantcare.app.core.data.plant

import plat.plantcare.app.core.data.plant.general.domain.PlantSaveResult
import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantCharacteristics
import plat.plantcare.app.core.data.plant.considerations.domain.PlantConsiderations
import plat.plantcare.app.core.data.plant.general.domain.NewPlant
import plat.plantcare.app.core.data.plant.general.domain.Plant

interface PlantRepository {
    fun observePlants(): Flow<List<Plant>>
    fun observePlant(id: String): Flow<Plant?>
    fun observeCharacteristics(plantId: String): Flow<PlantCharacteristics?>
    fun observeConsiderations(plantId: String): Flow<PlantConsiderations?>

    suspend fun addPlant(newPlant: NewPlant, characteristics: PlantCharacteristics, plantId: String? = null): PlantSaveResult

    suspend fun updatePlant(plant: Plant)

    suspend fun deletePlant(plantId: String) { error("Borrado no disponible") }
    suspend fun regenerateConsiderations(plantId: String) { error("Generación no disponible") }

    suspend fun setFavorite(plantId: String, favorite: Boolean)
}