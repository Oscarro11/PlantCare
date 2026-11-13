package plat.plantcare.app.core.data.diagnostic

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.core.photos.camera.PhotoStorage
import plat.plantcare.app.core.data.diagnostic.domain.Diagnostic
import plat.plantcare.app.core.data.diagnostic.remote.DiagnosticApi
import plat.plantcare.app.core.data.diagnostic.remote.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiagnosticRepositoryImpl @Inject constructor(
    private val api: DiagnosticApi,
    private val storage: PhotoStorage,
    private val authRepository: AuthRepository
) : DiagnosticRepository {

    override fun observeLatest(plantId: String): Flow<Diagnostic?> {
        TODO("Not yet implemented")
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeHistory(plantId: String): Flow<List<Diagnostic>> =
        authRepository.currentUser
            .map { it?.uid }
            .distinctUntilChanged()
            .flatMapLatest { uid ->
                if (uid == null) {
                    flowOf(emptyList())
                } else {
                    api.observeByPlant(uid, plantId)
                        .map { list -> list.map { it.toDomain() } }
                }
            }

    override fun observeDiagnostic(id: String): Flow<Diagnostic?> {
        TODO("Not yet implemented")
    }

    override suspend fun get(id: String): Diagnostic? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllForPlant(plantId: String) {
        TODO("Not yet implemented")
    }

}