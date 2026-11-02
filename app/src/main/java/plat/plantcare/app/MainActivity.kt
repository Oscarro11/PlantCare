package plat.plantcare.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import plat.plantcare.app.core.navigation.AppNavHost
import plat.plantcare.app.ui.theme.PlantcareTheme

@HiltAndroidApp
class PlantCareApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.USE_EMULATOR) {
            val host = BuildConfig.EMULATOR_HOST
            Firebase.auth.useEmulator(host, 9099)
            Firebase.firestore.useEmulator(host, 8080)
            Firebase.storage.useEmulator(host, 9199)
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantcareTheme {
                AppNavHost()
            }
        }
    }
}