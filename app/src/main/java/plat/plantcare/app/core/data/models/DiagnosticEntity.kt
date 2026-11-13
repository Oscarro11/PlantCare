package plat.plantcare.app.core.data.models

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.R
import java.time.LocalDate
import java.time.ZoneId

/** Colección: plantas/{plantId}/diagnosticos/{id} */
data class DiagnosticEntity(
    @DocumentId val id: String = "",
    val plantId: String = "",                // FK -> Planta.id
    val date: Timestamp? = null,             // Fecha de realización
    val foliageState: String = "",           // Estado de follaje
    val growthVigor: String = "",            // Vigor y crecimiento
    val pestControl: String = "",            // Control de plagas
    val diseaseSigns: String = "",           // Signos de enfermedades
    val hasFlowersOrFruits: Boolean = false, // Flores o frutos
    val score: Int = 0                       // Puntaje
)

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

fun DiagnosticEntity.toDomain(zone: ZoneId = ZoneId.systemDefault()): Diagnostic?{
    val date = date?.toLocalDate(zone) ?: return null
    return Diagnostic(
        id = id,
        plantId = plantId,
        date = date,
        foliageState = foliageState,
        growthVigor = growthVigor,
        pestControl = pestControl,
        diseaseSigns = diseaseSigns,
        hasFlowersOrFruits = hasFlowersOrFruits,
        score = score
    )
}

fun Diagnostic.toEntity(zone: ZoneId = ZoneId.systemDefault()): DiagnosticEntity{
    return DiagnosticEntity(
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