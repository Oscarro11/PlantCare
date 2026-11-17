package plat.plantcare.app.core.data.plant.characteristics.domain

data class PlantCharacteristics(
    val plantId: String,
    val lightExposure: PlantLightExposure,
    val wateringFrequencyDays: PlantWateringFrequency,
    val waterVolume: Int,
    val humidityLevel: Humidity,
    val currentClimate: Climate,
    val potSize: PotSize,
    val potType: String,
    val substrate: String,
    val attentionLevel: AttentionLevel
)

enum class PlantLightExposure(
    val displayName: String,
    val level: Int
) {
    BAJA(
        displayName = "Baja",
        level = 1
    ),
    MEDIA(
        displayName = "Media",
        level = 2
    ),
    BRILLANTE_INDIRECTA(
        displayName = "Brillante indirecta",
        level = 3
    ),
    BRILLANTE_DIRECTA(
        displayName = "Brillante directa",
        level = 4
    );

    companion object {
        fun fromLevel(level: Int): PlantLightExposure =
            entries.firstOrNull{ it.level == level } ?: BAJA
    }
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
    );

    companion object {
        fun fromLevel(level: Int): PlantWateringFrequency =
            entries.firstOrNull{ it.level == level } ?: ESPACIADO
    }
}

enum class Humidity(
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
    );

    companion object {
        fun fromLevel(level: Int): Humidity =
            entries.firstOrNull{ it.level == level } ?: BAJA
    }
}

enum class Climate(
    val displayName: String,
    val level: Int
) {
    FRIO(
        displayName = "Frio",
        level = 1
    ),
    TEMPLADO(
        displayName = "Templado",
        level = 2
    ),
    CALIDO(
        displayName = "Calido",
        level = 3
    ),
    CALIENTE(
        displayName = "Caliente",
        level = 4
    );

    companion object {
        fun fromLevel(level: Int): Climate =
            entries.firstOrNull{ it.level == level } ?: FRIO
    }
}

enum class PotSize(
    val displayName: String,
    val level: Int
) {
    PEQUENA(
        displayName = "Pequena",
        level = 1
    ),
    MEDIANA(
        displayName = "Mediana",
        level = 2
    ),
    GRANDE(
        displayName = "Grande",
        level = 3
    );

    companion object {
        fun fromLevel(level: Int): PotSize =
            entries.firstOrNull{ it.level == level } ?: PEQUENA
    }
}

enum class AttentionLevel(
    val level: Int
) {
    POCO(
        level = 1
    ),
    MEDIO(
        level = 2
    ),
    DEMANDANTE(
        level = 3
    );

    companion object {
        fun fromLevel(level: Int): AttentionLevel =
            entries.firstOrNull { it.level == level } ?: POCO
    }
}
