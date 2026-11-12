package plat.plantcare.app.core.data.user.remote

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UserApi {
    fun observeProfile(uid: String): Flow<UserDTO?>
    suspend fun getProfile(uid: String): UserDTO?
    suspend fun createProfile(
        uid: String,
        name: String,
        imageUrl: String
    ): UserDTO
    suspend fun updateProfile(
        uid: String,
        name: String,
        location: String,
        imageUrl: String
    )
    suspend fun recordStreakDays(uid: String, today: LocalDate = LocalDate.now())
    suspend fun deleteProfile(uid: String)
}