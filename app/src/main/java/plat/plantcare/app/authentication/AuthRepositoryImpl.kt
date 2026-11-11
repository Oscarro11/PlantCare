package plat.plantcare.app.authentication

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import plat.plantcare.app.authentication.domain.AuthUser
import plat.plantcare.app.authentication.remote.AuthApi
import plat.plantcare.app.authentication.remote.AuthError
import plat.plantcare.app.authentication.remote.AuthResultDto
import plat.plantcare.app.authentication.remote.AuthUserDto
import plat.plantcare.app.authentication.remote.toAuthError
import plat.plantcare.app.authentication.remote.toDomain
import plat.plantcare.app.core.data.user.domain.User
import plat.plantcare.app.core.data.user.local.UserDAO
import plat.plantcare.app.core.data.user.local.toDomain
import plat.plantcare.app.core.data.user.remote.UserApi
import plat.plantcare.app.core.data.user.remote.UserDTO
import plat.plantcare.app.core.data.user.remote.toDomain
import plat.plantcare.app.core.data.user.remote.toEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi,
    private val userDao: UserDAO
) : AuthRepository{
    override val currentUser: Flow<AuthUser?> = authApi.authState.map { it?.toDomain() }
    override val currentUid: String? get() = authApi.currentUid

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<Unit> =
        authenticate { authApi.signInWithEmail(email, password) }

    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): Result<Unit> =
        authenticate { authApi.registerWithEmail(email, password) }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> =
        authenticate { authApi.signInWithGoogle(idToken) }

    override suspend fun signOut() {
        authApi.signOut()
        userDao.clear()
    }

    private suspend fun authenticate(call: suspend() -> AuthResultDto): Result<Unit> =

        try {
            // Needs the network: credentials can't be verified offline, so a failure
            // here surfaces as NoNetwork. There is no local fallback for this step.
            val result = call()

            try {
                loadProfile(result.user)          // server first, then the local cache
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                authApi.signOut()                     // don't stay signed in without a usable profile
                throw e
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toAuthError())
        }

    private suspend fun loadProfile(user: AuthUserDto): User {
        val remote = fetchRemoteProfile(user.uid)

        if (remote.isFailure) {
            return userDao.getUser(user.uid).first()?.toDomain()
                ?: throw AuthError.ProfileUnavailable(remote.exceptionOrNull())
        }

        // The server answered. A null profile means it really doesn't exist, so create it
        // instead of falling back to local data, which could be stale.
        val profile = remote.getOrNull() ?: userApi.createProfile(
            uid = user.uid,
            name = user.displayName.orEmpty(),
            imageUrl = user.photoUrl.orEmpty()
        )
        userDao.updateOrInsertUser(profile.toEntity())
        return profile.toDomain()
    }

    private suspend fun fetchRemoteProfile(uid: String): Result<UserDTO?> =
        try {
            Result.success(withTimeout(REMOTE_TIMEOUT_MS.milliseconds) { userApi.getProfile(uid) })
        } catch (e: TimeoutCancellationException) {
            Result.failure(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    private companion object {
        const val REMOTE_TIMEOUT_MS = 5_000L
    }
}