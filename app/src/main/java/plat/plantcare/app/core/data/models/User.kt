package plat.plantcare.app.core.data.models

import plat.plantcare.app.core.data.remote.Firebase.UserFirebaseDTO

// ---------------------------------------------------------------------------
// Data layer: what is stored in Firestore
// ---------------------------------------------------------------------------

/**
 * Colección: usuarios/{uid}
 * El ID es el uid de Firebase Authentication.
 * La contraseña NO se guarda aquí: la gestiona Firebase Auth.
 */

data class User(
    val id: String,
    val name: String,
    val location: String,
    val imageUrl: String,
    val streakDays: Int
)


fun User.toFirebaseDTO(): UserFirebaseDTO =
    UserFirebaseDTO(
        id = id,
        name = name,
        location = location,
        image = imageUrl,
        streakDays = streakDays
    )