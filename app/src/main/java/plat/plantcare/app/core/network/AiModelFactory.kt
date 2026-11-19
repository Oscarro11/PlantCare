package plat.plantcare.app.core.network

import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import plat.plantcare.app.BuildConfig
import plat.plantcare.app.core.ai.gemini.GeminiClient
import javax.inject.Inject

class AiModelFactory @Inject constructor(private val client: GeminiClient) {
    fun create(schema: Schema, instructions: String): GenerativeModel = client.model(
        name = BuildConfig.GEMINI_MODEL,
        instruction = instructions,
        config = generationConfig {
            responseMimeType = "application/json"
            responseSchema = schema
            temperature = 0.2f
        }
    )
}
