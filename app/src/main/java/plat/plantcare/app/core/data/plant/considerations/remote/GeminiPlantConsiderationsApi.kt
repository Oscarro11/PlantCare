package plat.plantcare.app.core.data.plant.considerations.remote

import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.content
import kotlinx.serialization.json.Json
import plat.plantcare.app.core.data.plant.considerations.PlantConsiderationsModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiPlantConsiderationsApi @Inject constructor(
    @PlantConsiderationsModel private val model: GenerativeModel
) : PlantConsiderationsAiApi {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun generateConsiderations(
        speciesName: String?,
        photo: ByteArray
    ): PlantConsiderationsAiDTO {
        val prompt = content {
            inlineData(photo, "image/jpeg")
            text("Identifica las características de cuidado que necesita la planta de la foto")
            takeIf { speciesName != null }.let { text("Especie de la planta: $speciesName") }
        }

        val response = model.generateContent(prompt)
        val text = response.text ?: error("Gemini no devolvió texto")
        return json.decodeFromString<PlantConsiderationsAiDTO>(text)
    }
}