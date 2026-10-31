package plat.plantcare.app.core.data.models

import plat.plantcare.app.core.data.remote.PlantReferenceConsiderationsDTO


data class PlantReferenceConsiderations(
    val referenceId: String,
    val wateringGuide: String,
    val recommendedLocationAndLight: String,
    val nutrition: String,
    val sprayingAndCleaning: String
)

fun PlantReferenceConsiderations.toDTO(): PlantReferenceConsiderationsDTO =
    PlantReferenceConsiderationsDTO(
        referenceId = referenceId,
        wateringGuide = wateringGuide,
        recommendedLocationAndLight = recommendedLocationAndLight,
        nutrition = nutrition,
        sprayingAndCleaning = sprayingAndCleaning
    )