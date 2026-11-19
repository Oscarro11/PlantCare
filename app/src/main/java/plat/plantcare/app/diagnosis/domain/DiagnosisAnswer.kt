package plat.plantcare.app.diagnosis.domain

import java.time.LocalDate
import java.util.UUID

data class DiagnosisAnswer(
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
    fun validate(): DiagnosisAnswer {
        require(isPlant) { "La imagen no permite identificar una planta. Elige otra foto." }
        require(imageUsable) { "La foto no tiene suficiente detalle. Toma otra con buena luz y enfoque." }
        require(assessment in ASSESSMENTS) { "El análisis recibido no es válido. Inténtalo nuevamente." }
        listOf(summary, foliageState, growthVigor, pestControl, diseaseSigns).forEach {
            require(it.isNotBlank() && it.length <= 2000) { "El análisis recibido está incompleto." }
        }
        listOf(possibleCauses, recommendations, limitations).forEach { items ->
            require(items.size <= 8 && items.all { it.isNotBlank() && it.length <= 1000 }) {
                "El análisis recibido no tiene el formato esperado."
            }
        }
        require(recommendations.isNotEmpty() && limitations.isNotEmpty()) { "El análisis recibido está incompleto." }
        return this
    }

    fun toDiagnostic(plantId: String, model: String): Diagnostic {
        validate()
        return Diagnostic(
            id = UUID.randomUUID().toString(), plantId = plantId, date = LocalDate.now(),
            foliageState = foliageState, growthVigor = growthVigor, pestControl = pestControl,
            diseaseSigns = diseaseSigns, hasFlowersOrFruits = hasFlowersOrFruits, score = null,
            assessment = assessment, summary = summary, possibleCauses = possibleCauses,
            recommendations = recommendations, limitations = limitations, model = model,
            analyzedAtEpochMillis = System.currentTimeMillis()
        )
    }

    companion object {
        val ASSESSMENTS = listOf("SALUDABLE", "NECESITA_CUIDADO", "EN_PELIGRO", "NO_EVALUABLE")
    }
}
