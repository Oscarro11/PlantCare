package plat.plantcare.app.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class PreferredCollectionLayout { GRID, LIST }

data class AppSettings(
    val weatherAlerts: Boolean = true,
    val collectionLayout: PreferredCollectionLayout = PreferredCollectionLayout.GRID
)

class DataStorePreferences(private val store: DataStore<Preferences>) {
    constructor(context: Context) : this(context.dataStore)

    private val uidKey = stringPreferencesKey("uid")

    suspend fun setUid(uid: String) { store.edit { it[uidKey] = uid } }
    fun getUid(): Flow<String?> = store.data.map { it[uidKey] }
    suspend fun removeUid() { store.edit { it.remove(uidKey) } }

    fun observeAppSettings(uid: String): Flow<AppSettings> {
        require(uid.isNotBlank())
        return store.data.map { preferences ->
            AppSettings(
                weatherAlerts = preferences[weatherAlertsKey(uid)] ?: true,
                collectionLayout = layout(preferences[collectionLayoutKey(uid)])
            )
        }
    }

    suspend fun setWeatherAlerts(uid: String, enabled: Boolean) {
        require(uid.isNotBlank())
        store.edit { it[weatherAlertsKey(uid)] = enabled }
    }

    suspend fun setCollectionLayout(uid: String, layout: PreferredCollectionLayout) {
        require(uid.isNotBlank())
        store.edit { it[collectionLayoutKey(uid)] = layout.name }
    }

    suspend fun toggleCollectionLayout(uid: String) {
        require(uid.isNotBlank())
        store.edit { preferences ->
            val key = collectionLayoutKey(uid)
            preferences[key] = when (layout(preferences[key])) {
                PreferredCollectionLayout.GRID -> PreferredCollectionLayout.LIST.name
                PreferredCollectionLayout.LIST -> PreferredCollectionLayout.GRID.name
            }
        }
    }

    private fun weatherAlertsKey(uid: String) = booleanPreferencesKey("weather_alerts_$uid")
    private fun collectionLayoutKey(uid: String) = stringPreferencesKey("collection_layout_$uid")
    private fun layout(value: String?) = PreferredCollectionLayout.entries.firstOrNull { it.name == value }
        ?: PreferredCollectionLayout.GRID
}
