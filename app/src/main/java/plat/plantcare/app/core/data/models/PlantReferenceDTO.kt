package plat.plantcare.app.core.data.models

import com.google.firebase.firestore.DocumentId

// ---------------------------------------------------------------------------
// Data layer: what is stored in Firestore
// ---------------------------------------------------------------------------

/** Colección: referencias/{id} */
data class PlantReferenceDto(
    @DocumentId val id: String = "",
    val scientificName: String = "",  // Nombre científico
    val description: String = ""      // Descripción
)

// ---------------------------------------------------------------------------
// Domain layer: what the rest of the app uses
// ---------------------------------------------------------------------------

data class PlantReference(
    val id: String,
    val scientificName: String,
    val description: String
)

// ---------------------------------------------------------------------------
// Conversions (DTO <-> domain)
// ---------------------------------------------------------------------------

fun PlantReferenceDto.toDomain(): PlantReference =
    PlantReference(
        id = id,
        scientificName = scientificName,
        description = description
    )

fun PlantReference.toDto(): PlantReferenceDto =
    PlantReferenceDto(
        id = id,
        scientificName = scientificName,
        description = description
    )