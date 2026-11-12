package plat.plantcare.app.core.data.user

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import plat.plantcare.app.authentication.SessionProvider
import plat.plantcare.app.core.data.user.domain.User
import plat.plantcare.app.core.data.user.domain.toEntity
import plat.plantcare.app.core.data.user.local.UserDAO
import plat.plantcare.app.core.data.user.local.toDomain
import plat.plantcare.app.core.data.user.remote.UserApi
import plat.plantcare.app.core.data.user.remote.toEntity
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val session: SessionProvider,
    private val userApi: UserApi,
    private val userDao: UserDAO
) : UserRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCurrentUser(): Flow<User?> =
        session.sessionUid.flatMapLatest { uid ->
            if (uid.isNullOrBlank()) {
                flowOf(null)
            } else {
                userDao.getUser(uid).map { entity -> entity?.toDomain() }
            }
        }

    override suspend fun getCurrentUser(): User? {
        val id = session.sessionUid.first()

        return if (id.isNullOrBlank()) {
            null
        } else {
            userDao.getUser(id).first()?.toDomain()
        }
    }

    override suspend fun updateUser(user: User) {
        val uid = requireCurrentUid()
        require(user.id == uid) { "Cannot update a different user's profile" }

        userApi.updateProfile(
            uid = user.id,
            name = user.name,
            location = user.location,
            imageUrl = user.imageUrl
        )

        userDao.updateOrInsertUser(user.toEntity())
    }

    override suspend fun updateStreakDays() {
        val uid = requireCurrentUid()

        userApi.recordStreakDays(uid)
        userApi.getProfile(uid)?.let { profile ->
            userDao.updateOrInsertUser(profile.toEntity())
        }
    }

    private suspend fun requireCurrentUid(): String =
        session.sessionUid.first()
            ?.takeIf { it.isNotBlank() }
            ?:throw IllegalStateException("There is no current user")
}