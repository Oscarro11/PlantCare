package plat.plantcare.app.core.network

import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig

object AiModelFactory {
    private const val MODEL_NAME = "gemini-3.6-flash"

    fun create(schema: Schema, instructions: String): GenerativeModel {
        return Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = schema
                temperature = 0.2f
            },
            systemInstruction = content {
                text( instructions )
            }
        )
    }
}