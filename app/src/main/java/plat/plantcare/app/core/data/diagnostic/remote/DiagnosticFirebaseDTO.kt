package plat.plantcare.app.core.data.diagnostic.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import plat.plantcare.app.core.data.models.toTimestamp
import java.time.Instant

data class FindingFirebaseDTO(
    val level: String = "",
    val detail: String = ""
)

data class CareRecommendationFirebaseDTO(
    val action: String = "",
    val everyDays: Int? = null,
    val reason: String = ""
)

/** Colección: plantas/{plantId}/diagnosticos/{id} */
data class DiagnosticFirebaseDTO(
    @DocumentId val id: String = "",
    val plantId: String = "",
    val createdAt: Timestamp? = null,
    val photoUrl: String? = null,
    val foliage: FindingFirebaseDTO = FindingFirebaseDTO(),
    val vigor: FindingFirebaseDTO = FindingFirebaseDTO(),
    val pests: FindingFirebaseDTO = FindingFirebaseDTO(),
    val diseases: FindingFirebaseDTO = FindingFirebaseDTO(),
    val hasFlowersOrFruits: Boolean = false,
    val score: Int = 0,
    val confidence: Float = 0f,
    val recommendations: List<CareRecommendationFirebaseDTO> = emptyList(),
    val modelVersion: String = "",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

fun DiagnosticDTO.toFirebaseDTO(): DiagnosticFirebaseDTO? {
    val created = Instant.parse(createdAt) ?: return null
    return DiagnosticFirebaseDTO(
        id = id,
        plantId = plantId,
        createdAt = created.toTimestamp(),
        photoUrl = photoUrl,
        foliage = foliage.toFirebaseDTO(),
        vigor = vigor.toFirebaseDTO(),
        pests = pests.toFirebaseDTO(),
        diseases = diseases.toFirebaseDTO(),
        hasFlowersOrFruits = hasFlowersOrFruits,
        score = score.coerceIn(0, 100),
        confidence = confidence.coerceIn(0f, 1f),
        recommendations = recommendations.map { it.toFirebaseDTO() },
        modelVersion = modelVersion
    )
}

fun DiagnosticFirebaseDTO.toCommonDTO(): DiagnosticDTO? {
    val created = createdAt?.toDate()?.toInstant() ?: return null
    return DiagnosticDTO(
        id = id,
        plantId = plantId,
        createdAt = created.toString(),
        photoUrl = photoUrl,
        foliage = foliage.toCommonDTO(),
        vigor = vigor.toCommonDTO(),
        pests = pests.toCommonDTO(),
        diseases = diseases.toCommonDTO(),
        hasFlowersOrFruits = hasFlowersOrFruits,
        score = score.coerceIn(0, 100),
        confidence = confidence.coerceIn(0f, 1f),
        recommendations = recommendations.map { it.toCommonDTO() },
        modelVersion = modelVersion
    )
}

private fun FindingDTO.toFirebaseDTO() =
    FindingFirebaseDTO(level = level, detail = detail)

private fun FindingFirebaseDTO.toCommonDTO() =
    FindingDTO(level = level, detail = detail)

private fun CareRecommendationDTO.toFirebaseDTO() =
    CareRecommendationFirebaseDTO(action = action, everyDays = everyDays, reason = reason)

private fun CareRecommendationFirebaseDTO.toCommonDTO() =
    CareRecommendationDTO(action = action, everyDays = everyDays, reason = reason)