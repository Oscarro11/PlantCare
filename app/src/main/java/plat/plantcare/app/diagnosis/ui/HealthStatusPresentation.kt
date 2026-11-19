package plat.plantcare.app.diagnosis.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import plat.plantcare.app.R
import plat.plantcare.app.diagnosis.domain.HealthStatus

fun HealthStatus.icon(): Int = when (this) {
    HealthStatus.SALUDABLE -> R.drawable.verified_24dp_000000_fill0_wght400_grad0_opsz24
    HealthStatus.NECESITA_CUIDADO -> R.drawable.waving_hand
    HealthStatus.EN_PELIGRO -> R.drawable.danger
    HealthStatus.MUERTA -> R.drawable.blocked
    HealthStatus.NO_EVALUABLE -> R.drawable.monitor
}
fun HealthStatus.label(): String = when (this) {
    HealthStatus.SALUDABLE -> "Aspecto saludable"
    HealthStatus.NECESITA_CUIDADO -> "Necesita cuidado"
    HealthStatus.EN_PELIGRO -> "Signos de deterioro"
    HealthStatus.MUERTA -> "Muerta"
    HealthStatus.NO_EVALUABLE -> "No evaluable"
}
@Composable fun HealthStatus.color(): Color = when (this) {
    HealthStatus.SALUDABLE -> MaterialTheme.colorScheme.primary
    HealthStatus.NECESITA_CUIDADO -> MaterialTheme.colorScheme.tertiary
    HealthStatus.EN_PELIGRO -> MaterialTheme.colorScheme.error
    HealthStatus.MUERTA, HealthStatus.NO_EVALUABLE -> MaterialTheme.colorScheme.onSurfaceVariant
}
