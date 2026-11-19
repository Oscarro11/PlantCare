package plat.plantcare.app.collection.plantDetails.data

import kotlinx.coroutines.CancellationException
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.plantcare.app.collection.plantDetails.ui.PlantDetailsDestination
import plat.plantcare.app.diagnosis.domain.Diagnostic
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.plant.PlantRepository
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantCharacteristics
import plat.plantcare.app.core.data.plant.considerations.domain.PlantConsiderations
import plat.plantcare.app.core.data.plant.general.domain.Plant
import plat.plantcare.app.core.data.repositories.TaskRepository
import plat.plantcare.app.diagnosis.application.ObservePlantDiagnosis
import plat.plantcare.app.diagnosis.domain.DiagnosisDetails
import javax.inject.Inject

sealed interface PlantDetailsEvent{
    data class SavePlant(val name: String, val location: String) : PlantDetailsEvent
    data object ToggleFavorite: PlantDetailsEvent
    data object ToggleEditionMode: PlantDetailsEvent
    data object ClickMoreOptions: PlantDetailsEvent
    data class CompleteTask(
        val id: String
    ): PlantDetailsEvent
}

//These classes are used to group flows of data, so that state doesn't have to keep too many
private data class PlantInfo(
    val plant: Plant? = null,
    val characteristics: PlantCharacteristics? = null,
    val considerations: PlantConsiderations? = null,
    val diagnostic: Diagnostic? = null,
    val diagnosticError: String? = null
)

private data class DiagnosisObservation(val details: DiagnosisDetails = DiagnosisDetails(), val error: String? = null)

private data class PlantDetailsLocalState(
    //TODO: see what to do on edition mode, and more options
    val isEditionModeActive: Boolean = false,
    val saving: Boolean = false,
    val error: String? = null,
    val areMoreOptionsOpened: Boolean = false
)

@HiltViewModel
class PlantDetailsViewModel @Inject constructor (
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TaskRepository,
    private val plantRepository: PlantRepository,
    private val observePlantDiagnosis: ObservePlantDiagnosis
): ViewModel() {

    private val destination = savedStateHandle.toRoute<PlantDetailsDestination>()
    private val plantId = destination.id

    private val _localState = MutableStateFlow(PlantDetailsLocalState())
    private val plant: Flow<Plant?> = plantRepository.observePlant(plantId)

    private val diagnosis = observePlantDiagnosis(plantId)
        .map { DiagnosisObservation(it) }
        .catch { emit(DiagnosisObservation(error = "No se pudo cargar el diagnóstico. Revisa la conexión.")) }

    private val plantInfo: Flow<PlantInfo> = combine(
        plant,
        plantRepository.observeCharacteristics(plantId),
        plantRepository.observeConsiderations(plantId),
        diagnosis
    ) { plant, characteristics, considerations, observation ->
        PlantInfo(plant, characteristics, considerations, observation.details.latest, observation.error) }


    private val plantTask: Flow<Task?> = tasksRepository.observeAllTasks()
        .map { tasks ->
            tasks.filter { it.plantId == plantId && !it.completed }
                .minByOrNull { it.dueDate }
        }

    private val history: Flow<List<Task>?> = tasksRepository.observeAllTasks()
        .map { tasks ->
            tasks.filter { it.plantId == plantId && it.completed }
                .sortedBy { it.dueDate }
        }

    val state: StateFlow<PlantDetailsState> = combine(
        plantInfo,
        plantTask,
        history,
        _localState
    ) { plantGroup, task, history, local ->
        if (plantGroup.plant == null) PlantDetailsState.NotFound
        else {
            PlantDetailsState.Content(
                plant = plantGroup.plant,
                reference = null,
                nextTask = task,
                characteristics = plantGroup.characteristics,
                diagnostic = plantGroup.diagnostic,
                diagnosticError = plantGroup.diagnosticError,
                considerations = plantGroup.considerations,
                history = history,
                editionMode = local.isEditionModeActive,
                saving = local.saving, error = local.error,
                isMoreOptionsActive = local.areMoreOptionsOpened
            )
        }
    }
        .catch {  }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlantDetailsState.Loading
        )

    fun onEvent(event: PlantDetailsEvent) {
        when (event) {
            is PlantDetailsEvent.SavePlant -> savePlant(event.name, event.location)
            is PlantDetailsEvent.ToggleFavorite -> changeFavorite()
            is PlantDetailsEvent.CompleteTask -> completeTask(event.id)
            is PlantDetailsEvent.ClickMoreOptions -> clickMoreOptions()
            is PlantDetailsEvent.ToggleEditionMode -> toggleEditionMode()
        }
    }

    private fun changeFavorite() {
        viewModelScope.launch {
            val plant = plant.first()

            try { plantRepository.setFavorite(plantId, if (plant != null) !plant.favorite else false) }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { _localState.update { it.copy(error = "No se pudo guardar el favorito.") } }
        }
    }

    private fun completeTask(id: String) {
        viewModelScope.launch {
            try { tasksRepository.completeTask(id) }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { _localState.update { it.copy(error = "No se pudo completar la tarea.") } }
        }
    }

    private fun clickMoreOptions() {
        _localState.update { state ->
            state.copy(
                areMoreOptionsOpened = !state.areMoreOptionsOpened
            )
        }
    }

    private fun savePlant(name: String, location: String) {
        if (_localState.value.saving) return
        if (name.isBlank() || name.trim().length > 60 || location.trim().length > 60) {
            _localState.update { it.copy(error = "Escribe un nombre y usa un máximo de 60 caracteres por campo.") }; return
        }
        val current = (state.value as? PlantDetailsState.Content)?.plant ?: return
        _localState.update { it.copy(saving = true, error = null) }
        viewModelScope.launch {
            try {
                plantRepository.updatePlant(current.copy(name = name.trim(), location = location.trim()))
                _localState.update { it.copy(saving = false, isEditionModeActive = false) }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { _localState.update { it.copy(saving = false, error = "No se pudieron guardar los cambios.") } }
        }
    }

    private fun toggleEditionMode() {
        if (_localState.value.saving) return
        _localState.update { state ->
            state.copy(
                isEditionModeActive = !state.isEditionModeActive
            )
        }
    }
}