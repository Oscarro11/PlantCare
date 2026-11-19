package plat.plantcare.app.core.data.remote.Firebase

import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.core.data.models.WishPlant

/** Colección: plantasDeseadas/{id} */
data class WishPlantDTO(
    @DocumentId val id: String = "",
    val userId: String = "",       // FK -> Usuario.id
    val referenceId: String = ""   // FK -> ReferenciaPlanta.id (Referencia de planta)
)

fun WishPlantDTO.toModel(): WishPlant =
    WishPlant(
        id = id,
        userId = userId,
        referenceId = referenceId
    )