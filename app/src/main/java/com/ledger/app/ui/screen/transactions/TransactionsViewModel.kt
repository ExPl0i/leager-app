package com.ledger.app.ui.screen.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ledger.app.LedgerApplication
import com.ledger.app.domain.model.Category
import com.ledger.app.domain.model.Transaction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TransactionsState(
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val categories: Map<String, Category> = emptyMap(),
    val accountNames: Map<String, String> = emptyMap(),
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

class TransactionsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as LedgerApplication

    private val _state = MutableStateFlow(TransactionsState())
    val state: StateFlow<TransactionsState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                app.transactionRepo.getAll(),
                app.categoryRepo.getAll(),
                app.accountRepo.getActiveAccounts()
            ) { transactions, categories, accounts ->
                Triple(transactions, categories, accounts)
            }.collect { (transactions, categories, accounts) ->
                val current = _state.value
                val filtered = filterTransactions(transactions, current.searchQuery)
                _state.value = current.copy(
                    transactions = transactions,
                    filteredTransactions = filtered,
                    categories = categories.associateBy { it.id },
                    accountNames = accounts.associate { it.id to it.name },
                    isLoading = false
                )
            }
        }
    }

    fun onSearch(query: String) {
        val current = _state.value
        _state.value = current.copy(
            searchQuery = query,
            filteredTransactions = filterTransactions(current.transactions, query)
        )
    }

    private fun filterTransactions(list: List<Transaction>, query: String): List<Transaction> {
        if (query.isBlank()) return list
        val q = query.lowercase()
        return list.filter { tx ->
            tx.note.lowercase().contains(q) || tx.tags.any { it.lowercase().contains(q) }
        }
    }
}
