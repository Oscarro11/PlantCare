package plat.plantcare.app.diagnosis

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import plat.plantcare.app.core.data.plant.domain.Plant
import plat.plantcare.app.diagnosis.application.AnalyzePlant
import plat.plantcare.app.diagnosis.application.SaveDiagnosis
import plat.plantcare.app.diagnosis.domain.*

/** Business behavior runs without Android, Firebase or the Gemini SDK. */
class DiagnosisUseCasesTest {
    private val plant = Plant("plant-1", "Monstera", null, "Sala", null, "owner", null)
    private fun answer() = DiagnosisAnswer(true, true, "NO_EVALUABLE", "Evaluación visual limitada",
        "Hojas visibles", "No evaluable con una foto", "Sin evidencia suficiente", "No concluyente", null,
        emptyList(), listOf("Comprobar sustrato"), listOf("Raíces no visibles"))

    @Test fun analyzeUsesSelectedContextAndPhotoAndDoesNotSaveAutomatically() = runBlocking {
        val repository = MemoryDiagnoses(DiagnosisContext("owner", plant, null, null))
        val photos = MemoryPhotos()
        val analyzer = RecordingAnalyzer(answer())
        val draft = AnalyzePlant(repository, analyzer, photos)(plant, "local-photo")
        assertEquals("local-photo", photos.requested)
        assertArrayEquals(byteArrayOf(1, 2, 3), analyzer.receivedBytes)
        assertEquals(repository.context, analyzer.receivedContext)
        assertEquals(plant.id, draft.diagnostic.plantId)
        assertEquals("test-provider-model", draft.diagnostic.model)
        assertNull(draft.diagnostic.score)
        assertTrue(repository.saved.isEmpty())
    }

    @Test fun mismatchedOwnerStopsBeforePhotoOrAiAccess() = runBlocking {
        val repository = MemoryDiagnoses(DiagnosisContext("another-owner", plant, null, null))
        val photos = MemoryPhotos()
        val analyzer = RecordingAnalyzer(answer())
        try { AnalyzePlant(repository, analyzer, photos)(plant, "photo"); fail("Expected owner validation") }
        catch (_: IllegalArgumentException) { }
        assertNull(photos.requested)
        assertEquals(0, analyzer.calls)
    }

    @Test fun contextReadFailureDoesNotSendThePhotoToAi() = runBlocking {
        val repository = MemoryDiagnoses(DiagnosisContext("owner", plant, null, null)).apply { failLoading = true }
        val photos = MemoryPhotos()
        val analyzer = RecordingAnalyzer(answer())
        try { AnalyzePlant(repository, analyzer, photos)(plant, "photo"); fail("Expected context read failure") }
        catch (_: IllegalStateException) { }
        assertNull(photos.requested)
        assertEquals(0, analyzer.calls)
    }

    @Test fun unusableImageDoesNotBecomeASaveableDraft() = runBlocking {
        val repository = MemoryDiagnoses(DiagnosisContext("owner", plant, null, null))
        val analyzer = RecordingAnalyzer(answer().copy(imageUsable = false))
        try { AnalyzePlant(repository, analyzer, MemoryPhotos())(plant, "photo"); fail("Expected image validation") }
        catch (_: IllegalArgumentException) { }
        assertTrue(repository.saved.isEmpty())
    }

    @Test fun savePreservesDraftIdentityAndRejectsAnotherPlant() = runBlocking {
        val context = DiagnosisContext("owner", plant, null, null)
        val repository = MemoryDiagnoses(context)
        val diagnostic = answer().toDiagnostic(plant.id, "test-model")
        val draft = DiagnosisDraft(context, diagnostic)
        val save = SaveDiagnosis(repository)
        save(draft)
        save(draft)
        assertEquals(listOf(diagnostic.id, diagnostic.id), repository.saved.map { it.id })
        try { save(draft.copy(diagnostic = diagnostic.copy(plantId = "different-plant"))); fail("Expected plant validation") }
        catch (_: IllegalArgumentException) { }
        assertEquals(2, repository.saved.size)
    }

    private class MemoryDiagnoses(val context: DiagnosisContext) : DiagnosisRepository {
        var failLoading = false
        val saved = mutableListOf<Diagnostic>()
        override fun observeDetails(plantId: String): Flow<DiagnosisDetails> = flowOf(DiagnosisDetails())
        override suspend fun loadContext(plant: Plant): DiagnosisContext {
            check(!failLoading) { "Unavailable" }
            return context
        }
        override suspend fun save(context: DiagnosisContext, diagnostic: Diagnostic) { saved += diagnostic }
    }
    private class MemoryPhotos : DiagnosisPhotos {
        var requested: String? = null
        override suspend fun importPhoto(source: String) = source
        override suspend fun read(photo: String): ByteArray { requested = photo; return byteArrayOf(1, 2, 3) }
        override suspend fun delete(photo: String) = Unit
    }
    private class RecordingAnalyzer(private val answer: DiagnosisAnswer) : PlantAnalyzer {
        var calls = 0
        var receivedBytes: ByteArray? = null
        var receivedContext: DiagnosisContext? = null
        override suspend fun analyze(jpeg: ByteArray, context: DiagnosisContext): AnalyzedPlant {
            calls++
            receivedBytes = jpeg
            receivedContext = context
            return AnalyzedPlant(answer, "test-provider-model")
        }
    }
}
