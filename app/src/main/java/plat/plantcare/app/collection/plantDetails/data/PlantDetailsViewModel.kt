package plat.plantcare.app.collection.plantDetails.data

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.plantcare.app.collection.plantDetails.ui.PlantDetailsDestination
import plat.plantcare.app.core.data.models.Diagnostic
import plat.plantcare.app.core.data.models.Plant
import plat.plantcare.app.core.data.models.PlantCharacteristics
import plat.plantcare.app.core.data.models.PlantReference
import plat.plantcare.app.core.data.models.PlantReferenceConsiderations
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.repositories.PlantReferenceRepository
import plat.plantcare.app.core.data.repositories.PlantRepository
import plat.plantcare.app.core.data.repositories.TaskRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

sealed interface PlantDetailsEvent{
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
    val diagnostic: Diagnostic? = null
)

private data class ReferenceInfo(
    val reference: PlantReference? = null,
    val considerations: PlantReferenceConsiderations? = null
)

private data class PlantDetailsLocalState(
    //TODO: see what to do on edition mode, and more options
    val isEditionModeActive: Boolean = false,
    val areMoreOptionsOpened: Boolean = false
)

@HiltViewModel
class PlantDetailsViewModel @Inject constructor (
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TaskRepository,
    private val plantRepository: PlantRepository,
    private val referencesRepository: PlantReferenceRepository
): ViewModel() {

    private val destination = savedStateHandle.toRoute<PlantDetailsDestination>()
    private val plantId = destination.id

    private val _localState = MutableStateFlow(PlantDetailsLocalState())
    private val plant: Flow<Plant?> = plantRepository.observePlant(plantId)

    private val referenceId: Flow<String?> = plant
        .map { it?.referenceId?.takeIf { id -> id.isNotBlank() } }
        .distinctUntilChanged()

    private val plantInfo: Flow<PlantInfo> = combine(
        plant,
        plantRepository.observeCharacteristics(plantId),
        plantRepository.observeDiagnostic(plantId)
    ) { plant, characteristics, diagnostic -> PlantInfo(plant, characteristics, diagnostic) }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val referenceInfo: Flow<ReferenceInfo> = referenceId.flatMapLatest { id ->
        if (id == null) flowOf(ReferenceInfo())
        else combine(
            referencesRepository.observePlantReference(id),
            referencesRepository.observePlantConsiderations(id)
        ) { reference, considerations -> ReferenceInfo(reference, considerations) }
    }

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
        referenceInfo,
        plantTask,
        history,
        _localState
    ) { plantGroup, referenceGroup, task, history, local ->
        if (plantGroup.plant == null) PlantDetailsState.NotFound
        else {
            PlantDetailsState.Content(
                plant = plantGroup.plant,
                reference = referenceGroup.reference,
                nextTask = task,
                characteristics = plantGroup.characteristics,
                diagnostic = plantGroup.diagnostic,
                considerations = referenceGroup.considerations,
                history = history,
                editionMode = local.isEditionModeActive,
                isMoreOptionsActive = local.areMoreOptionsOpened
            )
        }
    }
        //TODO: remove fake delay
        .onStart { delay(3_000.milliseconds) }
        .catch {  }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlantDetailsState.Loading
        )

    fun onEvent(event: PlantDetailsEvent) {
        when (event) {
            is PlantDetailsEvent.ToggleFavorite -> changeFavorite()
            is PlantDetailsEvent.CompleteTask -> completeTask(event.id)
            is PlantDetailsEvent.ClickMoreOptions -> clickMoreOptions()
            is PlantDetailsEvent.ToggleEditionMode -> toggleEditionMode()
        }
    }

    private fun changeFavorite() {
        viewModelScope.launch {
            plantRepository.togglePlantFavorite(plantId)
        }
    }

    private fun completeTask(id: String) {
        viewModelScope.launch {
            tasksRepository.setTaskCompleted(id, true)
        }
    }

    private fun clickMoreOptions() {
        _localState.update { state ->
            state.copy(
                areMoreOptionsOpened = !state.areMoreOptionsOpened
            )
        }
    }

    private fun toggleEditionMode() {
        _localState.update { state ->
            state.copy(
                isEditionModeActive = !state.isEditionModeActive
            )
        }
    }
}