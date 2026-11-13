package plat.plantcare.app.core.data.diagnostic

import kotlinx.coroutines.flow.Flow
import plat.plantcare.app.core.data.diagnostic.domain.Diagnostic

//TODO: add implementation, ask for draft and generate DAO
interface DiagnosticRepository {
    fun observeLatest(plantId: String): Flow<Diagnostic?>
    fun observeHistory(plantId: String): Flow<List<Diagnostic>>
    fun observeDiagnostic(id: String): Flow<Diagnostic?>
    suspend fun get(id: String): Diagnostic?
    suspend fun delete(id: String)
    suspend fun deleteAllForPlant(plantId: String)
}