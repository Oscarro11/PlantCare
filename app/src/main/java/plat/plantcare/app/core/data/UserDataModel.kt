package plat.plantcare.app.core.data

import com.google.firebase.firestore.DocumentId

/**
 * Colección: usuarios/{uid}
 * El ID es el uid de Firebase Authentication.
 * La contraseña NO se guarda aquí: la gestiona Firebase Auth.
 */
data class User(
    @DocumentId val id: String = "",
    val name: String = "",
    val location: String = "",
    val image: String = "",      // URL de la imagen
    val streakDays: Int = 0      // Días de racha
)