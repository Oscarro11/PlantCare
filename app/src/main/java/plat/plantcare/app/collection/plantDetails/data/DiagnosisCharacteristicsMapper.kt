package plat.plantcare.app.collection.plantDetails.data

import plat.plantcare.app.core.data.models.PlantCharacteristics
import plat.plantcare.app.core.data.models.PlantRequiredLight
import plat.plantcare.app.core.data.models.PlantWateringFrequency
import plat.plantcare.app.core.data.models.PlantHumidity
import plat.plantcare.app.diagnosis.domain.DiagnosisRequirements

internal fun DiagnosisRequirements.toPlantCharacteristics(plantId: String) = PlantCharacteristics(
    plantId = plantId,
    requiredLight = PlantRequiredLight.entries.firstOrNull {
        it.displayName.equals(requiredLight, true) || it.name.equals(requiredLight, true)
    } ?: PlantRequiredLight.NO_REGISTRADO,
    wateringFrequencyDays = PlantWateringFrequency.entries.firstOrNull {
        wateringFrequencyDays?.let { days -> days > 0 && it.condition(days) } == true
    } ?: PlantWateringFrequency.NO_REGISTRADO,
    humidityLevel = PlantHumidity.entries.firstOrNull {
        humidityLevel?.let { humidity -> humidity in 1..100 && it.condition(humidity) } == true
    } ?: PlantHumidity.NO_REGISTRADO,
    preferredClimate = preferredClimate,
    pot = pot,
    substrate = substrate,
    wateringDays = wateringFrequencyDays,
    humidityPercent = humidityLevel
)
