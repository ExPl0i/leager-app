package com.ledger.app

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ledger.app.data.db.LedgerDatabase
import com.ledger.app.data.prefs.PrefsManager
import com.ledger.app.data.repository.AccountRepository
import com.ledger.app.data.repository.CategoryRepository
import com.ledger.app.data.repository.RecurringRepository
import com.ledger.app.data.repository.TransactionRepository
import com.ledger.app.security.SecurityManager
import com.ledger.app.util.DefaultData
import com.ledger.app.worker.RecurringTransactionWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LedgerApplication : Application() {

    private val db by lazy { LedgerDatabase.getInstance(this) }

    val accountRepo by lazy { AccountRepository(db.accountDao()) }
    val categoryRepo by lazy { CategoryRepository(db.categoryDao()) }
    val transactionRepo by lazy { TransactionRepository(db.transactionDao()) }
    val recurringRepo by lazy { RecurringRepository(db.recurringConfigDao()) }

    val securityManager by lazy { SecurityManager(this) }
    val prefsManager by lazy { PrefsManager(this) }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            accountRepo.seedIfEmpty(DefaultData.accounts)
            categoryRepo.seedIfEmpty(DefaultData.categories)
        }
        scheduleRecurringWorker()
    }

    private fun scheduleRecurringWorker() {
        val request = PeriodicWorkRequestBuilder<RecurringTransactionWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "recurring_transactions",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
