package com.ledger.app.ui.screen.accounts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ledger.app.LedgerApplication
import com.ledger.app.domain.model.Account
import com.ledger.app.domain.model.AccountType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

data class AccountsState(
    val accounts: List<Account> = emptyList(),
    val netWorth: Double = 0.0,
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val showDialog: Boolean = false,
    val editTarget: Account? = null
)

class AccountsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as LedgerApplication
    private val _state = MutableStateFlow(AccountsState())
    val state: StateFlow<AccountsState> = _state.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            app.accountRepo.getActiveAccounts().collect { accounts ->
                val netWorth = accounts.fold(0.0) { sum, acc ->
                    if (!acc.includeInTotal) return@fold sum
                    val rate = when (acc.currency) { "USD" -> 92.0; "EUR" -> 99.0; else -> 1.0 }
                    sum + acc.balance * rate
                }
                val now = LocalDate.now()
                val monthStart = now.withDayOfMonth(1)
                val income = app.transactionRepo.getTotalIncome(monthStart, now)
                val expense = app.transactionRepo.getTotalExpense(monthStart, now)
                _state.value = _state.value.copy(
                    accounts = accounts,
                    netWorth = netWorth,
                    monthIncome = income,
                    monthExpense = expense
                )
            }
        }
    }

    fun openCreate() {
        _state.value = _state.value.copy(showDialog = true, editTarget = null)
    }

    fun openEdit(acc: Account) {
        _state.value = _state.value.copy(showDialog = true, editTarget = acc)
    }

    fun dismissDialog() {
        _state.value = _state.value.copy(showDialog = false, editTarget = null)
    }

    fun saveAccount(
        name: String,
        type: AccountType,
        currency: String,
        color: String,
        includeInTotal: Boolean,
        balance: Double
    ) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            val existing = _state.value.editTarget
            val account = if (existing != null) {
                existing.copy(
                    name = trimmed,
                    type = type,
                    currency = currency,
                    color = color,
                    includeInTotal = includeInTotal,
                    balance = balance
                )
            } else {
                Account(
                    id = UUID.randomUUID().toString(),
                    name = trimmed,
                    type = type,
                    currency = currency,
                    color = color,
                    balance = balance,
                    includeInTotal = includeInTotal,
                    sortOrder = _state.value.accounts.size
                )
            }
            if (existing != null) app.accountRepo.update(account)
            else app.accountRepo.save(account)
            dismissDialog()
        }
    }

    fun archiveAccount(acc: Account) {
        viewModelScope.launch {
            app.accountRepo.archive(acc.id)
            dismissDialog()
        }
    }
}
