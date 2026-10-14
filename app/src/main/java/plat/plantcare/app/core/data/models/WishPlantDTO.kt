package plat.plantcare.app.core.data.models

import com.google.firebase.firestore.DocumentId

// ---------------------------------------------------------------------------
// Data layer: what is stored in Firestore
// ---------------------------------------------------------------------------

/** Colección: plantasDeseadas/{id} */
data class WishPlantDto(
    @DocumentId val id: String = "",
    val userId: String = "",       // FK -> Usuario.id
    val referenceId: String = ""   // FK -> ReferenciaPlanta.id (Referencia de planta)
)

// ---------------------------------------------------------------------------
// Domain layer: what the rest of the app uses
// ---------------------------------------------------------------------------

data class WishPlant(
    val id: String,
    val userId: String,
    val referenceId: String
)

// ---------------------------------------------------------------------------
// Conversions (DTO <-> domain)
// ---------------------------------------------------------------------------

fun WishPlantDto.toDomain(): WishPlant =
    WishPlant(
        id = id,
        userId = userId,
        referenceId = referenceId
    )

fun WishPlant.toDto(): WishPlantDto =
    WishPlantDto(
        id = id,
        userId = userId,
        referenceId = referenceId
    )