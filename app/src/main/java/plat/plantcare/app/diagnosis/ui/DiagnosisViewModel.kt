package plat.plantcare.app.diagnosis.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.plantcare.app.diagnosis.domain.Diagnostic
import plat.plantcare.app.core.data.plant.general.domain.Plant
import plat.plantcare.app.diagnosis.domain.DiagnosisDraft
import plat.plantcare.app.diagnosis.domain.DiagnosisPhotos
import plat.plantcare.app.diagnosis.application.AnalyzePlant
import plat.plantcare.app.diagnosis.application.SaveDiagnosis
import plat.plantcare.app.diagnosis.application.ObservePlantsForDiagnosis
import plat.plantcare.app.core.ai.gemini.AiAuthorizationException
import javax.inject.Inject

@HiltViewModel
class DiagnosisViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val observePlants: ObservePlantsForDiagnosis,
    private val analyzePlant: AnalyzePlant,
    private val saveDiagnosis: SaveDiagnosis,
    private val images: DiagnosisPhotos
) : ViewModel() {
    val fixedPlantId = savedState.toRoute<DiagnosisDestination>().plantId
    private val mutable = MutableStateFlow(DiagnosisState(
        selectedId = savedState["selectedPlant"] ?: fixedPlantId,
        photo = savedState["diagnosisPhoto"]
    ))
    val state = mutable.asStateFlow()
    private var observation: Job? = null
    private var analyzedDraft: DiagnosisDraft? = null
    init { reloadPlants() }

    fun reloadPlants() {
        observation?.cancel()
        mutable.update { it.copy(loadingPlants = true, error = null) }
        observation = viewModelScope.launch {
            observePlants().catch {
                mutable.update { it.copy(loadingPlants = false, error = "No se pudo cargar tu colección. Inténtalo otra vez.") }
            }.collect { list -> mutable.update { it.copy(plants = list, loadingPlants = false) } }
        }
    }

    fun selectPlant(id: String?) {
        if (state.value.busy || state.value.selectedId == id) return
        val previous = state.value.photo
        savedState["selectedPlant"] = id
        savedState["diagnosisPhoto"] = null
        analyzedDraft = null
        mutable.update { it.copy(selectedId = id, photo = null, result = null, saved = false, error = null) }
        if (previous != null) viewModelScope.launch { images.delete(previous) }
    }

    fun choosePhoto(uri: String) {
        if (state.value.busy) return
        mutable.update { it.copy(phase = DiagnosisPhase.IMPORTING, error = null) }
        viewModelScope.launch {
            try {
                val imported = images.importPhoto(uri)
                val previous = state.value.photo
                savedState["diagnosisPhoto"] = imported
                analyzedDraft = null
                mutable.update { it.copy(photo = imported, result = null, saved = false) }
                if (previous != null) images.delete(previous)
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { fail("No se pudo abrir la foto. Elige otra imagen.", e) }
            finally { mutable.update { it.copy(phase = DiagnosisPhase.IDLE) } }
        }
    }

    fun cameraError() { mutable.update { it.copy(error = "No se pudo abrir la cámara. Puedes elegir una foto de la galería.") } }

    fun analyze() {
        val snapshot = state.value
        val plant = snapshot.selectedPlant ?: return
        val photo = snapshot.photo ?: return
        if (snapshot.busy || snapshot.result != null) return
        mutable.update { it.copy(phase = DiagnosisPhase.ANALYZING, error = null) }
        viewModelScope.launch {
            try {
                val draft = analyzePlant(plant, photo)
                analyzedDraft = draft
                mutable.update { it.copy(result = draft.diagnostic, saved = false) }
            } catch (e: TimeoutCancellationException) { fail("El análisis tardó demasiado. Revisa la conexión y vuelve a intentarlo.", e) }
            catch (e: CancellationException) { throw e }
            catch (e: IllegalArgumentException) { fail(e.message ?: "La foto no se pudo evaluar. Prueba con otra.", e) }
            catch (e: AiAuthorizationException) { fail("El servicio de IA no está autorizado en este dispositivo. Tu planta sigue guardada.", e) }
            catch (e: Exception) { fail("No se pudo completar el análisis. Comprueba la conexión. Si persiste, el servicio puede no estar disponible.", e) }
            finally { mutable.update { it.copy(phase = DiagnosisPhase.IDLE) } }
        }
    }

    fun save() {
        val snapshot = state.value
        if (snapshot.result == null) return
        val draft = analyzedDraft ?: return
        if (snapshot.busy || snapshot.saved) return
        mutable.update { it.copy(phase = DiagnosisPhase.SAVING, error = null) }
        viewModelScope.launch {
            try {
                saveDiagnosis(draft)
                mutable.update { it.copy(saved = true) }
            } catch (e: TimeoutCancellationException) { fail("No se confirmó el guardado. Revisa la conexión y vuelve a pulsar Guardar; no se duplicará el resultado.", e) }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { fail("No se pudo guardar el diagnóstico. Conservamos el resultado para que puedas reintentarlo.", e) }
            finally { mutable.update { it.copy(phase = DiagnosisPhase.IDLE) } }
        }
    }

    private fun fail(message: String, error: Exception) {
        Log.w("PlantDiagnosis", message, error)
        mutable.update { it.copy(error = message) }
    }

    override fun onCleared() {
        state.value.photo?.let { photo -> CoroutineScope(Dispatchers.IO).launch { runCatching { images.delete(photo) } } }
        super.onCleared()
    }
}
