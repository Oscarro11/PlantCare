package plat.plantcare.app.core.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/** Colección: plantas/{id} */
data class Plant(
    @DocumentId val id: String = "",
    val name: String = "",            // Nombre personal
    val image: String = "",           // URL de la imagen
    val location: String = "",
    val birthDate: Timestamp? = null, // Fecha de ingreso
    val ownerId: String = "",         // FK -> Usuario.id (Dueño)
    val referenceId: String = ""      // FK -> ReferenciaPlanta.id
)