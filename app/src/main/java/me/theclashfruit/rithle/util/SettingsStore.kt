package me.theclashfruit.rithle.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

data class Settings(
    val theme: Int = 0,
    val lang: String = "default",
    val extractModpacks: Boolean = false,
    val modpackLocation: String? = null,
    val updateChecker: Boolean = true
)

class SettingsStore(private val context: Context) {

    object PreferencesKeys {
        val theme = intPreferencesKey("theme")
        val lang = stringPreferencesKey("lang")

        val extractModpacks = booleanPreferencesKey("extract_modpacks")
        val modpackLocation = stringPreferencesKey("modpack_location")

        val updateChecker = booleanPreferencesKey("update_checker")
    }

    val key = PreferencesKeys

    val settingsFlow: Flow<Settings> = context.settingsStore.data.map { preferences ->
        Settings(
            theme = preferences[key.theme] ?: 0,
            lang = preferences[key.lang] ?: "default",
            extractModpacks = preferences[key.extractModpacks] ?: false,
            modpackLocation = preferences[key.modpackLocation],
            updateChecker = preferences[key.updateChecker] ?: true,
        )
    }

    fun get(): Settings = runBlocking {
        settingsFlow.first()
    }

    suspend fun update(action: SettingsBuilder.() -> Unit) {
        context.settingsStore.edit { preferences ->
            val current = Settings(
                theme = preferences[key.theme] ?: 0,
                lang = preferences[key.lang] ?: "default",
                extractModpacks = preferences[key.extractModpacks] ?: false,
                modpackLocation = preferences[key.modpackLocation],
                updateChecker = preferences[key.updateChecker] ?: true
            )

            val builder = SettingsBuilder(current).apply(action)

            preferences[key.theme] = builder.theme
            preferences[key.lang] = builder.lang
            preferences[key.extractModpacks] = builder.extractModpacks
            preferences[key.updateChecker] = builder.updateChecker

            val location = builder.modpackLocation
            if (location != null) {
                preferences[key.modpackLocation] = location
            } else {
                preferences.remove(key.modpackLocation)
            }
        }
    }

    class SettingsBuilder(initial: Settings) {
        var theme: Int = initial.theme
        var lang: String = initial.lang
        var extractModpacks: Boolean = initial.extractModpacks
        var modpackLocation: String? = initial.modpackLocation
        var updateChecker: Boolean = initial.updateChecker
    }
}