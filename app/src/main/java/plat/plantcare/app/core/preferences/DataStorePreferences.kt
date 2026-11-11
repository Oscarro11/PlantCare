package plat.plantcare.app.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStorePreferences(private val context: Context) {
    private val uidKey = stringPreferencesKey("uid")
    private val emailKey = stringPreferencesKey("email")
    private val signedInKey = booleanPreferencesKey("signedIn")

    suspend fun setEmail(email: String) {
        context.dataStore.edit { it[emailKey] = email }
    }

    suspend fun setUid(uid: String) {
        context.dataStore.edit { it[uidKey] = uid }
    }

    suspend fun setSignedIn() {
        context.dataStore.edit { it[signedInKey] = true }
    }

    suspend fun getEmail(): String {
        val preferences = context.dataStore.data.first()
        return preferences[emailKey] ?: ""
    }

    suspend fun getUid(): String {
        val preferences = context.dataStore.data.first()
        return preferences[uidKey] ?: ""
    }

    suspend fun getSignedIn(): Boolean {
        val preferences = context.dataStore.data.first()
        return preferences[signedInKey] ?: false
    }

    suspend fun removeEmail() {
        context.dataStore.edit { it.remove(emailKey) }
    }

    suspend fun removeUid() {
        context.dataStore.edit { it.remove(uidKey) }
    }

    suspend fun removeSignedIn() {
        context.dataStore.edit { it.remove(signedInKey) }
    }
}
