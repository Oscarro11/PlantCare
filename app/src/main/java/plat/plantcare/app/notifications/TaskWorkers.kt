package plat.plantcare.app.notifications

import android.content.Context
import android.content.BroadcastReceiver
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import plat.plantcare.app.authentication.AuthRepository
import plat.plantcare.app.core.data.tasks.LocalTaskStore
import plat.plantcare.app.core.data.tasks.SyncedTaskRepository
import java.time.LocalDate

@EntryPoint @InstallIn(SingletonComponent::class)
interface TaskWorkerEntryPoint {
    fun taskRepository(): SyncedTaskRepository
    fun store(): LocalTaskStore
    fun auth(): AuthRepository
    fun preferences(): ReminderPreferences
    fun scheduler(): TaskWorkScheduler
}
private fun entry(context: Context) = EntryPointAccessors.fromApplication(context, TaskWorkerEntryPoint::class.java)

class TaskSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val uid = inputData.getString("uid") ?: return Result.failure()
        val dependencies = entry(applicationContext)
        if (dependencies.auth().currentUid != uid) return Result.success()
        return try { dependencies.taskRepository().syncPending(uid); Result.success() }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { Log.w("TaskSync", "La sincronización se reintentará", error); Result.retry() }
    }
}
class WateringReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val uid = inputData.getString("uid") ?: return Result.failure()
        val d = entry(applicationContext)
        if (d.auth().currentUid != uid || d.auth().sessionUid.first() != uid) return Result.success()
        val settings = d.preferences().observe(uid).first()
        if (!settings.enabled) return Result.success()
        val today = LocalDate.now().toString()
        val count = d.store().dao.pendingWaterings(uid, today)
        if (d.auth().currentUid == uid && d.auth().sessionUid.first() == uid) {
            if (count == 0) WateringNotifications(applicationContext).clear()
            else if (settings.lastNotified != today && WateringNotifications(applicationContext).show(count))
                d.preferences().notified(uid, today)
            val latest = d.preferences().observe(uid).first()
            if (latest.enabled) d.scheduler().reminder(uid, latest, append = true)
        }
        return Result.success()
    }
}
class ReminderTimeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val d = entry(context)
                d.auth().sessionUid.first()?.let { uid -> d.scheduler().reminder(uid, d.preferences().observe(uid).first(), replace = true) }
            } finally { pending.finish() }
        }
    }
}
