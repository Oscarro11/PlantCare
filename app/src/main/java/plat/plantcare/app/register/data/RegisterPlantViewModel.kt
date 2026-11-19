package plat.plantcare.app.register.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.plantcare.app.core.data.plant.PlantRepository
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantCharacteristics
import plat.plantcare.app.core.data.plant.general.domain.NewPlant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterPlantViewModel @Inject constructor(
    private val plantRepository: PlantRepository
) : ViewModel() {

    private var registrationId = UUID.randomUUID().toString()
    private val _state = MutableStateFlow(RegisterPlantState())
    val state = _state.asStateFlow()

    fun handleEvent(event: RegisterPlantEvent) {
        when (event) {
            is RegisterPlantEvent.ChangeDraft -> changeDraft(event.draft)
            RegisterPlantEvent.Save -> savePlant()
            RegisterPlantEvent.Discard -> if (!_state.value.isSaving) {
                registrationId = UUID.randomUUID().toString()
                _state.value = RegisterPlantState()
            }
        }
    }

    private fun changeDraft(draft: RegisterPlantDraft) {
        if (_state.value.isSaving || _state.value.saved) return
        _state.update { it.copy(draft = draft, error = null, saved = false) }
    }

    private fun savePlant() {
        if (_state.value.isSaving || _state.value.saved) return
        val draft = _state.value.draft

        if (!draft.isValid) {
            _state.update { it.copy(error = "Ingresa el nombre de la planta") }
            return
        }

        _state.update { it.copy(isSaving = true, saved = false, error = null) }

        viewModelScope.launch {
            try {
                val result = plantRepository.addPlant(
                    plantId = registrationId,
                    newPlant = NewPlant(
                        name = draft.plantName,
                        imageUrl = draft.imageUrl.orEmpty(),
                        location = draft.location,
                        birthDate = draft.arrivalDate
                    ),
                    characteristics = PlantCharacteristics(
                        plantId = "",
                        lightExposure = draft.lightExposure,
                        wateringFrequencyDays = draft.wateringFrequency,
                        waterVolume = draft.waterVolume,
                        humidityLevel = draft.humidity,
                        currentClimate = draft.currentClimate,
                        potSize = draft.potSize,
                        potType = draft.potType,
                        substrate = draft.substrate,
                        attentionLevel = draft.attentionLevel
                    )
                )

                _state.update { it.copy(isSaving = false, saved = true, error = null,
                    warning = if (result.careUnavailable) "Planta guardada. Las pautas de cuidado con IA no están disponibles por ahora." else null) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        saved = false,
                        error = "No se pudo confirmar el guardado. Revisa la conexión y vuelve a intentarlo; no se duplicará la planta."
                    )
                }
            }
        }
    }
}
