package plat.plantcare.app.authentication.remote

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import plat.plantcare.app.authentication.domain.AuthUser

data class AuthUserDto(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean,
    val providerIds: List<String>
)

data class AuthResultDto(val user: AuthUserDto, val isNewUser: Boolean)

fun AuthUserDto.toDomain() = AuthUser(uid, email, isEmailVerified)

sealed class AuthError(cause: Throwable? = null) : Exception(cause) {
    class InvalidCredentials(cause: Throwable? = null) : AuthError(cause)
    class EmailAlreadyInUse(cause: Throwable? = null) : AuthError(cause)
    class WeakPassword(cause: Throwable? = null) : AuthError(cause)
    class NoNetwork(cause: Throwable? = null) : AuthError(cause)
    class TooManyRequests(cause: Throwable? = null) : AuthError(cause)
    class Unknown(cause: Throwable? = null) : AuthError(cause)
}

fun Throwable.toAuthError(): AuthError = when (this) {
    is AuthError -> this
    is FirebaseNetworkException -> AuthError.NoNetwork(this)
    // WeakPassword extends InvalidCredentials, so it must be checked first
    is FirebaseAuthWeakPasswordException -> AuthError.WeakPassword(this)
    is FirebaseAuthInvalidCredentialsException,
    is FirebaseAuthInvalidUserException -> AuthError.InvalidCredentials(this)
    is FirebaseAuthUserCollisionException -> AuthError.EmailAlreadyInUse(this)
    is FirebaseTooManyRequestsException -> AuthError.TooManyRequests(this)
    else -> AuthError.Unknown(this)
}