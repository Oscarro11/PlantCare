package plat.plantcare.app.diagnosis.application

import plat.plantcare.app.diagnosis.domain.DiagnosisDraft
import plat.plantcare.app.diagnosis.domain.DiagnosisRepository

class SaveDiagnosis(private val diagnoses: DiagnosisRepository) {
    suspend operator fun invoke(draft: DiagnosisDraft) {
        require(draft.diagnostic.plantId == draft.context.plant.id) { "El diagnóstico no corresponde a esta planta." }
        diagnoses.save(draft.context, draft.diagnostic)
    }
}
