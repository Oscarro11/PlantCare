package plat.plantcare.app.diagnosis.application

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import plat.plantcare.app.core.data.plant.PlantRepository
import plat.plantcare.app.diagnosis.domain.*
import plat.plantcare.app.diagnosis.data.firebase.FirestoreDiagnosisRepository
import plat.plantcare.app.diagnosis.data.gemini.GeminiPlantAnalyzer
import plat.plantcare.app.diagnosis.data.photos.LocalDiagnosisPhotos

@Module
@InstallIn(SingletonComponent::class)
abstract class DiagnosisModule {
    @Binds @Singleton abstract fun repository(implementation: FirestoreDiagnosisRepository): DiagnosisRepository
    @Binds @Singleton abstract fun analyzer(implementation: GeminiPlantAnalyzer): PlantAnalyzer
    @Binds @Singleton abstract fun photos(implementation: LocalDiagnosisPhotos): DiagnosisPhotos

    companion object {
        @Provides fun analyzePlant(repository: DiagnosisRepository, analyzer: PlantAnalyzer, photos: DiagnosisPhotos) = AnalyzePlant(repository, analyzer, photos)
        @Provides fun saveDiagnosis(repository: DiagnosisRepository) = SaveDiagnosis(repository)
        @Provides fun observeDiagnosis(repository: DiagnosisRepository) = ObservePlantDiagnosis(repository)
        @Provides fun choosePlant(repository: PlantRepository) = ObservePlantsForDiagnosis(repository)
    }
}
