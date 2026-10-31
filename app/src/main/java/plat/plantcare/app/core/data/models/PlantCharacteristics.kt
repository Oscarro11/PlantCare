package plat.plantcare.app.core.data.models

import plat.plantcare.app.core.data.remote.PlantCharacteristicsDTO

data class PlantCharacteristics(
    val plantId: String,
    val requiredLight: PlantRequiredLight,
    val wateringFrequencyDays: PlantWateringFrequency,
    val humidityLevel: PlantHumidity,
    val preferredClimate: String,
    val pot: String,
    val substrate: String
)

fun PlantCharacteristics.toDTO(): PlantCharacteristicsDTO =
    PlantCharacteristicsDTO(
        plantId = plantId,
        requiredLight = requiredLight.displayName,
        //TODO: update
        wateringFrequencyDays = 0,
        humidityLevel = 0,
        preferredClimate = preferredClimate,
        pot = pot,
        substrate = substrate
    )

enum class PlantRequiredLight(
    val displayName: String,
    val level: Int
) {
    BAJA(
        displayName = "Baja",
        level = 1
    ),
    MEDIA_INDIRECTA(
        displayName = "Media indirecta",
        level = 2
    ),
    BRILLANTE_INDIRECTA(
        displayName = "Brillante indirecta",
        level = 3
    ),
    BRILLANTE_DIRECTA(
        displayName = "Brillante directa",
        level = 4
    )
}

enum class PlantWateringFrequency(
    val displayName: String,
    val dayRange: String,
    val condition: (Int) -> Boolean,
    val level: Int
) {
    MUY_FRECUENTE(
        displayName = "Muy frecuente",
        dayRange = "Menos de 4 d",
        condition = {days -> days < 4},
        level = 1
    ),
    FRECUENTE(
        displayName = "Frecuente",
        dayRange = "4-5 d",
        condition = {days -> days in 4..5},
        level = 2
    ),
    MODERADO(
        displayName = "Moderado",
        dayRange = "6-8 d",
        condition = {days -> days in 6..8},
        level = 3
    ),
    ESPACIADO(
        displayName = "Espaciado",
        dayRange = "Más de 8 d",
        condition = {days -> days > 8},
        level = 4
    )
}

enum class PlantHumidity(
    val displayName: String,
    val percentage: String,
    val condition: (Int) -> Boolean,
    val level: Int
) {
    BAJA(
        displayName = "Baja",
        percentage = "Menos del 30%",
        condition = {percentage -> percentage < 30},
        level = 1
    ),
    MODERADA(
        displayName = "Moderada",
        percentage = "30-50%",
        condition = {percentage -> percentage in 30..50},
        level = 2
    ),
    MEDIA(
        displayName = "Media",
        percentage = "50-70%",
        condition = {percentage -> percentage in 50..70},
        level = 3
    ),
    ALTA(
        displayName = "Alta",
        percentage = "Más del 70%",
        condition = {percentage -> percentage > 70},
        level = 4
    ),
}