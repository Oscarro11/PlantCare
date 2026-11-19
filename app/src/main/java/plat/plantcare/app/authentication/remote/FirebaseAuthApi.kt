package plat.plantcare.app.authentication.remote

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await

class FirebaseAuthApi (
    private val auth: FirebaseAuth
) : AuthApi {
    override val authState: Flow<AuthUserDto?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.toDTO()) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    override val currentUid: String?
        get() = auth.currentUser?.uid

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): AuthResultDto = auth.signInWithEmailAndPassword(email, password).await().toDTO()

    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): AuthResultDto = auth.createUserWithEmailAndPassword(email, password).await().toDTO()

    override suspend fun signInWithGoogle(idToken: String): AuthResultDto {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(credential).await().toDTO()
    }

    override suspend fun getIdToken(forceRefresh: Boolean): String? =
        auth.currentUser?.getIdToken(forceRefresh)?.await()?.token

    override fun signOut() = auth.signOut()
}

private fun FirebaseUser.toDTO() = AuthUserDto(
    uid = uid,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl?.toString(),
    isEmailVerified = isEmailVerified,
    providerIds = providerData.map { it.providerId }.filter{ it != "firebase" }
)

private fun AuthResult.toDTO() = AuthResultDto(
    user = requireNotNull(user) { "Firebase returned no user" }.toDTO(),
    isNewUser = additionalUserInfo?.isNewUser == true
)
