package plat.plantcare.app.core.ai.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.json.JSONObject
import plat.plantcare.app.BuildConfig

/** Explicit build-time selection. A missing/invalid configuration never selects another project. */
internal object AiProjectConfiguration {
    fun options(context: Context, project: String = BuildConfig.AI_PROJECT): FirebaseOptions = when (project) {
        "plantcare" -> FirebaseApp.getInstance().options
        "plantcare-ia" -> sparkOptions(context)
        else -> throw IllegalArgumentException("El proyecto de IA seleccionado no es válido.")
    }

    private fun sparkOptions(context: Context): FirebaseOptions {
        val json = context.assets.open("firebase_ai.json").bufferedReader().use { JSONObject(it.readText()) }
        require(json.getString("projectId") == "plantcare-ia") { "La configuración de PlantCare IA no es válida." }
        return FirebaseOptions.Builder()
            .setProjectId(json.getString("projectId"))
            .setApplicationId(json.getString("applicationId"))
            .setApiKey(json.getString("apiKey"))
            .setGcmSenderId(json.getString("senderId"))
            .build()
    }
}
