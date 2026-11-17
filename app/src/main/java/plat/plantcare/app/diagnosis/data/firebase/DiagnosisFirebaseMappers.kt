package plat.plantcare.app.diagnosis.data.firebase

import com.google.firebase.Timestamp
import java.time.ZoneId
import java.util.Date
import plat.plantcare.app.core.data.models.toLocalDate
import plat.plantcare.app.core.data.models.toTimestamp
import plat.plantcare.app.core.data.remote.Firebase.PlantCharacteristicsFirebaseDTO
import plat.plantcare.app.diagnosis.domain.Diagnostic
import plat.plantcare.app.diagnosis.domain.DiagnosisRequirements

fun Diagnostic.toFirebaseDTO(zone: ZoneId = ZoneId.systemDefault()) = DiagnosticFirebaseDTO(
    id = id, plantId = plantId,
    date = if (analyzedAtEpochMillis > 0) Timestamp(Date(analyzedAtEpochMillis)) else date.toTimestamp(zone),
    foliageState = foliageState, growthVigor = growthVigor, pestControl = pestControl,
    diseaseSigns = diseaseSigns, hasFlowersOrFruits = hasFlowersOrFruits, score = score,
    assessment = assessment, summary = summary, possibleCauses = possibleCauses,
    recommendations = recommendations, limitations = limitations, model = model
)

fun DiagnosticFirebaseDTO.toModel(zone: ZoneId = ZoneId.systemDefault()): Diagnostic? {
    val timestamp = date ?: return null
    return Diagnostic(id, plantId, timestamp.toLocalDate(zone), foliageState, growthVigor, pestControl,
        diseaseSigns, hasFlowersOrFruits, score, assessment, summary, possibleCauses, recommendations,
        limitations, model, timestamp.toDate().time)
}

internal fun PlantCharacteristicsFirebaseDTO.toDiagnosisRequirements() = DiagnosisRequirements(
    requiredLight, wateringFrequencyDays.takeIf { it > 0 }, humidityLevel.takeIf { it in 1..100 },
    preferredClimate, pot, substrate
)
