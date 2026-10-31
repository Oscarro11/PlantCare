package plat.plantcare.app.core.data.models

import plat.plantcare.app.core.data.remote.PlantReferenceDTO

data class PlantReference(
    val id: String,
    val scientificName: String,
    val description: String
)

fun PlantReference.toDTO(): PlantReferenceDTO =
    PlantReferenceDTO(
        id = id,
        scientificName = scientificName,
        description = description
    )