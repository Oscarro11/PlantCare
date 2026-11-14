package plat.plantcare.app.register.data

import plat.plantcare.app.register.ui.AttentionLevel
import plat.plantcare.app.register.ui.LightExposure
import plat.plantcare.app.register.ui.PotSize
import plat.plantcare.app.register.ui.WateringFrequency

data class RegisterPlantState(
    val saved: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val draft: RegisterPlantDraft = RegisterPlantDraft()
)

data class RegisterPlantDraft(
    val plantName: String = "",
    val imageUrl: String? = null,
    val location: String = "",
    val arrivalDate: String = "",
    val lightExposure: LightExposure = LightExposure.INDIRECT_HIGH,
    val potSize: PotSize = PotSize.MEDIUM,
    val potType: String = "",
    val substrate: String = "",
    val wateringFrequency: WateringFrequency = WateringFrequency.WEEKLY,
    val waterAmount: Float = 0.5f,
    val attentionLevel: AttentionLevel = AttentionLevel.MODERATE,
    val easyCare: Boolean = true,
    val airPurifier: Boolean = true,
    val ownCutting: Boolean = false
) {
    val isValid: Boolean
        get() = plantName.isNotBlank()
}
