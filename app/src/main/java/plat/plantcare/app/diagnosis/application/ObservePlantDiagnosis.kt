package plat.plantcare.app.diagnosis.application

import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.diagnosis.domain.DiagnosisDetails
import plat.plantcare.app.diagnosis.domain.DiagnosisRepository

class ObservePlantDiagnosis(private val diagnoses: DiagnosisRepository) {
    operator fun invoke(plantId: String): Flow<DiagnosisDetails> = diagnoses.observeDetails(plantId)
}
