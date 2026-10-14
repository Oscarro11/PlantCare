package plat.plantcare.app.core.data

import com.google.firebase.firestore.DocumentId

/** Documento: caracteristicas/{plantId} (mismo ID que la planta) */
data class PlantCharacteristics(
    @DocumentId val plantId: String = "",   // PK y FK -> Planta.id
    val requiredLight: String = "",         // Luz requerida
    val wateringFrequencyDays: Int = 0,     // Frecuencia de riego (días)
    val humidityLevel: String = "",         // Nivel de humedad
    val preferredClimate: String = "",      // Clima preferido
    val pot: String = "",                   // Maceta
    val substrate: String = ""              // Sustrato
)