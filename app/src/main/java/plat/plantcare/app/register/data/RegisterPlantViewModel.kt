package plat.plantcare.app.register.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import plat.plantcare.app.core.data.models.Plant
import plat.plantcare.app.core.data.repositories.PlantRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
@HiltViewModel
class RegisterPlantViewModel @Inject constructor(
    private val plantRepository: PlantRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterPlantState())
    val state = _state.asStateFlow()

    fun onEvent(event: RegisterPlantEvent) {
        when (event) {
            RegisterPlantEvent.Save -> savePlant()

            RegisterPlantEvent.Discard -> {
                _state.value = RegisterPlantState()
            }

            else -> {
                _state.update { current ->
                    when (event) {
                        is RegisterPlantEvent.ChangeName ->
                            current.copy(plantName = event.value, error = null)

                        is RegisterPlantEvent.SelectLocation ->
                            current.copy(location = event.value)

                        is RegisterPlantEvent.ChangeArrivalDate ->
                            current.copy(arrivalDate = event.value)

                        is RegisterPlantEvent.SelectLight ->
                            current.copy(lightExposure = event.value)

                        is RegisterPlantEvent.SelectPotSize ->
                            current.copy(potSize = event.value)

                        is RegisterPlantEvent.ChangePotType ->
                            current.copy(potType = event.value)

                        is RegisterPlantEvent.ChangeSubstrate ->
                            current.copy(substrate = event.value)

                        is RegisterPlantEvent.SelectWateringFrequency ->
                            current.copy(wateringFrequency = event.value)

                        is RegisterPlantEvent.ChangeWaterAmount ->
                            current.copy(waterAmount = event.value)

                        is RegisterPlantEvent.SelectAttentionLevel ->
                            current.copy(attentionLevel = event.value)

                        is RegisterPlantEvent.SetEasyCare ->
                            current.copy(easyCare = event.value)

                        is RegisterPlantEvent.SetAirPurifier ->
                            current.copy(airPurifier = event.value)

                        is RegisterPlantEvent.SetOwnCutting ->
                            current.copy(ownCutting = event.value)

                        else -> current
                    }
                }
            }
        }
    }

    private fun savePlant() {
        val current = _state.value

        if (current.plantName.isBlank()) {
            _state.update {
                it.copy(error = "Ingresa el nombre de la planta")
            }
            return
        }

        viewModelScope.launch {
            try {
                val plant = Plant(
                    id = UUID.randomUUID().toString(),
                    name = current.plantName.trim(),
                    imageUrl = "",
                    location = current.location,
                    birthDate = LocalDate.now(),
                    ownerId = "user-1",
                    referenceId = ""
                )

                plantRepository.addPlant(plant)

                _state.update {
                    it.copy(saved = true, error = null)
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        saved = false,
                        error = e.message ?: "Error al guardar la planta"
                    )
                }
            }
        }
    }
}
