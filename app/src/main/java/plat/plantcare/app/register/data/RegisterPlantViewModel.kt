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
import plat.plantcare.app.core.data.plant.domain.NewPlant
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RegisterPlantViewModel @Inject constructor(
    private val plantRepository: PlantRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterPlantState())
    val state = _state.asStateFlow()

    fun handleEvent(event: RegisterPlantEvent) {
        when (event) {
            is RegisterPlantEvent.ChangeDraft -> changeDraft(event.draft)
            RegisterPlantEvent.Save -> savePlant()
            RegisterPlantEvent.Discard -> _state.value = RegisterPlantState()
        }
    }

    private fun changeDraft(draft: RegisterPlantDraft) {
        _state.update { it.copy(draft = draft, error = null, saved = false) }
    }

    private fun savePlant() {
        val draft = _state.value.draft

        if (!draft.isValid) {
            _state.update { it.copy(error = "Ingresa el nombre de la planta") }
            return
        }

        val arrivalDate = try {
            draft.arrivalDate.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
                ?: LocalDate.now()
        } catch (e: Exception) {
            _state.update {
                it.copy(error = "La fecha debe tener el formato YYYY-MM-DD")
            }
            return
        }

        _state.update { it.copy(isSaving = true, saved = false, error = null) }

        viewModelScope.launch {
            try {
                plantRepository.addPlant(
                    NewPlant(
                        name = draft.plantName,
                        imageUrl = draft.imageUrl.orEmpty(),
                        location = draft.location,
                        birthDate = arrivalDate
                    )
                )

                _state.update { it.copy(isSaving = false, saved = true, error = null) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        saved = false,
                        error = e.message ?: "Error al guardar la planta"
                    )
                }
            }
        }
    }
}
