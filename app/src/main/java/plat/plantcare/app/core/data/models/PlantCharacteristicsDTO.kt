package plat.plantcare.app.core.data.models

import com.google.firebase.firestore.DocumentId

// ---------------------------------------------------------------------------
// Data layer: what is stored in Firestore
// ---------------------------------------------------------------------------

/** Documento: caracteristicas/{plantId} (mismo ID que la planta) */
data class PlantCharacteristicsDto(
    @DocumentId val plantId: String = "",   // PK y FK -> Planta.id
    val requiredLight: String = "",         // Luz requerida
    val wateringFrequencyDays: Int = 0,     // Frecuencia de riego (días)
    val humidityLevel: String = "",         // Nivel de humedad
    val preferredClimate: String = "",      // Clima preferido
    val pot: String = "",                   // Maceta
    val substrate: String = ""              // Sustrato
)

// ---------------------------------------------------------------------------
// Domain layer: what the rest of the app uses
// ---------------------------------------------------------------------------

data class PlantCharacteristics(
    val plantId: String,
    val requiredLight: String,
    val wateringFrequencyDays: Int,
    val humidityLevel: String,
    val preferredClimate: String,
    val pot: String,
    val substrate: String
)

// ---------------------------------------------------------------------------
// Conversions (DTO <-> domain)
// ---------------------------------------------------------------------------

fun PlantCharacteristicsDto.toDomain(): PlantCharacteristics =
    PlantCharacteristics(
        plantId = plantId,
        requiredLight = requiredLight,
        wateringFrequencyDays = wateringFrequencyDays,
        humidityLevel = humidityLevel,
        preferredClimate = preferredClimate,
        pot = pot,
        substrate = substrate
    )

fun PlantCharacteristics.toDto(): PlantCharacteristicsDto =
    PlantCharacteristicsDto(
        plantId = plantId,
        requiredLight = requiredLight,
        wateringFrequencyDays = wateringFrequencyDays,
        humidityLevel = humidityLevel,
        preferredClimate = preferredClimate,
        pot = pot,
        substrate = substrate
    )