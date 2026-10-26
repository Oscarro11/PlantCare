package plat.plantcare.app.collection.plantDetails.data

import plat.plantcare.app.core.data.models.Diagnostic
import plat.plantcare.app.core.data.models.Plant
import plat.plantcare.app.core.data.models.PlantCharacteristics
import plat.plantcare.app.core.data.models.PlantReference
import plat.plantcare.app.core.data.models.PlantReferenceConsiderations
import plat.plantcare.app.core.data.models.Task

enum class Section{
    CHARACTERISTICS,
    DIAGNOSTIC,
    CONSIDERATIONS,
    HISTORY
}

data class PlantDetailsState(
    val plant: Plant? = null,
    val reference: PlantReference? = null,
    val nextTask: Task? = null,
    val characteristics: PlantCharacteristics? = null,
    val diagnostic: Diagnostic? = null,
    val considerations: PlantReferenceConsiderations? = null,
    val history: List<Task>? = emptyList(),
    val openSections: List<Pair<Section, Boolean>> = emptyList(),
    val editionMode: Boolean = false,
    val isMoreOptionsActive: Boolean = false
)