package me.theclashfruit.rithle.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenRepository(private val context: Context) {
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val expiresAtKey = longPreferencesKey("expires_at")

    val accessToken: Flow<String?> = context.authDataStore.data.map { prefs ->
        val token = prefs[accessTokenKey]
        val expiresAt = prefs[expiresAtKey]

        if (token != null && expiresAt != null && expiresAt <= System.currentTimeMillis()) {
            null
        } else {
            token
        }
    }

    /**
     * @param expiresInSeconds Lifetime of the token in seconds, as typically
     * returned by an OAuth `expires_in` field. Pass null if the token doesn't expire.
     */
    suspend fun saveToken(token: String, expiresInSeconds: Long? = null) {
        context.authDataStore.edit { prefs ->
            prefs[accessTokenKey] = token

            if (expiresInSeconds != null) {
                prefs[expiresAtKey] = System.currentTimeMillis() + (expiresInSeconds * 1000)
            } else {
                prefs.remove(expiresAtKey)
            }
        }
    }

    suspend fun getAccessTokenOnce(): String? = accessToken.first()

    suspend fun clear() {
        context.authDataStore.edit { it.clear() }
    }
}