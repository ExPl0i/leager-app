package com.ledger.app.data.repository

import com.ledger.app.data.db.dao.AccountDao
import com.ledger.app.data.db.entity.AccountEntity
import com.ledger.app.domain.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepository(private val dao: AccountDao) {

    fun getActiveAccounts(): Flow<List<Account>> =
        dao.getActiveAccounts().map { list -> list.map { it.toDomain() } }

    fun getAllAccounts(): Flow<List<Account>> =
        dao.getAllAccounts().map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: String): Account? = dao.getById(id)?.toDomain()

    suspend fun save(account: Account) = dao.insert(AccountEntity.fromDomain(account))

    suspend fun update(account: Account) = dao.update(AccountEntity.fromDomain(account))

    suspend fun adjustBalance(id: String, delta: Double) = dao.updateBalance(id, delta)

    suspend fun archive(id: String) = dao.archive(id)

    suspend fun delete(account: Account) = dao.delete(AccountEntity.fromDomain(account))

    suspend fun resetAllBalances() = dao.resetAllBalances()

    suspend fun seedIfEmpty(accounts: List<Account>) {
        if (dao.count() == 0) {
            dao.insertAll(accounts.map { AccountEntity.fromDomain(it) })
        }
    }
}
