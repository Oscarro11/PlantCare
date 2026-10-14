package plat.plantcare.app.core.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/** Colección: plantas/{plantId}/diagnosticos/{id} */
data class Diagnostic(
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