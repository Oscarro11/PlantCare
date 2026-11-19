package plat.plantcare.app.diagnosis.data.gemini

import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.withTimeout
import plat.plantcare.app.core.ai.gemini.GeminiClient
import plat.plantcare.app.BuildConfig
import plat.plantcare.app.diagnosis.domain.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiPlantAnalyzer @Inject constructor(private val gemini: GeminiClient) : PlantAnalyzer {
    private val model by lazy {
        gemini.model(
            name = BuildConfig.GEMINI_MODEL,
            instruction = DIAGNOSIS_SYSTEM_INSTRUCTION,
            config = generationConfig {
                temperature = 0.2f
                maxOutputTokens = 4096
                responseMimeType = "application/json"
                responseSchema = Schema.obj(mapOf(
                    "isPlant" to Schema.boolean(),
                    "imageUsable" to Schema.boolean(),
                    "assessment" to Schema.enumeration(DiagnosisAnswer.ASSESSMENTS),
                    "summary" to Schema.string(),
                    "foliageState" to Schema.string(),
                    "growthVigor" to Schema.string(),
                    "pestControl" to Schema.string(),
                    "diseaseSigns" to Schema.string(),
                    "hasFlowersOrFruits" to Schema.boolean(nullable = true),
                    "possibleCauses" to Schema.array(Schema.string()),
                    "recommendations" to Schema.array(Schema.string()),
                    "limitations" to Schema.array(Schema.string())
                ))
            }
        )
    }

    override suspend fun analyze(jpeg: ByteArray, context: DiagnosisContext): AnalyzedPlant = withTimeout(90_000) {
        val text = gemini.generateImageText(
            model = model,
            image = jpeg,
            mimeType = "image/jpeg",
            prompt = "Observa la planta de esta foto usando este contexto registrado (null = dato desconocido):\n${context.toGeminiPrompt()}"
        )
        val answer = GeminiDiagnosisResponse.parse(text)
        AnalyzedPlant(answer, BuildConfig.GEMINI_MODEL)
    }

}
