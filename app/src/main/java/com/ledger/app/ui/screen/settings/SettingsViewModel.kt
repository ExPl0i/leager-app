package com.ledger.app.ui.screen.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ledger.app.LedgerApplication
import com.ledger.app.data.CsvImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class ImportStatus {
    object Idle : ImportStatus()
    object Importing : ImportStatus()
    data class Done(val imported: Int, val skipped: Int) : ImportStatus()
    data class Error(val message: String) : ImportStatus()
}

data class SettingsState(
    val pinEnabled: Boolean = false,
    val biometricEnabled: Boolean = false,
    val darkTheme: Boolean = true,
    val usdRate: Double = 92.0,
    val eurRate: Double = 99.0,
    val netWorthCurrency: String = "RUB",
    val navigateToPinSetup: Boolean = false,
    val importStatus: ImportStatus = ImportStatus.Idle
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as LedgerApplication
    private val security = app.securityManager
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                combine(security.isPinEnabled, security.isBiometricEnabled, app.prefsManager.isDarkTheme) { pin, bio, dark -> Triple(pin, bio, dark) },
                app.prefsManager.usdRate,
                app.prefsManager.eurRate,
                app.prefsManager.netWorthCurrency
            ) { sec, usdRate, eurRate, netWorthCurrency ->
                _state.value.copy(
                    pinEnabled = sec.first, biometricEnabled = sec.second, darkTheme = sec.third,
                    usdRate = usdRate, eurRate = eurRate, netWorthCurrency = netWorthCurrency,
                    navigateToPinSetup = false
                )
            }.collect { _state.value = it }
        }
    }

    fun togglePin() {
        viewModelScope.launch {
            if (_state.value.pinEnabled) {
                security.clearPin()
            } else {
                _state.value = _state.value.copy(navigateToPinSetup = true)
            }
        }
    }

    fun onNavigatedToPinSetup() {
        _state.value = _state.value.copy(navigateToPinSetup = false)
    }

    fun toggleBiometric() {
        viewModelScope.launch {
            security.setBiometricEnabled(!_state.value.biometricEnabled)
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            app.prefsManager.setDarkTheme(!_state.value.darkTheme)
        }
    }

    fun setUsdRate(rate: Double) {
        viewModelScope.launch { app.prefsManager.setUsdRate(rate) }
    }

    fun setEurRate(rate: Double) {
        viewModelScope.launch { app.prefsManager.setEurRate(rate) }
    }

    fun setNetWorthCurrency(currency: String) {
        viewModelScope.launch { app.prefsManager.setNetWorthCurrency(currency) }
    }

    fun importCsv(uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(importStatus = ImportStatus.Importing)
            try {
                val result = withContext(Dispatchers.IO) {
                    val stream = app.contentResolver.openInputStream(uri)
                        ?: throw Exception("Не удалось открыть файл")
                    stream.use { s ->
                        CsvImporter(app.accountRepo, app.categoryRepo, app.transactionRepo).import(s)
                    }
                }
                _state.value = _state.value.copy(
                    importStatus = ImportStatus.Done(result.imported, result.skipped)
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    importStatus = ImportStatus.Error(e.message ?: "Неизвестная ошибка")
                )
            }
        }
    }

    fun dismissImport() {
        _state.value = _state.value.copy(importStatus = ImportStatus.Idle)
    }

    fun clearData() {
        viewModelScope.launch {
            app.transactionRepo.deleteAll()
        }
    }

    class Factory(private val app: LedgerApplication) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
            SettingsViewModel(app) as T
    }
}
