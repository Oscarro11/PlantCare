package plat.plantcare.app.core.data.plant.considerations.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import plat.plantcare.app.core.data.plant.considerations.domain.PlantConsiderations

@Entity(tableName = "plant_considerations")
data class PlantConsiderationsEntity (
    @PrimaryKey (autoGenerate = false) val plantId: String,
    val scientificName: String,
    val wateringGuide: String,
    val recommendedLocationAndLight: String,
    val nutrition: String,
    val sprayingAndCleaning: String
)

fun PlantConsiderationsEntity.toDomain(): PlantConsiderations =
    PlantConsiderations(
        plantId,
        scientificName,
        wateringGuide,
        recommendedLocationAndLight,
        nutrition,
        sprayingAndCleaning
    )

fun PlantConsiderations.toEntity(): PlantConsiderationsEntity =
    PlantConsiderationsEntity(
        plantId,
        scientificName,
        wateringGuide,
        recommendedLocationAndLight,
        nutrition,
        sprayingAndCleaning
    )