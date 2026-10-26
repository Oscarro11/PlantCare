package plat.plantcare.app.core.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.time.LocalDate
import java.time.ZoneId

/** Colección: plantas/{plantId}/diagnosticos/{id} */
data class DiagnosticDTO(
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

fun DiagnosticDTO.toDomain(zone: ZoneId = ZoneId.systemDefault()): Diagnostic?{
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

fun Diagnostic.toDTO(zone: ZoneId = ZoneId.systemDefault()): DiagnosticDTO{
    return DiagnosticDTO(
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