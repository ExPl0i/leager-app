package com.ledger.app.data.repository

import com.ledger.app.data.db.dao.RecurringConfigDao
import com.ledger.app.data.db.entity.RecurringConfigEntity
import com.ledger.app.domain.model.RecurringConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class RecurringRepository(private val dao: RecurringConfigDao) {

    fun getActive(): Flow<List<RecurringConfig>> =
        dao.getActive().map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: String): RecurringConfig? = dao.getById(id)?.toDomain()

    suspend fun getDue(today: LocalDate): List<RecurringConfig> =
        dao.getDue(today.toEpochDay()).map { it.toDomain() }

    suspend fun save(config: RecurringConfig) =
        dao.insert(RecurringConfigEntity.fromDomain(config))

    suspend fun update(config: RecurringConfig) =
        dao.update(RecurringConfigEntity.fromDomain(config))

    suspend fun deactivate(id: String) = dao.deactivate(id)

    suspend fun advance(config: RecurringConfig, today: LocalDate) {
        val next = when (config.interval) {
            com.ledger.app.domain.model.RecurringInterval.DAILY   -> today.plusDays(config.intervalCount.toLong())
            com.ledger.app.domain.model.RecurringInterval.WEEKLY  -> today.plusWeeks(config.intervalCount.toLong())
            com.ledger.app.domain.model.RecurringInterval.MONTHLY -> today.plusMonths(config.intervalCount.toLong())
            com.ledger.app.domain.model.RecurringInterval.YEARLY  -> today.plusYears(config.intervalCount.toLong())
        }
        val isActive = config.endDate == null || next <= config.endDate
        update(config.copy(nextDate = next, isActive = isActive))
    }
}
