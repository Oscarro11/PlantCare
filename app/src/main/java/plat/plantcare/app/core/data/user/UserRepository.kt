package plat.plantcare.app.core.data.user

import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.core.data.user.domain.User

interface UserRepository {
    fun observeCurrentUser(): Flow<User?>
    suspend fun getCurrentUser(): User?
    suspend fun updateUser(user: User)
    suspend fun updateStreakDays()
}