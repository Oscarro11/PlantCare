package plat.plantcare.app.core.data.plant.considerations.remote

import com.google.firebase.ai.type.Schema
import kotlinx.serialization.Serializable

@Serializable
data class PlantConsiderationsAiDTO(
    val wateringGuide: String = "",
    val recommendedLocationAndLight: String = "",
    val nutrition: String = "",
    val sprayingAndCleaning: String = ""
)

val considerationsSchema = Schema.obj(
    mapOf(
        "wateringGuide" to Schema.string(),
        "recommendedLocationAndLight" to Schema.string(),
        "nutrition" to Schema.string(),
        "sprayingAndCleaning" to Schema.string()
    )
)

fun PlantConsiderationsAiDTO.toCommonDTO(): PlantConsiderationsDTO =
    PlantConsiderationsDTO(
        plantId = "",
        wateringGuide,
        recommendedLocationAndLight,
        nutrition,
        sprayingAndCleaning
    )

fun PlantConsiderationsDTO.toAiDTO(): PlantConsiderationsAiDTO =
    PlantConsiderationsAiDTO(
        wateringGuide,
        recommendedLocationAndLight,
        nutrition,
        sprayingAndCleaning
    )