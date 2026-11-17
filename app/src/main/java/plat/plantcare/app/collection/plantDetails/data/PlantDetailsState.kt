package plat.plantcare.app.collection.plantDetails.data

import plat.plantcare.app.diagnosis.domain.Diagnostic
import plat.plantcare.app.core.data.models.PlantReference
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantCharacteristics
import plat.plantcare.app.core.data.plant.considerations.domain.PlantConsiderations
import plat.plantcare.app.core.data.plant.general.domain.Plant

sealed interface PlantDetailsState {
    data object Loading : PlantDetailsState
    data object NotFound : PlantDetailsState
    data class Content(
        val plant: Plant,
        val reference: PlantReference? = null,
        val nextTask: Task? = null,
        val characteristics: PlantCharacteristics? = null,
        val diagnostic: Diagnostic? = null,
        val diagnosticError: String? = null,
        val considerations: PlantConsiderations? = null,
        val history: List<Task>? = emptyList(),
        val editionMode: Boolean = false,
        val isMoreOptionsActive: Boolean = false
    ) : PlantDetailsState
}