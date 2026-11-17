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
        FirebaseAppCheck.getInstance(app).getAppCheckToken(false)
            .addOnSuccessListener { Log.i("FirebaseAiSetup", "App Check autorizado para esta instalación en "+app.options.projectId+".") }
            .addOnFailureListener {
                Log.i("FirebaseAiSetup", "Revisa DebugAppCheckProvider en Logcat y registra su token en App Check del proyecto "+app.options.projectId+".")
            }
    }.onFailure { Log.w("FirebaseAiSetup", "No se pudo preparar App Check para el proyecto de IA seleccionado.", it) }
}
