package plat.plantcare.app.notifications

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

fun reminderDelay(settings: ReminderSettings, now: ZonedDateTime = ZonedDateTime.now()): Duration {
    var next = now.toLocalDate().atTime(settings.hour, settings.minute).atZone(now.zone)
    if (!next.isAfter(now)) next = now.toLocalDate().plusDays(1).atTime(settings.hour, settings.minute).atZone(now.zone)
    return Duration.between(now, next)
}
@Singleton
class TaskWorkScheduler @Inject constructor(@ApplicationContext private val context: Context) {
    private val work get() = WorkManager.getInstance(context)
    fun sync(uid: String, append: Boolean = true) {
        work.enqueueUniqueWork("task-sync-" + uid,
            if (append) ExistingWorkPolicy.APPEND_OR_REPLACE else ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<TaskSyncWorker>().setInputData(workDataOf("uid" to uid))
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS).build())
    }
    fun reminder(uid: String, settings: ReminderSettings, replace: Boolean = false, append: Boolean = false) {
        if (!settings.enabled) { cancelReminder(uid); return }
        val policy = if (append) ExistingWorkPolicy.APPEND_OR_REPLACE else if (replace) ExistingWorkPolicy.REPLACE else ExistingWorkPolicy.KEEP
        work.enqueueUniqueWork("watering-reminder-" + uid, policy,
            OneTimeWorkRequestBuilder<WateringReminderWorker>().setInputData(workDataOf("uid" to uid))
                .setInitialDelay(reminderDelay(settings).toMillis(), TimeUnit.MILLISECONDS).build())
    }
    fun cancelReminder(uid: String) { work.cancelUniqueWork("watering-reminder-" + uid); WateringNotifications(context).clear() }
}
