package plat.plantcare.app.register.data

sealed interface RegisterPlantEvent {
    data class ChangeDraft(val draft: RegisterPlantDraft) : RegisterPlantEvent
    data object IdentifyPhoto : RegisterPlantEvent
    data class SelectSpecies(val scientificName: String) : RegisterPlantEvent
    data object Save : RegisterPlantEvent
    data object Discard : RegisterPlantEvent
}
