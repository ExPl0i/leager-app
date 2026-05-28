package com.ledger.app.data.repository

import com.ledger.app.data.db.dao.CategoryTotal
import com.ledger.app.data.db.dao.DailyTotal
import com.ledger.app.data.db.dao.TransactionDao
import com.ledger.app.data.db.entity.TransactionEntity
import com.ledger.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TransactionRepository(private val dao: TransactionDao) {

    fun getAll(): Flow<List<Transaction>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    fun getInRange(from: LocalDate, to: LocalDate): Flow<List<Transaction>> =
        dao.getInRange(from.toEpochDay(), to.toEpochDay())
            .map { list -> list.map { it.toDomain() } }

    fun getByAccount(accountId: String): Flow<List<Transaction>> =
        dao.getByAccount(accountId).map { list -> list.map { it.toDomain() } }

    fun getByCategory(categoryId: String): Flow<List<Transaction>> =
        dao.getByCategory(categoryId).map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: String): Transaction? = dao.getById(id)?.toDomain()

    suspend fun save(transaction: Transaction) =
        dao.insert(TransactionEntity.fromDomain(transaction))

    suspend fun update(transaction: Transaction) =
        dao.update(TransactionEntity.fromDomain(transaction))

    suspend fun delete(id: String) = dao.deleteById(id)

    suspend fun getTotalExpense(from: LocalDate, to: LocalDate): Double =
        dao.getTotalExpense(from.toEpochDay(), to.toEpochDay())

    suspend fun getTotalIncome(from: LocalDate, to: LocalDate): Double =
        dao.getTotalIncome(from.toEpochDay(), to.toEpochDay())

    suspend fun getExpenseByCategory(from: LocalDate, to: LocalDate): List<CategoryTotal> =
        dao.getExpenseByCategory(from.toEpochDay(), to.toEpochDay())

    suspend fun getDailyExpense(from: LocalDate, to: LocalDate): List<DailyTotal> =
        dao.getDailyExpense(from.toEpochDay(), to.toEpochDay())

    suspend fun getIncomeByCategory(from: LocalDate, to: LocalDate): List<CategoryTotal> =
        dao.getIncomeByCategory(from.toEpochDay(), to.toEpochDay())

    suspend fun insert(transaction: Transaction) =
        dao.insert(TransactionEntity.fromDomain(transaction))

    suspend fun deleteAll() = dao.deleteAll()
}
