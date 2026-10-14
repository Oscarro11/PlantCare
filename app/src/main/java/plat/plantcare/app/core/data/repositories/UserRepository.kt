package plat.plantcare.app.core.data.repositories

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUserName(): Flow<String>
}