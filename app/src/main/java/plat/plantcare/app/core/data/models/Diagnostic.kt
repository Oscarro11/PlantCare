package plat.plantcare.app.core.data.models

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import plat.plantcare.app.R
import plat.plantcare.app.core.data.remote.Firebase.DiagnosticFirebaseDTO
import java.time.LocalDate
import java.time.ZoneId

data class Diagnostic(
    val id: String,
    val plantId: String,
    val date: LocalDate,
    val foliageState: String,
    val growthVigor: String,
    val pestControl: String,
    val diseaseSigns: String,
    val hasFlowersOrFruits: Boolean,
    val score: Int
)

enum class HealthStatus {
    SALUDABLE,
    NECESITA_CUIDADO,
    EN_PELIGRO,
    MUERTA
}

fun Diagnostic.toFirebaseDTO(zone: ZoneId = ZoneId.systemDefault()): DiagnosticFirebaseDTO{
    return DiagnosticFirebaseDTO(
        id = id,
        plantId = plantId,
        date = date.toTimestamp(zone),
        foliageState = foliageState,
        growthVigor = growthVigor,
        pestControl = pestControl,
        diseaseSigns = diseaseSigns,
        hasFlowersOrFruits = hasFlowersOrFruits,
        score = score
    )
}

fun Diagnostic.getHealthStatus(): HealthStatus{
    return when {
        score < 25 -> HealthStatus.MUERTA
        score in 25 .. 49 -> HealthStatus.EN_PELIGRO
        score in 50 .. 75 -> HealthStatus.NECESITA_CUIDADO
        else -> HealthStatus.SALUDABLE
    }
}

fun HealthStatus.icon(): Int = when (this) {
    HealthStatus.SALUDABLE -> R.drawable.verified_24dp_000000_fill0_wght400_grad0_opsz24
    HealthStatus.NECESITA_CUIDADO -> R.drawable.waving_hand
    HealthStatus.EN_PELIGRO -> R.drawable.danger
    HealthStatus.MUERTA -> R.drawable.blocked
}

fun HealthStatus.label(): String = when (this) {
    HealthStatus.SALUDABLE -> "Saludable"
    HealthStatus.NECESITA_CUIDADO -> "Necesita cuidado"
    HealthStatus.EN_PELIGRO -> "En peligro"
    HealthStatus.MUERTA -> "Muerta"
}

@Composable
fun HealthStatus.color(): Color = when (this) {
    HealthStatus.SALUDABLE -> MaterialTheme.colorScheme.primary
    HealthStatus.NECESITA_CUIDADO -> Color(0xFFFFA000)
    HealthStatus.EN_PELIGRO -> MaterialTheme.colorScheme.error
    HealthStatus.MUERTA -> Color.Gray
}