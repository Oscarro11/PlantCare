package plat.plantcare.app.core.data.remote.Firebase

import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.core.data.models.PlantReferenceConsiderations

/**
 * Documento: pautasCuidado/{referenceId} (mismo ID que la ReferenciaPlanta)
 * Según el diagrama, se relaciona con ReferenciaPlanta.
 */
data class PlantReferenceConsiderationsFirebaseDTO(
    @DocumentId val referenceId: String = "",      // PK y FK -> ReferenciaPlanta.id
    val wateringGuide: String = "",                // Cómo hacer riego
    val recommendedLocationAndLight: String = "",  // Ubicación y luz recomendadas
    val nutrition: String = "",                    // Nutrición
    val sprayingAndCleaning: String = ""           // Pulverización y limpieza
)

fun PlantReferenceConsiderationsFirebaseDTO.toModel(): PlantReferenceConsiderations =
    PlantReferenceConsiderations(
        referenceId = referenceId,
        wateringGuide = wateringGuide,
        recommendedLocationAndLight = recommendedLocationAndLight,
        nutrition = nutrition,
        sprayingAndCleaning = sprayingAndCleaning
    )