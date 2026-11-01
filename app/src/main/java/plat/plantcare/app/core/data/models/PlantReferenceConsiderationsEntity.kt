package plat.plantcare.app.core.data.models

import plat.plantcare.app.core.data.remote.Firebase.PlantReferenceConsiderationsFirebaseDTO


data class PlantReferenceConsiderations(
    val referenceId: String,
    val wateringGuide: String,
    val recommendedLocationAndLight: String,
    val nutrition: String,
    val sprayingAndCleaning: String
)

fun PlantReferenceConsiderations.toFirebaseDTO(): PlantReferenceConsiderationsFirebaseDTO =
    PlantReferenceConsiderationsFirebaseDTO(
        referenceId = referenceId,
        wateringGuide = wateringGuide,
        recommendedLocationAndLight = recommendedLocationAndLight,
        nutrition = nutrition,
        sprayingAndCleaning = sprayingAndCleaning
    )