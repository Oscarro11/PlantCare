package plat.plantcare.app.core.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.time.LocalDate
import java.time.ZoneId

// ---------------------------------------------------------------------------
// Data layer: what is stored in Firestore
// ---------------------------------------------------------------------------

/** Colección: plantas/{id} */
data class PlantEntity(
    @DocumentId val id: String = "",
    val name: String = "",            // Nombre personal
    val image: String = "",           // URL de la imagen
    val location: String = "",
    val birthDate: Timestamp? = null, // Fecha de ingreso
    val ownerId: String = "",         // FK -> Usuario.id (Dueño)
    val referenceId: String = "",      // FK -> ReferenciaPlanta.id
    val favorite: Boolean = false
)

// ---------------------------------------------------------------------------
// Domain layer: what the rest of the app uses
// ---------------------------------------------------------------------------

data class Plant(
    val id: String,
    val name: String,
    val imageUrl: String,
    val location: String,
    val birthDate: LocalDate?,        // Null when the entry date was never recorded
    val ownerId: String,
    val referenceId: String,
    val favorite: Boolean = false
)

// ---------------------------------------------------------------------------
// Conversions (Entity <-> domain)
// Timestamp <-> LocalDate helpers live in TaskEntity.kt (same package)
// ---------------------------------------------------------------------------

fun PlantEntity.toDomain(zone: ZoneId = ZoneId.systemDefault()): Plant =
    Plant(
        id = id,
        name = name,
        imageUrl = image,
        location = location,
        birthDate = birthDate?.toLocalDate(zone),
        ownerId = ownerId,
        referenceId = referenceId,
        favorite = favorite
    )

fun Plant.toEntity(zone: ZoneId = ZoneId.systemDefault()): PlantEntity =
    PlantEntity(
        id = id,
        name = name,
        image = imageUrl,
        location = location,
        birthDate = birthDate?.toTimestamp(zone),
        ownerId = ownerId,
        referenceId = referenceId,
        favorite = favorite
    )