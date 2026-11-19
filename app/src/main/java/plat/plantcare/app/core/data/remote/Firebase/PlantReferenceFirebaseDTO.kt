package plat.plantcare.app.core.data.remote.Firebase

import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.core.data.models.PlantReference

/** Colección: referencias/{id} */
data class PlantReferenceFirebaseDTO(
    @DocumentId val id: String = "",
    val scientificName: String = "",
    val description: String = ""
)

fun PlantReferenceFirebaseDTO.toModel(): PlantReference =
    PlantReference(
        id = id,
        scientificName = scientificName,
        description = description
    )