package plat.plantcare.app.core.data.models

import com.google.firebase.firestore.DocumentId

// ---------------------------------------------------------------------------
// Data layer: what is stored in Firestore
// ---------------------------------------------------------------------------

/** Colección: plantasDeseadas/{id} */
data class WishPlantEntity(
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
// Conversions (Entity <-> domain)
// ---------------------------------------------------------------------------

fun WishPlantEntity.toDomain(): WishPlant =
    WishPlant(
        id = id,
        userId = userId,
        referenceId = referenceId
    )

fun WishPlant.toEntity(): WishPlantEntity =
    WishPlantEntity(
        id = id,
        userId = userId,
        referenceId = referenceId
    )