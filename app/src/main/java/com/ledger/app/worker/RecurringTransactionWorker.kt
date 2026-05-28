package com.ledger.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ledger.app.LedgerApplication
import com.ledger.app.domain.model.Transaction
import com.ledger.app.domain.model.TransactionType
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class RecurringTransactionWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as LedgerApplication
        val today = LocalDate.now()
        val due = app.recurringRepo.getDue(today)

        for (config in due) {
            val signedAmount = when (config.templateType) {
                TransactionType.EXPENSE  -> -config.templateAmount
                TransactionType.INCOME   -> config.templateAmount
                TransactionType.TRANSFER -> -config.templateAmount
            }
            val tx = Transaction(
                id = UUID.randomUUID().toString(),
                amount = signedAmount,
                type = config.templateType,
                categoryId = config.templateCategoryId,
                accountId = config.templateAccountId,
                toAccountId = config.templateToAccountId,
                note = config.templateNote,
                date = today,
                time = LocalTime.now(),
                tags = emptyList(),
                recurringConfigId = config.id,
                attachmentUri = null
            )
            app.transactionRepo.save(tx)

            app.accountRepo.adjustBalance(config.templateAccountId, signedAmount)
            config.templateToAccountId?.let { toId ->
                app.accountRepo.adjustBalance(toId, config.templateAmount)
            }

            app.recurringRepo.advance(config, today)
        }

        return Result.success()
    }
}
