package plat.plantcare.app.core.data.plant.considerations.remote

import kotlinx.serialization.Serializable
import plat.plantcare.app.core.data.plant.considerations.domain.PlantConsiderations

@Serializable
data class PlantConsiderationsDTO(
    val plantId: String = "",
    val wateringGuide: String = "",
    val recommendedLocationAndLight: String = "",
    val nutrition: String = "",
    val sprayingAndCleaning: String = ""
)

fun PlantConsiderationsDTO.toDomain(): PlantConsiderations =
    PlantConsiderations(
        plantId = plantId,
        wateringGuide = wateringGuide,
        recommendedLocationAndLight = recommendedLocationAndLight,
        nutrition = nutrition,
        sprayingAndCleaning = sprayingAndCleaning
    )

fun PlantConsiderations.toCommonDTO(): PlantConsiderationsDTO =
    PlantConsiderationsDTO(
        plantId,
        wateringGuide,
        recommendedLocationAndLight,
        nutrition,
        sprayingAndCleaning
    )