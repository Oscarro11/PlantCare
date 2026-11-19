package plat.plantcare.app.register.data

sealed interface RegisterPlantEvent {
    data class ChangeDraft(val draft: RegisterPlantDraft) : RegisterPlantEvent
    data object Save : RegisterPlantEvent
    data object Discard : RegisterPlantEvent
}
