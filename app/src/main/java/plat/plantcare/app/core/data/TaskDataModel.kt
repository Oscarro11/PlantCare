package plat.plantcare.app.core.data

import androidx.annotation.DrawableRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.R
import plat.plantcare.app.core.darkenColor

/** Colección: plantas/{plantId}/tareas/{id} */
data class Task(
    @DocumentId val id: String = "",
    val plantId: String = "",
    val dueDate: Timestamp? = null,  // Fecha límite
    val action: TaskAction = TaskAction.OTHER,         // Acción
    val description: String = "",
    val completed: Boolean = false,
    val urgency: TaskUrgency = TaskUrgency.URGENTE
)

enum class TaskUrgency {
    URGENTE,
    PRIORITARIO,
    HOY
}

//TODO: update icons for each task action
enum class TaskAction(
    @DrawableRes val iconId: Int
) {
    WATER (iconId = R.drawable.water_drop),
    MIST (iconId = R.drawable.water_drop),
    CLEAN_LEAVES (iconId = R.drawable.water_drop),
    PRUNE (iconId = R.drawable.water_drop),
    FERTILIZE (iconId = R.drawable.water_drop),
    ROTATE (iconId = R.drawable.water_drop),
    REPOT (iconId = R.drawable.water_drop),
    CHECK_SOIL_MOISTURE (iconId = R.drawable.water_drop),
    CHECK_FOR_PESTS (iconId = R.drawable.water_drop),
    TREAT_FOR_PESTS (iconId = R.drawable.water_drop),
    PROPAGATE (iconId = R.drawable.water_drop),
    OTHER (iconId = R.drawable.water_drop)
}

@Composable
fun TaskUrgency.backgroundColor(): Color = when (this) {
    TaskUrgency.URGENTE -> Color.Red
    TaskUrgency.PRIORITARIO -> darkenColor(MaterialTheme.colorScheme.primary)
    TaskUrgency.HOY -> MaterialTheme.colorScheme.primary
}

@Composable
fun TaskUrgency.contentColor(): Color = when (this) {
    TaskUrgency.URGENTE -> Color.White
    TaskUrgency.PRIORITARIO,
    TaskUrgency.HOY -> MaterialTheme.colorScheme.onPrimary
}