package plat.plantcare.app.diagnosis.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/** usuarios/{uid}/plantas/{plantId}/diagnosticos/{id} */
data class DiagnosticFirebaseDTO(
    @DocumentId val id: String = "",
    val plantId: String = "",
    val date: Timestamp? = null,
    val foliageState: String = "",
    val growthVigor: String = "",
    val pestControl: String = "",
    val diseaseSigns: String = "",
    val hasFlowersOrFruits: Boolean? = null,
    val score: Int? = null,
    val assessment: String = "",
    val summary: String = "",
    val possibleCauses: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val limitations: List<String> = emptyList(),
    val model: String = ""
)
