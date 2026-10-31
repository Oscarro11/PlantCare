package plat.plantcare.app.core.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import plat.plantcare.app.core.data.models.Diagnostic
import plat.plantcare.app.core.data.models.toLocalDate
import java.time.ZoneId

/** Colección: plantas/{plantId}/diagnosticos/{id} */
data class DiagnosticDTO(
    @DocumentId val id: String = "",
    val plantId: String = "",
    val date: Timestamp? = null,
    val foliageState: String = "",
    val growthVigor: String = "",
    val pestControl: String = "",
    val diseaseSigns: String = "",
    val hasFlowersOrFruits: Boolean = false,
    val score: Int = 0
)

fun DiagnosticDTO.toModel(zone: ZoneId = ZoneId.systemDefault()): Diagnostic?{
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