package plat.plantcare.app.core.data.remote.Firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.core.data.models.Plant
import plat.plantcare.app.core.data.models.toLocalDate
import java.time.ZoneId

/** Colección: plantas/{id} */
data class PlantFirebaseDTO(
    @DocumentId val id: String = "",
    val name: String = "",            // Nombre personal
    val image: String = "",           // URL de la imagen
    val location: String = "",
    val birthDate: Timestamp? = null, // Fecha de ingreso
    val ownerId: String = "",         // FK -> Usuario.id (Dueño)
    val referenceId: String = "",      // FK -> ReferenciaPlanta.id
    val favorite: Boolean = false
)

fun PlantFirebaseDTO.toModel(zone: ZoneId = ZoneId.systemDefault()): Plant =
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