package plat.plantcare.app.core.data.diagnostic.remote

import kotlinx.coroutines.flow.Flow

interface DiagnosticApi {
    fun newId(uid: String, plantId: String): String
    fun observeByPlant(uid: String, plantId: String): Flow<List<DiagnosticDTO>>
    suspend fun getByPlant(uid: String, plantId: String): List<DiagnosticDTO>
    suspend fun get(uid: String, plantId: String, id: String): DiagnosticDTO?
    suspend fun create(uid: String, plantId: String, diagnostic: DiagnosticDTO)
    suspend fun delete(uid: String, plantId: String, id: String)
    suspend fun deleteAll(uid: String, plantId: String, ids: List<String>)
}