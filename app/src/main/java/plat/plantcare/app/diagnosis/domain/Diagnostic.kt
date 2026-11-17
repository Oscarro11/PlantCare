package plat.plantcare.app.diagnosis.domain

import java.time.LocalDate

data class Diagnostic(
    val id: String,
    val plantId: String,
    val date: LocalDate,
    val foliageState: String,
    val growthVigor: String,
    val pestControl: String,
    val diseaseSigns: String,
    val hasFlowersOrFruits: Boolean?,
    val score: Int?,
    val assessment: String = "",
    val summary: String = "",
    val possibleCauses: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val limitations: List<String> = emptyList(),
    val model: String = "",
    val analyzedAtEpochMillis: Long = 0
)

enum class HealthStatus { SALUDABLE, NECESITA_CUIDADO, EN_PELIGRO, MUERTA, NO_EVALUABLE }

fun Diagnostic.getHealthStatus(): HealthStatus {
    if (assessment.isNotBlank()) return HealthStatus.entries.firstOrNull { it.name == assessment } ?: HealthStatus.NO_EVALUABLE
    // Retain compatibility with older records. New AI results never invent a score.
    val legacy = score ?: return HealthStatus.NO_EVALUABLE
    return when { legacy < 25 -> HealthStatus.MUERTA; legacy < 50 -> HealthStatus.EN_PELIGRO
        legacy <= 75 -> HealthStatus.NECESITA_CUIDADO; else -> HealthStatus.SALUDABLE }
}
