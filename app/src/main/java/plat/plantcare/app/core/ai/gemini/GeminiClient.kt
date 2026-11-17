package plat.plantcare.app.core.ai.gemini

import com.google.firebase.appcheck.FirebaseAppCheck
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import android.content.Context
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.GenerationConfig
import com.google.firebase.ai.type.content
import dagger.hilt.android.qualifiers.ApplicationContext
import plat.plantcare.app.core.ai.firebase.FirebaseAiApp
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GeminiClient @Inject constructor(@ApplicationContext private val context: Context) {
    fun model(name: String, instruction: String, config: GenerationConfig): GenerativeModel =
        FirebaseAI.getInstance(FirebaseAiApp.get(context), GenerativeBackend.googleAI()).generativeModel(
            modelName = name,
            systemInstruction = content { text(instruction) },
            generationConfig = config
        )

    suspend fun requireAiAccess() {
        try {
            FirebaseAppCheck.getInstance(FirebaseAiApp.get(context)).getAppCheckToken(false).await()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            throw AiAuthorizationException(error)
        }
    }

    suspend fun generateImageText(model: GenerativeModel, image: ByteArray, mimeType: String, prompt: String): String {
        requireAiAccess()
        require(mimeType.startsWith("image/")) { "El archivo debe ser una imagen." }
        val response = model.generateContent(content {
            inlineData(image, mimeType)
            text(prompt)
        })
        return requireNotNull(response.text) { "No se recibió una respuesta del modelo. Vuelve a intentarlo." }
    }
}

class AiAuthorizationException(cause: Throwable) : Exception("El servicio de IA no está autorizado para esta instalación.", cause)
