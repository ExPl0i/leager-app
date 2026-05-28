package com.ledger.app.security

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

private val Context.securityDataStore by preferencesDataStore(name = "security_prefs")

class SecurityManager(private val context: Context) {

    private object Keys {
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    }

    val isPinEnabled: Flow<Boolean> = context.securityDataStore.data
        .map { prefs -> prefs[Keys.PIN_HASH]?.isNotEmpty() == true }

    val isBiometricEnabled: Flow<Boolean> = context.securityDataStore.data
        .map { prefs -> prefs[Keys.BIOMETRIC_ENABLED] ?: false }

    suspend fun setPin(pin: String) {
        val hash = sha256(pin)
        context.securityDataStore.edit { prefs ->
            prefs[Keys.PIN_HASH] = hash
        }
    }

    suspend fun verifyPin(pin: String): Boolean {
        val hash = sha256(pin)
        val stored = context.securityDataStore.data.map { it[Keys.PIN_HASH] ?: "" }.first()
        return hash == stored
    }

    suspend fun clearPin() {
        context.securityDataStore.edit { prefs ->
            prefs.remove(Keys.PIN_HASH)
            prefs[Keys.BIOMETRIC_ENABLED] = false
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.securityDataStore.edit { prefs ->
            prefs[Keys.BIOMETRIC_ENABLED] = enabled
        }
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
