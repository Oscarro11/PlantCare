package plat.plantcare.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import plat.plantcare.app.plantDetails.DetailsRoute
import plat.plantcare.app.ui.theme.PlantcareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantcareTheme {
                DetailsRoute(id = 1)
            }
        }
    }
}