package plat.plantcare.app.core.data.plant.general.remote

import kotlinx.serialization.Serializable
import plat.plantcare.app.core.data.models.toLocalDate
import plat.plantcare.app.core.data.plant.general.domain.Plant

@Serializable
data class PlantDTO(
    val id: String = "",
    val name: String = "",
    val imageUrl: String? = null,
    val location: String = "",
    val birthDate: String = "",        // Null when the entry date was never recorded
    val ownerId: String = "",
    val referenceId: String? = null,
    val favorite: Boolean = false
)

fun Plant.toCommonDTO(): PlantDTO =
    PlantDTO(
        id = id,
        name = name,
        imageUrl = imageUrl,
        location = location,
        birthDate = birthDate?.toString().orEmpty(),
        ownerId = ownerId,
        referenceId = referenceId,
        favorite = favorite
    )

fun PlantDTO.toDomain(): Plant =
    Plant(
        id = id,
        name = name,
        imageUrl = imageUrl,
        location = location,
        birthDate = birthDate.takeIf { it.isNotBlank() }?.toLocalDate(),
        ownerId = ownerId,
        referenceId = referenceId,
        favorite = favorite
    )