package plat.plantcare.app.notifications

import android.content.Context
import androidx.datastore.preferences.core.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import plat.plantcare.app.core.preferences.dataStore
import javax.inject.Inject
import javax.inject.Singleton

data class ReminderSettings(val enabled: Boolean = false, val hour: Int = 9, val minute: Int = 0, val lastNotified: String = "") {
    val displayTime: String get() = "%02d:%02d".format(hour, minute)
}
@Singleton
class ReminderPreferences @Inject constructor(@ApplicationContext private val context: Context) {
    fun observe(uid: String): Flow<ReminderSettings> = context.dataStore.data.map { p ->
        ReminderSettings(p[booleanPreferencesKey("watering_enabled_" + uid)] ?: false,
            p[intPreferencesKey("watering_hour_" + uid)] ?: 9,
            p[intPreferencesKey("watering_minute_" + uid)] ?: 0,
            p[stringPreferencesKey("watering_notified_" + uid)] ?: "")
    }
    suspend fun enabled(uid: String, enabled: Boolean) { context.dataStore.edit { it[booleanPreferencesKey("watering_enabled_" + uid)] = enabled } }
    suspend fun time(uid: String, hour: Int, minute: Int) {
        require(hour in 0..23 && minute in 0..59)
        context.dataStore.edit { it[intPreferencesKey("watering_hour_" + uid)] = hour; it[intPreferencesKey("watering_minute_" + uid)] = minute }
    }
    suspend fun notified(uid: String, day: String) { context.dataStore.edit { it[stringPreferencesKey("watering_notified_" + uid)] = day } }
}
