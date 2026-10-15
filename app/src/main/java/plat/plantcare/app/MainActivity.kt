package plat.plantcare.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import plat.plantcare.app.core.navigation.AppNavHost
import plat.plantcare.app.ui.theme.PlantcareTheme

@HiltAndroidApp
class PlantCareApp : Application()

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