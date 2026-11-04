package io.github.dustin2.dockclock.feature.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

data class UserSettings(
    val hideNotificationContent: Boolean = false,
    /** Hidden rather than allowed apps, so apps installed later show up without extra setup. */
    val hiddenApps: Set<String> = emptySet(),
    val nightMode: Boolean = true
)

interface SettingsRepository {
    val settings: Flow<UserSettings>

    suspend fun setHideNotificationContent(hide: Boolean)

    suspend fun setAppHidden(packageName: String, hidden: Boolean)

    suspend fun setNightMode(enabled: Boolean)
}

class DataStoreSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    override val settings: Flow<UserSettings> =
        dataStore.data
            // A corrupt or unreadable file falls back to defaults instead of crashing the clock.
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map {
                UserSettings(
                    hideNotificationContent = it[HIDE_NOTIFICATION_CONTENT] ?: false,
                    hiddenApps = it[HIDDEN_APPS].orEmpty(),
                    nightMode = it[NIGHT_MODE] ?: true
                )
            }

    override suspend fun setHideNotificationContent(hide: Boolean) {
        dataStore.edit { it[HIDE_NOTIFICATION_CONTENT] = hide }
    }

    override suspend fun setAppHidden(packageName: String, hidden: Boolean) {
        dataStore.edit {
            val current = it[HIDDEN_APPS].orEmpty()
            it[HIDDEN_APPS] = if (hidden) current + packageName else current - packageName
        }
    }

    override suspend fun setNightMode(enabled: Boolean) {
        dataStore.edit { it[NIGHT_MODE] = enabled }
    }

    private companion object {
        val HIDE_NOTIFICATION_CONTENT = booleanPreferencesKey("hide_notification_content")
        val HIDDEN_APPS = stringSetPreferencesKey("hidden_apps")
        val NIGHT_MODE = booleanPreferencesKey("night_mode")
    }
}
