package plat.plantcare.app.core.data

import com.google.firebase.firestore.DocumentId

/** Colección: plantasDeseadas/{id} */
data class WishPlant(
    @DocumentId val id: String = "",
    val userId: String = "",       // FK -> Usuario.id
    val referenceId: String = ""   // FK -> ReferenciaPlanta.id (Referencia de planta)
)