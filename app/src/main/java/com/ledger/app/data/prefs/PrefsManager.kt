package com.ledger.app.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appDataStore by preferencesDataStore(name = "app_prefs")

class PrefsManager(private val context: Context) {

    private object Keys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val USD_RATE = doublePreferencesKey("usd_rate")
        val EUR_RATE = doublePreferencesKey("eur_rate")
        val NET_WORTH_CURRENCY = stringPreferencesKey("net_worth_currency")
    }

    val isDarkTheme: Flow<Boolean> = context.appDataStore.data
        .map { prefs -> prefs[Keys.DARK_THEME] ?: true }

    val usdRate: Flow<Double> = context.appDataStore.data
        .map { prefs -> prefs[Keys.USD_RATE] ?: 92.0 }

    val eurRate: Flow<Double> = context.appDataStore.data
        .map { prefs -> prefs[Keys.EUR_RATE] ?: 99.0 }

    val netWorthCurrency: Flow<String> = context.appDataStore.data
        .map { prefs -> prefs[Keys.NET_WORTH_CURRENCY] ?: "RUB" }

    suspend fun setDarkTheme(dark: Boolean) {
        context.appDataStore.edit { prefs -> prefs[Keys.DARK_THEME] = dark }
    }

    suspend fun setUsdRate(rate: Double) {
        context.appDataStore.edit { prefs -> prefs[Keys.USD_RATE] = rate }
    }

    suspend fun setEurRate(rate: Double) {
        context.appDataStore.edit { prefs -> prefs[Keys.EUR_RATE] = rate }
    }

    suspend fun setNetWorthCurrency(currency: String) {
        context.appDataStore.edit { prefs -> prefs[Keys.NET_WORTH_CURRENCY] = currency }
    }
}
