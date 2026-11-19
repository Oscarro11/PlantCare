package plat.plantcare.app.authentication

import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.authentication.domain.AuthUser

interface AuthRepository : SessionProvider {
    val currentUser: Flow<AuthUser?>
    val currentUid: String?
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun registerWithEmail(email: String, password: String): Result<Unit>
    suspend fun registerWithEmail(email: String, password: String, name: String, location: String): Result<Unit> =
        registerWithEmail(email, password)
    suspend fun signInWithGoogle(idToken: String): Result<Unit>
    suspend fun signOut()
}