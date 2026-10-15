package plat.plantcare.app.core.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import plat.plantcare.app.core.data.models.Plant
import javax.inject.Inject
import javax.inject.Singleton

interface PlantRepository {
    fun observePlants(): Flow<List<Plant>>
}

@Singleton
class FakePlantRepository @Inject constructor() : PlantRepository {
    override fun observePlants(): Flow<List<Plant>> = flowOf(emptyList())
}