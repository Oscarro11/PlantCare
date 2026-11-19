package plat.plantcare.app.diagnosis.application

import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.core.data.plant.PlantRepository
import plat.plantcare.app.core.data.plant.general.domain.Plant

class ObservePlantsForDiagnosis(private val plants: PlantRepository) {
    operator fun invoke(): Flow<List<Plant>> = plants.observePlants()
}
