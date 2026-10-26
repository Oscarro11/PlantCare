package plat.plantcare.app.core.data.models

import com.google.firebase.firestore.DocumentId

// ---------------------------------------------------------------------------
// Data layer: what is stored in Firestore
// ---------------------------------------------------------------------------

/**
 * Colección: usuarios/{uid}
 * El ID es el uid de Firebase Authentication.
 * La contraseña NO se guarda aquí: la gestiona Firebase Auth.
 */
data class UserDto(
    @DocumentId val id: String = "",
    val name: String = "",
    val location: String = "",
    val image: String = "",      // URL de la imagen
    val streakDays: Int = 0      // Días de racha
)

// ---------------------------------------------------------------------------
// Domain layer: what the rest of the app uses
// ---------------------------------------------------------------------------

data class User(
    val id: String,
    val name: String,
    val location: String,
    val imageUrl: String,
    val streakDays: Int
)

// ---------------------------------------------------------------------------
// Conversions (DTO <-> domain)
// ---------------------------------------------------------------------------

fun UserDto.toDomain(): User =
    User(
        id = id,
        name = name,
        location = location,
        imageUrl = image,
        streakDays = streakDays
    )

fun User.toDto(): UserDto =
    UserDto(
        id = id,
        name = name,
        location = location,
        image = imageUrl,
        streakDays = streakDays
    )