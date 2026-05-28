package com.ledger.app.data

import com.ledger.app.data.repository.AccountRepository
import com.ledger.app.data.repository.CategoryRepository
import com.ledger.app.data.repository.TransactionRepository
import com.ledger.app.domain.model.Account
import com.ledger.app.domain.model.AccountType
import com.ledger.app.domain.model.Category
import com.ledger.app.domain.model.CategoryType
import com.ledger.app.domain.model.Transaction
import com.ledger.app.domain.model.TransactionType
import kotlinx.coroutines.flow.first
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
import java.util.UUID

class CsvImporter(
    private val accountRepo: AccountRepository,
    private val categoryRepo: CategoryRepository,
    private val transactionRepo: TransactionRepository
) {
    data class Result(val imported: Int, val skipped: Int, val errors: List<String>)

    suspend fun import(stream: InputStream): Result {
        val accountCache = accountRepo.getAllAccounts().first()
            .associateBy { it.name.trim().lowercase() }
            .toMutableMap<String, Account>()

        val categoryCache = categoryRepo.getAll().first()
            .associateBy { "${it.type.name}:${it.name.trim().lowercase()}" }
            .toMutableMap<String, Category>()

        var imported = 0
        var skipped = 0
        val errors = mutableListOf<String>()

        val lines = stream.bufferedReader(Charsets.UTF_8).readLines()
        if (lines.size < 2) return Result(0, 0, emptyList())

        lines.drop(1).forEachIndexed { idx, raw ->
            val rowNum = idx + 2
            if (raw.isBlank()) return@forEachIndexed

            try {
                val cols = parseLine(raw)

                val dateStr   = cols.getOrElse(0) { "" }.trim()
                val typeStr   = cols.getOrElse(1) { "" }.trim()
                val amountStr = cols.getOrElse(2) { "" }.trim()
                val currency  = cols.getOrElse(3) { "RUB" }.trim().ifBlank { "RUB" }
                val accName   = cols.getOrElse(4) { "" }.trim()
                val toAccName = cols.getOrElse(5) { "" }.trim()
                val catName   = cols.getOrElse(6) { "" }.trim()
                val comment   = cols.getOrElse(7) { "" }.trim()
                val uidStr    = cols.getOrElse(8) { "" }.trim()

                if (accName.isBlank()) {
                    errors.add("Строка $rowNum: пустое название счёта")
                    skipped++
                    return@forEachIndexed
                }

                val date = try {
                    LocalDate.parse(dateStr)
                } catch (e: DateTimeParseException) {
                    throw IllegalArgumentException("Неверный формат даты: '$dateStr'")
                }

                val rawAmount = amountStr.toDoubleOrNull()
                    ?: throw IllegalArgumentException("Неверная сумма: '$amountStr'")

                val txType = when (typeStr.lowercase()) {
                    "income"   -> TransactionType.INCOME
                    "expense"  -> TransactionType.EXPENSE
                    "transfer" -> TransactionType.TRANSFER
                    else       -> throw IllegalArgumentException("Неизвестный тип: '$typeStr'")
                }

                val amount = if (txType == TransactionType.EXPENSE) -rawAmount else rawAmount

                val account = resolveAccount(accName, currency, accountCache)

                val toAccount = if (txType == TransactionType.TRANSFER && toAccName.isNotBlank()) {
                    resolveAccount(toAccName, currency, accountCache)
                } else null

                val category = resolveCategory(catName, txType, categoryCache)

                val txId = uidStr.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()

                transactionRepo.save(
                    Transaction(
                        id = txId,
                        amount = amount,
                        type = txType,
                        categoryId = category.id,
                        accountId = account.id,
                        toAccountId = toAccount?.id,
                        note = comment,
                        date = date,
                        time = LocalTime.of(0, 0)
                    )
                )
                imported++
            } catch (e: Exception) {
                errors.add("Строка $rowNum: ${e.message}")
                skipped++
            }
        }

        return Result(imported, skipped, errors)
    }

    private suspend fun resolveAccount(
        name: String,
        currency: String,
        cache: MutableMap<String, Account>
    ): Account {
        val key = name.lowercase()
        return cache[key] ?: run {
            val new = Account(
                id = UUID.randomUUID().toString(),
                name = name,
                currency = currency,
                balance = 0.0,
                type = AccountType.CARD,
                color = COLORS[cache.size % COLORS.size],
                sortOrder = cache.size
            )
            accountRepo.save(new)
            cache[key] = new
            new
        }
    }

    private suspend fun resolveCategory(
        name: String,
        txType: TransactionType,
        cache: MutableMap<String, Category>
    ): Category {
        val catType = if (txType == TransactionType.INCOME) CategoryType.INCOME else CategoryType.EXPENSE
        val effectiveName = name.ifBlank {
            if (catType == CategoryType.INCOME) "Прочие доходы" else "Прочие расходы"
        }
        val key = "${catType.name}:${effectiveName.lowercase()}"
        return cache[key] ?: run {
            val new = Category(
                id = UUID.randomUUID().toString(),
                name = effectiveName,
                iconCode = "other",
                color = COLORS[cache.size % COLORS.size],
                type = catType,
                sortOrder = cache.size
            )
            categoryRepo.save(new)
            cache[key] = new
            new
        }
    }

    // Handles quoted fields and commas inside quotes
    private fun parseLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        for (ch in line) {
            when {
                ch == '"'              -> inQuotes = !inQuotes
                ch == ',' && !inQuotes -> { result.add(sb.toString()); sb.clear() }
                else                   -> sb.append(ch)
            }
        }
        result.add(sb.toString())
        return result
    }

    companion object {
        private val COLORS = listOf(
            "#C5FF4A", "#FF6B5B", "#52E0C4", "#9B8BFF",
            "#FFD24A", "#FF9DC4", "#7BB8FF", "#A0A0A0"
        )
    }
}
