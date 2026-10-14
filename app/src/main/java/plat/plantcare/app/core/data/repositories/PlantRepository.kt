package plat.plantcare.app.core.data.repositories

import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.core.data.models.Plant

interface PlantRepository {
    fun observePlants(): Flow<List<Plant>>
}