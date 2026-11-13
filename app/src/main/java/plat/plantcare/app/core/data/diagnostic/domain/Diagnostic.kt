package plat.plantcare.app.core.data.diagnostic.domain

import plat.plantcare.app.core.data.models.TaskAction
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

enum class FindingLevel {
    EXCELLENT, GOOD, FAIR, POOR;

    companion object {
        fun fromName(name: String?): FindingLevel =
            entries.firstOrNull {it.name == name} ?: FAIR
    }
}

data class Finding(
    val level: FindingLevel,
    val detail: String
)

data class CareRecommendation(
    val action: TaskAction,
    val everyDays: Int?,
    val reason: String
)

enum class HealthStatus {
    HEALTHY, NEEDS_CARE, ENDANGERED, DEAD;
    companion object {
        fun fromScore(score: Int): HealthStatus = when {
            score < 25 -> DEAD
            score < 50 -> ENDANGERED
            score < 76 -> NEEDS_CARE
            else -> HEALTHY
        }
    }
}

data class Diagnostic (
    val id: String,
    val plantId: String,
    val createdAt: Instant,                  // a real moment, so same-day scans can be ordered
    val photoUrl: String?,                   // Firebase Storage URL
    val foliage: Finding,                    // was foliageState
    val vigor: Finding,                      // was growthVigor
    val pests: Finding,                      // was pestControl
    val diseases: Finding,                   // was diseaseSigns
    val hasFlowersOrFruits: Boolean,
    val score: Int,                          // 0..100
    val confidence: Float,                   // 0..1, how sure the model is
    val recommendations: List<CareRecommendation>,
    val modelVersion: String                 // which model produced it
) {
    val date: LocalDate
        get() = createdAt.atZone(ZoneId.systemDefault()).toLocalDate()

    fun getHealthStatus(): HealthStatus = HealthStatus.fromScore(score)
}