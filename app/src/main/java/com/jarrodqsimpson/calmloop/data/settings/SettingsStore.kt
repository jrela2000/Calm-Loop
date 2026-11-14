package com.jarrodqsimpson.calmloop.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "calmloop_settings")

data class UserSettings(
    val userName: String = DEFAULT_USER_NAME,
    val acceptedDisclaimerVersion: Int = 0,
    val aiEnabled: Boolean = false
) {
    val hasAcceptedCurrentDisclaimer: Boolean
        get() = acceptedDisclaimerVersion >= CURRENT_DISCLAIMER_VERSION

    companion object {
        const val DEFAULT_USER_NAME = "Calm Friend"

        // Bump when the disclaimer or privacy terms change materially so users see them again.
        const val CURRENT_DISCLAIMER_VERSION = 1
    }
}

/** On-device preferences. Nothing here leaves the device. */
class SettingsStore(private val context: Context) {
    private object Keys {
        val userName = stringPreferencesKey("user_name")
        val acceptedDisclaimerVersion = intPreferencesKey("accepted_disclaimer_version")
        val aiEnabled = booleanPreferencesKey("ai_enabled")
    }

    val settings: Flow<UserSettings> = context.settingsDataStore.data.map { prefs ->
        UserSettings(
            userName = prefs[Keys.userName] ?: UserSettings.DEFAULT_USER_NAME,
            acceptedDisclaimerVersion = prefs[Keys.acceptedDisclaimerVersion] ?: 0,
            aiEnabled = prefs[Keys.aiEnabled] ?: false
        )
    }

    suspend fun setUserName(name: String) {
        context.settingsDataStore.edit { it[Keys.userName] = name }
    }

    suspend fun acceptDisclaimer(aiEnabled: Boolean) {
        context.settingsDataStore.edit {
            it[Keys.acceptedDisclaimerVersion] = UserSettings.CURRENT_DISCLAIMER_VERSION
            it[Keys.aiEnabled] = aiEnabled
        }
    }

    suspend fun setAiEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.aiEnabled] = enabled }
    }

    suspend fun clear() {
        context.settingsDataStore.edit { it.clear() }
    }
}
