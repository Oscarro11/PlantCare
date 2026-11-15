package plat.plantcare.app.core.data.plant.characteristics.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class PlantCharacteristicsFirebaseDTO(
    @DocumentId val plantId: String = "",
    val lightExposure: Int = 0,
    val wateringFrequencyDays: Int = 0,
    val waterAmount: Int = 0,
    val humidityLevel: Int = 0,
    val currentClimate: Int = 0,
    val potSize: Int = 0,
    val potType: String = "",
    val substrate: String = "",
    val attentionLevel: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null
)

fun PlantCharacteristicsDTO.toFirebaseDTO(): PlantCharacteristicsFirebaseDTO =
    PlantCharacteristicsFirebaseDTO(
        plantId = plantId,
        lightExposure = lightExposure,
        wateringFrequencyDays = wateringFrequencyDays,
        waterAmount = waterAmount,
        humidityLevel = humidityLevel,
        currentClimate = currentClimate,
        potSize = potSize,
        potType = potType,
        substrate = substrate,
        attentionLevel = attentionLevel
    )

fun PlantCharacteristicsFirebaseDTO.toCommonDTO(): PlantCharacteristicsDTO =
    PlantCharacteristicsDTO(
        plantId = plantId,
        lightExposure = lightExposure,
        wateringFrequencyDays = wateringFrequencyDays,
        waterAmount = waterAmount,
        humidityLevel = humidityLevel,
        currentClimate = currentClimate,
        potSize = potSize,
        potType = potType,
        substrate = substrate,
        attentionLevel = attentionLevel
    )