package plat.plantcare.app.core.data

import com.google.firebase.firestore.DocumentId

/** Colección: referencias/{id} */
data class PlantReference(
    @DocumentId val id: String = "",
    val scientificName: String = "",  // Nombre científico
    val description: String = ""      // Descripción
)