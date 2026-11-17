package plat.plantcare.app.tasks

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkInfo
import androidx.work.WorkManager
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import plat.plantcare.app.notifications.*
import java.time.LocalTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ReminderInstrumentedTest {
    @Test fun summaryNotificationUsesOneIdUpdatesTheCountAndClearsWhenEmpty() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val permissionWasGranted = Build.VERSION.SDK_INT < 33 || context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        val notifications = WateringNotifications(context)
        try {
            if (!permissionWasGranted) instrumentation.uiAutomation.grantRuntimePermission(context.packageName, Manifest.permission.POST_NOTIFICATIONS)
            assertTrue(notifications.show(2))
            val manager = context.getSystemService(NotificationManager::class.java)
            fun awaitText(text: String) {
                repeat(60) {
                    if (manager.activeNotifications.any { it.id == WateringNotifications.ID && it.notification.extras.getCharSequence(Notification.EXTRA_TEXT).toString().contains(text) }) return
                    Thread.sleep(50)
                }
                fail("No llegó el resumen esperado: " + text)
            }
            awaitText("2 riegos")
            val first = manager.activeNotifications.first { it.id == WateringNotifications.ID }
            assertTrue(first.notification.extras.getCharSequence(Notification.EXTRA_TEXT).toString().contains("2 riegos"))
            assertNotNull(first.notification.contentIntent)
            assertTrue(notifications.show(1))
            awaitText("1 riego")
            val updated = manager.activeNotifications.filter { it.id == WateringNotifications.ID }
            assertEquals(1, updated.size)
            assertTrue(updated.first().notification.extras.getCharSequence(Notification.EXTRA_TEXT).toString().contains("1 riego"))
            assertFalse(notifications.show(0))
            repeat(20) { if (manager.activeNotifications.any { it.id == WateringNotifications.ID }) Thread.sleep(50) }
            assertTrue(manager.activeNotifications.none { it.id == WateringNotifications.ID })
        } finally {
            notifications.clear()
            // The host restores the initial permission after instrumentation finishes.
        }
    }
    @Test fun schedulingIsUniqueAndCanBeCancelled() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val uid = "reminder-test-" + UUID.randomUUID()
        val work = WorkManager.getInstance(context)
        val scheduler = TaskWorkScheduler(context)
        val time = LocalTime.now().plusHours(2)
        try {
            scheduler.reminder(uid, ReminderSettings(true, time.hour, time.minute), replace = true)
            scheduler.reminder(uid, ReminderSettings(true, time.hour, time.minute))
            val jobs = work.getWorkInfosForUniqueWork("watering-reminder-" + uid).get(10, TimeUnit.SECONDS)
            assertEquals(1, jobs.count { !it.state.isFinished })
            assertEquals(WorkInfo.State.ENQUEUED, jobs.first { !it.state.isFinished }.state)
        } finally { work.cancelUniqueWork("watering-reminder-" + uid).result.get(10, TimeUnit.SECONDS) }
    }
}
