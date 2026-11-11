package plat.plantcare.app.core.data.user.remote

import kotlinx.coroutines.flow.Flow

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
    suspend fun setStreakDays(uid: String, days: Int)
    suspend fun deleteProfile(uid: String)
}