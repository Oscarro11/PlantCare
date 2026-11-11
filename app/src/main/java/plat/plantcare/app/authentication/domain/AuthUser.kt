package plat.plantcare.app.authentication.domain

data class AuthUser(
    val uid: String,
    val email: String?,
    val isEmailVerified: Boolean
)

