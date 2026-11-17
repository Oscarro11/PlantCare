package plat.plantcare.app.home.home.data

import plat.plantcare.app.core.data.plant.general.domain.Plant
import plat.plantcare.app.core.data.models.Task
fun Task.toTaskCard(plants: Map<String, Plant>): HomeTaskCardUiState {
    val plant = plants[plantId]
    return HomeTaskCardUiState(id, urgency(), completed, action, description,
        plant?.name ?: plantName.ifBlank { "Planta " + plantId },
        plant?.imageUrl ?: plantImageUrl, plant?.location ?: plantLocation,
        dueDate, completedAt, isSynced)
}
