package plat.plantcare.app.core.data.models

import plat.plantcare.app.core.data.remote.WishPlantDTO

data class WishPlant(
    val id: String,
    val userId: String,
    val referenceId: String
)

fun WishPlant.toDTO(): WishPlantDTO =
    WishPlantDTO(
        id = id,
        userId = userId,
        referenceId = referenceId
    )