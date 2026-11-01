package plat.plantcare.app.core.data.models

import plat.plantcare.app.core.data.remote.Firebase.PlantReferenceFirebaseDTO

data class PlantReference(
    val id: String,
    val scientificName: String,
    val description: String
)

fun PlantReference.toFirebaseDTO(): PlantReferenceFirebaseDTO =
    PlantReferenceFirebaseDTO(
        id = id,
        scientificName = scientificName,
        description = description
    )