package plat.plantcare.app.register.data

import plat.plantcare.app.core.data.plant.characteristics.domain.AttentionLevel
import plat.plantcare.app.core.data.plant.characteristics.domain.Climate
import plat.plantcare.app.core.data.plant.characteristics.domain.Humidity
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantLightExposure
import plat.plantcare.app.core.data.plant.characteristics.domain.PlantWateringFrequency
import plat.plantcare.app.core.data.plant.characteristics.domain.PotSize
import java.time.LocalDate

data class RegisterPlantState(
    val saved: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val warning: String? = null,
    val identifying: Boolean = false,
    val identificationResults: List<plat.plantcare.app.core.data.plant.identification.PlantNetResponseDTO> = emptyList(),
    val identificationError: String? = null,
    val draft: RegisterPlantDraft = RegisterPlantDraft()
)

data class RegisterPlantDraft(
    val plantName: String = "",
    val identifiedSpecies: String? = null,
    val imageUrl: String? = null,
    val location: String = "",
    val arrivalDate: LocalDate = LocalDate.now(),
    val lightExposure: PlantLightExposure = PlantLightExposure.BAJA,
    val potSize: PotSize = PotSize.MEDIANA,
    val potType: String = "",
    val substrate: String = "",
    val humidity: Humidity = Humidity.MODERADA,
    val wateringFrequency: PlantWateringFrequency = PlantWateringFrequency.MODERADO,
    val waterVolume: Int = 0,
    val currentClimate: Climate = Climate.TEMPLADO,
    val attentionLevel: AttentionLevel = AttentionLevel.MEDIO
) {
    val isValid: Boolean
        get() = plantName.isNotBlank()
}
