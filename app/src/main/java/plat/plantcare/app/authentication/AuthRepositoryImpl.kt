package plat.plantcare.app.authentication

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import plat.plantcare.app.authentication.domain.AuthUser
import plat.plantcare.app.authentication.remote.AuthApi
import plat.plantcare.app.authentication.remote.AuthResultDto
import plat.plantcare.app.authentication.remote.toAuthError
import plat.plantcare.app.authentication.remote.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    //TODO: update
    //private val userRemote: UserRemoteDataSource,
    //private val userDao: UserDao
) : AuthRepository{
    override val currentUser: Flow<AuthUser?> = api.authState.map { it?.toDomain() }
    override val currentUid: String? get() = api.currentUid

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<Unit> =
        authenticate { api.signInWithEmail(email, password) }

    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): Result<Unit> =
        authenticate { api.registerWithEmail(email, password) }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> =
        authenticate { api.signInWithGoogle(idToken) }

    override suspend fun signOut() {
        api.signOut()
        //TODO: add
        //userDao.clear()
    }

    private suspend fun authenticate(call: suspend() -> AuthResultDto): Result<Unit> =
        try {
            val result = call()
            //TODO: update
            //runCatching { ensureProfile(result.user) }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toAuthError())
        }

    /*
    private suspend fun ensureProfile(user: AuthUserDto) {
        val profile = userRemote.getProfile(user.uid)
            ?: userRemote.createProfile(
                uid = user.uid,
                name = user.displayName.orEmpty(),
                imageUrl = user.photoUrl.orEmpty()
            )
        userDao.upsert(profile.toEntity())
    }*/
}