package plat.plantcare.app.core.data.remote

import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.core.data.models.PlantCharacteristics
import plat.plantcare.app.core.data.models.PlantHumidity
import plat.plantcare.app.core.data.models.PlantRequiredLight
import plat.plantcare.app.core.data.models.PlantWateringFrequency

/** Documento: caracteristicas/{plantId} (mismo ID que la planta) */
data class PlantCharacteristicsDTO(
    @DocumentId val plantId: String = "",
    val requiredLight: String = "",
    val wateringFrequencyDays: Int = 0,
    val humidityLevel: Int = 0,
    val preferredClimate: String = "",
    val pot: String = "",
    val substrate: String = ""
)

fun PlantCharacteristicsDTO.toModel(): PlantCharacteristics =
    PlantCharacteristics(
        plantId = plantId,
        requiredLight = PlantRequiredLight.entries
            .firstOrNull { it.displayName == preferredClimate } ?: PlantRequiredLight.BAJA,
        wateringFrequencyDays = PlantWateringFrequency.entries
            .firstOrNull { it.condition(wateringFrequencyDays) } ?: PlantWateringFrequency.ESPACIADO,
        humidityLevel = PlantHumidity.entries
            .firstOrNull { it.condition(humidityLevel) } ?: PlantHumidity.BAJA,
        preferredClimate = preferredClimate,
        pot = pot,
        substrate = substrate
    )