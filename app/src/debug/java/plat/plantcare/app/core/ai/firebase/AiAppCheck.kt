package plat.plantcare.app.core.ai.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

internal fun configureAiAppCheck(app: FirebaseApp) {
    FirebaseAppCheck.getInstance(app).installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
}

/** Prepare the debug secret at startup; no photo or Gemini request is needed. */
internal fun prepareAiDebug(context: Context) {
    runCatching {
        val app = FirebaseAiApp.get(context)
        FirebaseAppCheck.getInstance(app).getAppCheckToken(true)
            .addOnSuccessListener { Log.i("FirebaseAiSetup", "App Check autorizado para esta instalación en "+app.options.projectId+".") }
            .addOnFailureListener { error ->
                val reason = error.message.orEmpty()
                    .replace(Regex("AIza[0-9A-Za-z_-]+"), "[clave omitida]")
                    .replace(Regex("[0-9a-fA-F]{8}(-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}"), "[token omitido]")
                Log.w("FirebaseAiSetup", "No se pudo renovar App Check en ${app.options.projectId}: $reason")
            }
    }.onFailure { Log.w("FirebaseAiSetup", "No se pudo preparar App Check para el proyecto de IA seleccionado.", it) }
}
