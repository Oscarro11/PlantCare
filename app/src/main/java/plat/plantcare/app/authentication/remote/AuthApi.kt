package plat.plantcare.app.authentication.remote

import kotlinx.coroutines.flow.Flow

interface AuthApi {
    val authState: Flow<AuthUserDto?>
    val currentUid: String?

    suspend fun signInWithEmail(email: String, password: String): AuthResultDto
    suspend fun registerWithEmail(email: String, password: String): AuthResultDto
    suspend fun signInWithGoogle(idToken: String): AuthResultDto

    suspend fun getIdToken(forceRefresh: Boolean = false): String?

    fun signOut()
}