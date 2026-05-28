package com.ledger.app.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ledger.app.LedgerApplication
import com.ledger.app.domain.model.Account
import com.ledger.app.domain.model.Category
import com.ledger.app.domain.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class TransactionDetailState(
    val transaction: Transaction? = null,
    val category: Category? = null,
    val account: Account? = null,
    val toAccount: Account? = null,
    val deleted: Boolean = false
)

class TransactionDetailViewModel(
    private val app: LedgerApplication,
    private val transactionId: String
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionDetailState())
    val state: StateFlow<TransactionDetailState> = _state.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            val tx = app.transactionRepo.getById(transactionId) ?: return@launch
            val cat = app.categoryRepo.getById(tx.categoryId)
            val acc = app.accountRepo.getById(tx.accountId)
            val toAcc = tx.toAccountId?.let { app.accountRepo.getById(it) }
            _state.value = TransactionDetailState(
                transaction = tx,
                category = cat,
                account = acc,
                toAccount = toAcc
            )
        }
    }

    fun delete() {
        val tx = _state.value.transaction ?: return
        viewModelScope.launch {
            app.transactionRepo.delete(tx.id)
            app.accountRepo.adjustBalance(tx.accountId, -tx.amount)
            tx.toAccountId?.let { toId ->
                app.accountRepo.adjustBalance(toId, tx.amount)
            }
            _state.value = _state.value.copy(deleted = true)
        }
    }

    fun duplicate() {
        val tx = _state.value.transaction ?: return
        viewModelScope.launch {
            val copy = tx.copy(
                id = UUID.randomUUID().toString(),
                date = java.time.LocalDate.now(),
                time = java.time.LocalTime.now()
            )
            app.transactionRepo.save(copy)
            app.accountRepo.adjustBalance(copy.accountId, copy.amount)
            copy.toAccountId?.let { toId ->
                app.accountRepo.adjustBalance(toId, -copy.amount)
            }
        }
    }

    class Factory(private val app: LedgerApplication, private val id: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return TransactionDetailViewModel(app, id) as T
        }
    }
}
