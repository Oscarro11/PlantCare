package plat.plantcare.app.core.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()

            if (result.additionalUserInfo?.isNewUser == true) {
                //TODO: create usuarios/{uid} profile here (name, image from result.user)
                Log.i("Authentication", "The authentication would create a new user")
            }
            return Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("Authentication", "Google sign-in failed", e)
            return Result.failure(e)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            return Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("Authentication", "Manual sign-in failed", e)
            return Result.failure(e)
        }
    }

    override suspend fun registerWithEmail(email: String, password: String): Result<Unit> {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            if (result.additionalUserInfo?.isNewUser == true) {
                //TODO: create usuarios/{uid} profile here (name, image from result.user)
                Log.i("Authentication", "The authentication would create a new user")
            }
            return Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("Authentication", "Creation of account failed", e)
            return Result.failure(e)
        }
    }
}