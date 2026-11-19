package plat.plantcare.app.diagnosis.data.gemini

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import plat.plantcare.app.diagnosis.domain.DiagnosisAnswer

@Serializable
internal data class GeminiDiagnosisResponse(
    val isPlant: Boolean,
    val imageUsable: Boolean,
    val assessment: String,
    val summary: String,
    val foliageState: String,
    val growthVigor: String,
    val pestControl: String,
    val diseaseSigns: String,
    val hasFlowersOrFruits: Boolean?,
    val possibleCauses: List<String>,
    val recommendations: List<String>,
    val limitations: List<String>
) {
    fun toDomain() = DiagnosisAnswer(isPlant, imageUsable, assessment, summary, foliageState, growthVigor, pestControl, diseaseSigns, hasFlowersOrFruits, possibleCauses, recommendations, limitations).validate()

    companion object {
        fun parse(text: String): DiagnosisAnswer {
            require(text.length <= 30_000) { "La respuesta del análisis es demasiado extensa." }
            val response = try { Json.decodeFromString<GeminiDiagnosisResponse>(text) }
            catch (e: SerializationException) { throw IllegalArgumentException("La respuesta del análisis no tiene el formato esperado. Inténtalo de nuevo.", e) }
            return response.toDomain()
        }
    }
}
