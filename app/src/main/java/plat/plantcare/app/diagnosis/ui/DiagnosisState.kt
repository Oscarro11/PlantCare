package plat.plantcare.app.diagnosis.ui

import plat.plantcare.app.core.data.plant.domain.Plant
import plat.plantcare.app.diagnosis.domain.Diagnostic

enum class DiagnosisPhase { IDLE, IMPORTING, ANALYZING, SAVING }
data class DiagnosisState(
    val plants: List<Plant> = emptyList(),
    val loadingPlants: Boolean = true,
    val selectedId: String? = null,
    val photo: String? = null,
    val phase: DiagnosisPhase = DiagnosisPhase.IDLE,
    val result: Diagnostic? = null,
    val saved: Boolean = false,
    val error: String? = null
) {
    val selectedPlant: Plant? get() = plants.firstOrNull { it.id == selectedId }
    val busy: Boolean get() = phase != DiagnosisPhase.IDLE
}
