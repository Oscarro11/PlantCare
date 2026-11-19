package plat.plantcare.app.core.data.plant.considerations.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class PlantConsiderationsFirebaseDTO(
    @DocumentId val plantId: String = "",
    val scientificName: String = "",
    val wateringGuide: String = "",
    val recommendedLocationAndLight: String = "",
    val nutrition: String = "",
    val sprayingAndCleaning: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

fun PlantConsiderationsFirebaseDTO.toCommonDTO(): PlantConsiderationsDTO =
    PlantConsiderationsDTO(
        plantId,
        scientificName,
        wateringGuide,
        recommendedLocationAndLight,
        nutrition,
        sprayingAndCleaning
    )

fun PlantConsiderationsDTO.toFirebaseDTO(): PlantConsiderationsFirebaseDTO =
    PlantConsiderationsFirebaseDTO(
        plantId,
        scientificName,
        wateringGuide,
        recommendedLocationAndLight,
        nutrition,
        sprayingAndCleaning,
    )