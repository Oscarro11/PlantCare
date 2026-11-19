package plat.plantcare.app.core.data.user.domain

import plat.plantcare.app.core.data.user.local.UserEntity
import plat.plantcare.app.core.data.user.remote.UserFirebaseDTO

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
    val streakDays: Int,
    val lastStreakDate: String
)

fun User.toFirebaseDTO(): UserFirebaseDTO =
    UserFirebaseDTO(
        id = id,
        name = name,
        location = location,
        image = imageUrl,
        streakDays = streakDays,
        lastStreakDate = lastStreakDate
    )

fun User.toEntity(): UserEntity =
    UserEntity(id, name, location, imageUrl, streakDays, lastStreakDate)