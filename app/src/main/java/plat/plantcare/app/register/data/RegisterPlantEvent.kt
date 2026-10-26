package plat.plantcare.app.register.data

import plat.plantcare.app.register.ui.AttentionLevel
import plat.plantcare.app.register.ui.LightExposure
import plat.plantcare.app.register.ui.PotSize
import plat.plantcare.app.register.ui.WateringFrequency

sealed interface RegisterPlantEvent {
    data class ChangeName(val value: String) : RegisterPlantEvent
    data class SelectLocation(val value: String) : RegisterPlantEvent
    data class ChangeArrivalDate(val value: String) : RegisterPlantEvent
    data class SelectLight(val value: LightExposure) : RegisterPlantEvent
    data class SelectPotSize(val value: PotSize) : RegisterPlantEvent
    data class ChangePotType(val value: String) : RegisterPlantEvent
    data class ChangeSubstrate(val value: String) : RegisterPlantEvent
    data class SelectWateringFrequency(val value: WateringFrequency) : RegisterPlantEvent
    data class ChangeWaterAmount(val value: Float) : RegisterPlantEvent
    data class SelectAttentionLevel(val value: AttentionLevel) : RegisterPlantEvent
    data class SetEasyCare(val value: Boolean) : RegisterPlantEvent
    data class SetAirPurifier(val value: Boolean) : RegisterPlantEvent
    data class SetOwnCutting(val value: Boolean) : RegisterPlantEvent
    data object Save : RegisterPlantEvent
    data object Discard : RegisterPlantEvent
}
