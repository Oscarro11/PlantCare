package plat.plantcare.app.core.data.plant.characteristics.remote

import kotlinx.serialization.Serializable
import plat.plantcare.app.core.data.plant.characteristics.domain.AttentionLevel
import plat.plantcare.app.core.data.plant.characteristics.domain.Climate
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantCharacteristics
import plat.plantcare.app.core.data.plant.characteristics.domain.Humidity
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantLightExposure
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantWateringFrequency
import plat.plantcare.app.core.data.plant.characteristics.domain.PotSize

@Serializable
data class PlantCharacteristicsDTO (
    val plantId: String = "",
    val lightExposure: Int = 0,
    val wateringFrequencyDays: Int = 0,
    val waterAmount: Int = 0,
    val humidityLevel: Int = 0,
    val currentClimate: Int = 0,
    val potSize: Int = 0,
    val potType: String = "",
    val substrate: String = "",
    val attentionLevel: Int = 0
)

fun PlantCharacteristicsDTO.toDomain(): PlantCharacteristics =
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

fun PlantCharacteristics.toCommonDTO(): PlantCharacteristicsDTO =
    PlantCharacteristicsDTO(
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