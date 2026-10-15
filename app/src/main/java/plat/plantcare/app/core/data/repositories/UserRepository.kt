package plat.plantcare.app.core.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    fun observeUserName(): Flow<String>
}

@Singleton
class FakeUserRepository @Inject constructor() : UserRepository {
    override fun observeUserName(): Flow<String> = flowOf("Oscar :)")
}