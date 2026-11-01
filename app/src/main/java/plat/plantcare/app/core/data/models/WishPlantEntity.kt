package plat.plantcare.app.core.data.models

import plat.plantcare.app.core.data.remote.Firebase.WishPlantDTO

data class WishPlant(
    val id: String,
    val userId: String,
    val referenceId: String
)

fun WishPlant.toFirebaseDTO(): WishPlantDTO =
    WishPlantDTO(
        id = id,
        userId = userId,
        referenceId = referenceId
    )