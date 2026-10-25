package plat.plantcare.app.core.data.models

import com.google.firebase.firestore.DocumentId

// ---------------------------------------------------------------------------
// Data layer: what is stored in Firestore
// ---------------------------------------------------------------------------

/**
 * Documento: pautasCuidado/{referenceId} (mismo ID que la ReferenciaPlanta)
 * Según el diagrama, se relaciona con ReferenciaPlanta.
 */
data class PlantReferenceConsiderationsEntity(
    @DocumentId val referenceId: String = "",      // PK y FK -> ReferenciaPlanta.id
    val wateringGuide: String = "",                // Cómo hacer riego
    val recommendedLocationAndLight: String = "",  // Ubicación y luz recomendadas
    val nutrition: String = "",                    // Nutrición
    val sprayingAndCleaning: String = ""           // Pulverización y limpieza
)

// ---------------------------------------------------------------------------
// Domain layer: what the rest of the app uses
// ---------------------------------------------------------------------------

data class PlantReferenceConsiderations(
    val referenceId: String,
    val wateringGuide: String,
    val recommendedLocationAndLight: String,
    val nutrition: String,
    val sprayingAndCleaning: String
)

// ---------------------------------------------------------------------------
// Conversions (Entity <-> domain)
// ---------------------------------------------------------------------------

fun PlantReferenceConsiderationsEntity.toDomain(): PlantReferenceConsiderations =
    PlantReferenceConsiderations(
        referenceId = referenceId,
        wateringGuide = wateringGuide,
        recommendedLocationAndLight = recommendedLocationAndLight,
        nutrition = nutrition,
        sprayingAndCleaning = sprayingAndCleaning
    )

fun PlantReferenceConsiderations.toEntity(): PlantReferenceConsiderationsEntity =
    PlantReferenceConsiderationsEntity(
        referenceId = referenceId,
        wateringGuide = wateringGuide,
        recommendedLocationAndLight = recommendedLocationAndLight,
        nutrition = nutrition,
        sprayingAndCleaning = sprayingAndCleaning
    )