package plat.plantcare.app.diagnosis.application

import plat.plantcare.app.core.data.plant.domain.Plant
import plat.plantcare.app.diagnosis.domain.DiagnosisDraft
import plat.plantcare.app.diagnosis.domain.DiagnosisPhotos
import plat.plantcare.app.diagnosis.domain.DiagnosisRepository
import plat.plantcare.app.diagnosis.domain.PlantAnalyzer

class AnalyzePlant(
    private val diagnoses: DiagnosisRepository,
    private val analyzer: PlantAnalyzer,
    private val photos: DiagnosisPhotos
) {
    suspend operator fun invoke(plant: Plant, photo: String): DiagnosisDraft {
        val context = diagnoses.loadContext(plant)
        require(context.plant.id == plant.id && context.uid == plant.ownerId) {
            "El contexto no corresponde a esta planta."
        }
        val analysis = analyzer.analyze(photos.read(photo), context)
        return DiagnosisDraft(context, analysis.answer.toDiagnostic(plant.id, analysis.model))
    }
}
