package plat.plantcare.app.core.data.diagnostic.domain

import androidx.annotation.DrawableRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import plat.plantcare.app.R

@DrawableRes
fun HealthStatus.icon(): Int = when (this) {
    HealthStatus.HEALTHY -> R.drawable.verified_24dp_000000_fill0_wght400_grad0_opsz24
    HealthStatus.NEEDS_CARE -> R.drawable.waving_hand
    HealthStatus.ENDANGERED -> R.drawable.danger
    HealthStatus.DEAD -> R.drawable.blocked
}

@Composable
fun HealthStatus.label(): String = stringResource(
    when (this) {
        HealthStatus.HEALTHY -> R.string.health_status_healthy
        HealthStatus.NEEDS_CARE -> R.string.health_status_needs_care
        HealthStatus.ENDANGERED -> R.string.health_status_endangered
        HealthStatus.DEAD -> R.string.health_status_dead
    }
)

@Composable
fun HealthStatus.color(): Color = when (this) {
    HealthStatus.HEALTHY -> MaterialTheme.colorScheme.primary
    HealthStatus.NEEDS_CARE -> Color(0xFFFFA000)
    HealthStatus.ENDANGERED -> MaterialTheme.colorScheme.error
    HealthStatus.DEAD -> Color.Gray
}

@Composable
fun FindingLevel.label(): String = stringResource(
    when (this) {
        FindingLevel.EXCELLENT -> R.string.finding_excellent
        FindingLevel.GOOD -> R.string.finding_good
        FindingLevel.FAIR -> R.string.finding_fair
        FindingLevel.POOR -> R.string.finding_poor
    }
)

@Composable
fun FindingLevel.color(): Color = when (this) {
    FindingLevel.EXCELLENT, FindingLevel.GOOD -> MaterialTheme.colorScheme.primary
    FindingLevel.FAIR -> Color(0xFFFFA000)
    FindingLevel.POOR -> MaterialTheme.colorScheme.error
}