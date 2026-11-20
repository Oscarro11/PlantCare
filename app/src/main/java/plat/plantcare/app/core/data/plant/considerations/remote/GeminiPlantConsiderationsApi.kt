package plat.plantcare.app.core.data.plant.considerations.remote

import plat.plantcare.app.core.ai.gemini.GeminiClient
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.content
import kotlinx.serialization.json.Json
import plat.plantcare.app.core.data.plant.considerations.PlantConsiderationsModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiPlantConsiderationsApi @Inject constructor(
    @PlantConsiderationsModel private val model: GenerativeModel,
    private val client: GeminiClient
) : PlantConsiderationsAiApi {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun generateConsiderations(
        speciesName: String?,
        photo: ByteArray
    ): PlantConsiderationsAiDTO {
        client.requireAiAccess()
        val prompt = content {
            inlineData(photo, "image/jpeg")
            text("Identifica las características de cuidado que necesita la planta de la foto")
            speciesName?.takeIf { it.isNotBlank() }?.let { text("Especie de la planta: $it") }
        }

        val response = model.generateContent(prompt)
        val text = response.text ?: error("Gemini no devolvió texto")
        return json.decodeFromString<PlantConsiderationsAiDTO>(text)
    }
}