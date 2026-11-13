package plat.plantcare.app.core.data.diagnostic.remote

import kotlinx.serialization.Serializable
import plat.plantcare.app.core.data.diagnostic.domain.CareRecommendation
import plat.plantcare.app.core.data.diagnostic.domain.Diagnostic
import plat.plantcare.app.core.data.diagnostic.domain.Finding
import plat.plantcare.app.core.data.diagnostic.domain.FindingLevel
import plat.plantcare.app.core.data.models.TaskAction
import java.time.Instant

@Serializable
data class FindingDTO(
    val level: String = "",
    val detail: String = ""
)

@Serializable
data class CareRecommendationDTO(
    val action: String = "",
    val everyDays: Int? = null,
    val reason: String = ""
)

@Serializable
data class DiagnosticDTO (
    val id: String = "",
    val plantId: String = "",
    val createdAt: String = "",
    val photoUrl: String? = null,
    val foliage: FindingDTO = FindingDTO(),
    val vigor: FindingDTO = FindingDTO(),
    val pests: FindingDTO = FindingDTO(),
    val diseases: FindingDTO = FindingDTO(),
    val hasFlowersOrFruits: Boolean = false,
    val score: Int = 0,
    val confidence: Float = 0f,
    val recommendations: List<CareRecommendationDTO> = emptyList(),
    val modelVersion: String = ""
)

private fun Finding.toCommonDTO() =
    FindingDTO(level = level.name, detail = detail)

private fun FindingDTO.toDomain() =
    Finding(level = FindingLevel.fromName(level), detail = detail)

private fun CareRecommendation.toCommonDTO() =
    CareRecommendationDTO(action = action.name, everyDays = everyDays, reason = reason)

private fun CareRecommendationDTO.toDomain() =
    CareRecommendation(
        action = TaskAction.fromName(action),
        everyDays = everyDays,
        reason = reason
    )

fun Diagnostic.toDTO() = DiagnosticDTO(
    id = id,
    plantId = plantId,
    createdAt = createdAt.toString(),
    photoUrl = photoUrl,
    foliage = foliage.toCommonDTO(),
    vigor = vigor.toCommonDTO(),
    pests = pests.toCommonDTO(),
    diseases = diseases.toCommonDTO(),
    hasFlowersOrFruits = hasFlowersOrFruits,
    score = score,
    confidence = confidence,
    recommendations = recommendations.map { it.toCommonDTO() },
    modelVersion = modelVersion
)

fun DiagnosticDTO.toDomain() = Diagnostic(
    id = id,
    plantId = plantId,
    createdAt = Instant.parse(createdAt),
    photoUrl = photoUrl,
    foliage = foliage.toDomain(),
    vigor = vigor.toDomain(),
    pests = pests.toDomain(),
    diseases = diseases.toDomain(),
    hasFlowersOrFruits = hasFlowersOrFruits,
    score = score.coerceIn(0, 100),
    confidence = confidence.coerceIn(0f, 1f),
    recommendations = recommendations.map { it.toDomain() },
    modelVersion = modelVersion
)