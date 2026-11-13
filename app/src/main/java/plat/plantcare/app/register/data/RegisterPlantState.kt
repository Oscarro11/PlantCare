package plat.plantcare.app.register.data

import plat.plantcare.app.register.ui.AttentionLevel
import plat.plantcare.app.register.ui.LightExposure
import plat.plantcare.app.register.ui.PotSize
import plat.plantcare.app.register.ui.WateringFrequency

data class RegisterPlantState(
    val plantName: String = "Monstera Monstrita",
    val location: String = "Salón",
    val arrivalDate: String = "12 de Octubre, 2024",
    val lightExposure: LightExposure = LightExposure.INDIRECT_HIGH,
    val potSize: PotSize = PotSize.MEDIUM,
    val potType: String = "Terracota transpirable (con plato)",
    val substrate: String = "Sustrato tropical aireado (corteza + perlita)",
    val wateringFrequency: WateringFrequency = WateringFrequency.WEEKLY,
    val waterAmount: Float = 0.5f,
    val attentionLevel: AttentionLevel = AttentionLevel.MODERATE,
    val easyCare: Boolean = true,
    val airPurifier: Boolean = true,
    val ownCutting: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)
