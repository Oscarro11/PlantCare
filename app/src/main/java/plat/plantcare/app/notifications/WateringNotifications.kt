package plat.plantcare.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import plat.plantcare.app.MainActivity
import plat.plantcare.app.R

class WateringNotifications(private val context: Context) {
    fun createChannel() {
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(CHANNEL, "Riegos pendientes", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Resumen diario de los riegos de hoy y los atrasados"
            })
    }
    fun show(count: Int): Boolean {
        createChannel()
        if (count <= 0) { clear(); return false }
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return false
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return false
        if (context.getSystemService(NotificationManager::class.java).getNotificationChannel(CHANNEL)?.importance == NotificationManager.IMPORTANCE_NONE) return false
        val intent = Intent(context, MainActivity::class.java).putExtra(OPEN_TASKS, true)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pending = PendingIntent.getActivity(context, 2001, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val message = if (count == 1) "Tienes 1 riego pendiente para tus plantas." else "Tienes " + count + " riegos pendientes para tus plantas."
        try {
            manager.notify(ID, NotificationCompat.Builder(context, CHANNEL).setSmallIcon(R.drawable.water_drop)
                .setContentTitle("Es hora de cuidar tus plantas").setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message)).setContentIntent(pending)
                .setAutoCancel(true).setOnlyAlertOnce(true).build())
        } catch (_: SecurityException) { return false }
        return true
    }
    fun clear() { NotificationManagerCompat.from(context).cancel(ID) }
    companion object { const val CHANNEL = "watering_daily"; const val ID = 2001; const val OPEN_TASKS = "open_tasks" }
}
