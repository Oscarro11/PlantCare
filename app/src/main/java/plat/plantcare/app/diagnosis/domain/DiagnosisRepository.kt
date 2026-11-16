package plat.plantcare.app.diagnosis.domain

import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.core.data.plant.domain.Plant

interface DiagnosisRepository {
    fun observeDetails(plantId: String): Flow<DiagnosisDetails>
    suspend fun loadContext(plant: Plant): DiagnosisContext
    suspend fun save(context: DiagnosisContext, diagnostic: Diagnostic)
}
