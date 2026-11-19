package plat.plantcare.app.core.ai.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

internal fun configureAiAppCheck(app: FirebaseApp) {
    FirebaseAppCheck.getInstance(app).installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
}

// Debug token setup must never run in release builds.
@Suppress("UNUSED_PARAMETER")
internal fun prepareAiDebug(context: Context) = Unit
