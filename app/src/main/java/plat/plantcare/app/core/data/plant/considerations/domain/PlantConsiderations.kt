package plat.plantcare.app.core.data.plant.considerations.domain

data class PlantConsiderations(
    val plantId: String,
    val wateringGuide: String,
    val recommendedLocationAndLight: String,
    val nutrition: String,
    val sprayingAndCleaning: String
)