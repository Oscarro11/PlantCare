package plat.plantcare.app.core.data

import com.google.firebase.firestore.DocumentId

/**
 * Documento: pautasCuidado/{referenceId} (mismo ID que la ReferenciaPlanta)
 * Según el diagrama, se relaciona con ReferenciaPlanta.
 */
data class PlantReferenceConsiderations(
    @DocumentId val referenceId: String = "",      // PK y FK -> ReferenciaPlanta.id
    val wateringGuide: String = "",                // Cómo hacer riego
    val recommendedLocationAndLight: String = "",  // Ubicación y luz recomendadas
    val nutrition: String = "",                    // Nutrición
    val sprayingAndCleaning: String = ""           // Pulverización y limpieza
)