package plat.plantcare.app.core.data.plant.characteristics.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import plat.plantcare.app.core.data.plant.characteristics.domain.AttentionLevel
import plat.plantcare.app.core.data.plant.characteristics.domain.Climate
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantCharacteristics
import plat.plantcare.app.core.data.plant.characteristics.domain.Humidity
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantLightExposure
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantWateringFrequency
import plat.plantcare.app.core.data.plant.characteristics.domain.PotSize

@Entity(tableName = "plant_characteristics")
data class PlantCharacteristicsEntity(
    @PrimaryKey(autoGenerate = false) val plantId: String,
    val lightExposure: Int,
    val wateringFrequencyDays: Int,
    val waterAmount: Int,
    val humidityLevel: Int,
    val currentClimate: Int,
    val potSize: Int,
    val potType: String,
    val substrate: String,
    val attentionLevel: Int
)

fun PlantCharacteristicsEntity.toDomain(): PlantCharacteristics =
    PlantCharacteristics(
        plantId = plantId,
        lightExposure = PlantLightExposure.fromLevel(lightExposure),
        wateringFrequencyDays = PlantWateringFrequency.fromLevel(wateringFrequencyDays),
        waterVolume = waterAmount,
        humidityLevel = Humidity.fromLevel(humidityLevel),
        currentClimate = Climate.fromLevel(currentClimate),
        potSize = PotSize.fromLevel(potSize),
        potType = potType,
        substrate = substrate,
        attentionLevel = AttentionLevel.fromLevel(attentionLevel)
    )

fun PlantCharacteristics.toEntity(): PlantCharacteristicsEntity =
    PlantCharacteristicsEntity(
        plantId = plantId,
        lightExposure = lightExposure.level,
        wateringFrequencyDays = wateringFrequencyDays.level,
        waterAmount = waterVolume,
        humidityLevel = humidityLevel.level,
        currentClimate = currentClimate.level,
        potSize = potSize.level,
        potType = potType,
        substrate = substrate,
        attentionLevel = attentionLevel.level
    )