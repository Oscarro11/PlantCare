package plat.plantcare.app.core.data.remote.Firebase

import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.core.data.models.PlantCharacteristics
import plat.plantcare.app.core.data.models.PlantHumidity
import plat.plantcare.app.core.data.models.PlantRequiredLight
import plat.plantcare.app.core.data.models.PlantWateringFrequency

/** Documento: caracteristicas/{plantId} (mismo ID que la planta) */
data class PlantCharacteristicsFirebaseDTO(
    @DocumentId val plantId: String = "",
    val requiredLight: String = "",
    val wateringFrequencyDays: Int = 0,
    val humidityLevel: Int = 0,
    val preferredClimate: String = "",
    val pot: String = "",
    val substrate: String = ""
)

fun PlantCharacteristicsFirebaseDTO.toModel(): PlantCharacteristics =
    PlantCharacteristics(
        plantId = plantId,
        requiredLight = PlantRequiredLight.entries
            .firstOrNull { it.displayName.equals(requiredLight, true) || it.name.equals(requiredLight, true) } ?: PlantRequiredLight.NO_REGISTRADO,
        wateringFrequencyDays = PlantWateringFrequency.entries
            .firstOrNull { wateringFrequencyDays > 0 && it.condition(wateringFrequencyDays) } ?: PlantWateringFrequency.NO_REGISTRADO,
        humidityLevel = PlantHumidity.entries
            .firstOrNull { humidityLevel in 1..100 && it.condition(humidityLevel) } ?: PlantHumidity.NO_REGISTRADO,
        preferredClimate = preferredClimate,
        pot = pot,
        substrate = substrate,
        wateringDays = wateringFrequencyDays.takeIf { it > 0 },
        humidityPercent = humidityLevel.takeIf { it in 1..100 }
    )