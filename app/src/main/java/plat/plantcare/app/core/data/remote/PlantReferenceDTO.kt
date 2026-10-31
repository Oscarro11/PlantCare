package plat.plantcare.app.core.data.remote

import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.core.data.models.PlantReference

/** Colección: referencias/{id} */
data class PlantReferenceDTO(
    @DocumentId val id: String = "",
    val scientificName: String = "",
    val description: String = ""
)

fun PlantReferenceDTO.toModel(): PlantReference =
    PlantReference(
        id = id,
        scientificName = scientificName,
        description = description
    )