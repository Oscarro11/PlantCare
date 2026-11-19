package plat.plantcare.app.notifications

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.core.data.tasks.SyncedTaskRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRuntime @Inject constructor(
    private val repository: SyncedTaskRepository, private val auth: AuthRepository,
    private val preferences: ReminderPreferences, private val scheduler: TaskWorkScheduler,
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    fun start() {
        repository.start()
        WateringNotifications(context).createChannel()
        scope.launch {
            var previousUid: String? = null
            auth.sessionUid.collectLatest { uid ->
                if (previousUid != uid) {
                    previousUid?.let(scheduler::cancelReminder)
                    WateringNotifications(context).clear()
                }
                previousUid = uid
                if (uid != null) {
                    var first = true
                    preferences.observe(uid).map { Triple(it.enabled, it.hour, it.minute) }.distinctUntilChanged().collect { (enabled, hour, minute) ->
                        scheduler.reminder(uid, ReminderSettings(enabled, hour, minute), replace = !first)
                        first = false
                    }
                }
            }
        }
    }
}
