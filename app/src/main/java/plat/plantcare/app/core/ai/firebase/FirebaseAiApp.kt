package plat.plantcare.app.core.ai.firebase

import android.content.Context
import com.google.firebase.FirebaseApp

/** Uses the explicitly selected AI project, with auth isolated from the app data services. */
internal object FirebaseAiApp {
    private const val APP_NAME = "plantcare-ai"
    private var configured = false

    @Synchronized
    fun get(context: Context): FirebaseApp {
        val selectedOptions = AiProjectConfiguration.options(context)
        val app = FirebaseApp.getApps(context).firstOrNull { it.name == APP_NAME }
            ?: FirebaseApp.initializeApp(context, selectedOptions, APP_NAME)
        check(app.options.projectId == selectedOptions.projectId && app.options.applicationId == selectedOptions.applicationId) {
            "La instancia de IA no corresponde al proyecto seleccionado. Reinicia la app."
        }
        if (!configured) {
            configureAiAppCheck(app)
            configured = true
        }
        return app
    }
}
