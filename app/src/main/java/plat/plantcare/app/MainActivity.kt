package plat.plantcare.app

import android.app.Application
import android.os.Bundle
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import javax.inject.Inject
import plat.plantcare.app.notifications.TaskRuntime
import plat.plantcare.app.notifications.WateringNotifications
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
    @Inject lateinit var taskRuntime: TaskRuntime
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.USE_EMULATOR) {
            val host = BuildConfig.EMULATOR_HOST
            Firebase.auth.useEmulator(host, 9099)
            Firebase.firestore.useEmulator(host, 8080)
            Firebase.storage.useEmulator(host, 9199)
        }
        taskRuntime.start()
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var openTasks by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openTasks = intent.getBooleanExtra(WateringNotifications.OPEN_TASKS, false)
        enableEdgeToEdge()
        setContent {
            PlantcareTheme {
                AppNavHost(openTasks = openTasks, onTasksOpened = { openTasks = false })
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openTasks = intent.getBooleanExtra(WateringNotifications.OPEN_TASKS, false)
    }
}
