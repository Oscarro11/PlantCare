package plat.plantcare.app.core.data.plant.general.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import plat.plantcare.app.core.data.models.toTimestamp
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

data class PlantFirebaseDTO(
    @DocumentId val id: String = "",
    val name: String = "",
    val image: String? = null,
    val location: String = "",
    val birthDate: Timestamp? = null,
    val ownerId: String = "",
    val referenceId: String? = null,
    val favorite: Boolean = false,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

fun PlantDTO.toFirebaseDTO(): PlantFirebaseDTO? {
    val birth = LocalDate.parse(birthDate).atStartOfDay(ZoneOffset.UTC).toInstant() ?: return null
    return PlantFirebaseDTO(
        id = id,
        name = name,
        image = imageUrl,
        location = location,
        birthDate = birth.toTimestamp(),
        ownerId = ownerId,
        referenceId = referenceId,
        favorite = favorite
    )
}


fun PlantFirebaseDTO.toCommonDTO(zone: ZoneId = ZoneId.systemDefault()): PlantDTO =
    PlantDTO(
        id = id,
        name = name,
        imageUrl = image,
        location = location,
        birthDate = birthDate.toString(),
        ownerId = ownerId,
        referenceId = referenceId,
        favorite = favorite
    )