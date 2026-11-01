package plat.plantcare.app.core.data.models

import plat.plantcare.app.core.data.remote.Firebase.PlantFirebaseDTO
import java.time.LocalDate
import java.time.ZoneId

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

fun Plant.toFirebaseDTO(zone: ZoneId = ZoneId.systemDefault()): PlantFirebaseDTO =
    PlantFirebaseDTO(
        id = id,
        name = name,
        image = imageUrl,
        location = location,
        birthDate = birthDate?.toTimestamp(zone),
        ownerId = ownerId,
        referenceId = referenceId,
        favorite = favorite
    )