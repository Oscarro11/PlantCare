package plat.plantcare.app.diagnosis.domain

/** The analysis use case does not depend on a particular AI provider. */
interface PlantAnalyzer {
    suspend fun analyze(jpeg: ByteArray, context: DiagnosisContext): AnalyzedPlant
}
