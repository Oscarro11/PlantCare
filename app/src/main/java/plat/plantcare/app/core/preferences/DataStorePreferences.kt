package plat.plantcare.app.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStorePreferences(private val context: Context) {
    private val uidKey = stringPreferencesKey("uid")

    suspend fun setUid(uid: String) {
        context.dataStore.edit { it[uidKey] = uid }
    }

    fun getUid(): Flow<String?> =
        context.dataStore.data.map { it[uidKey] }

    suspend fun removeUid() {
        context.dataStore.edit { it.remove(uidKey) }
    }
}
