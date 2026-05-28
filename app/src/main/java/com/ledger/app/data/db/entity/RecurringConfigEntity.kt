package com.ledger.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ledger.app.domain.model.RecurringConfig
import com.ledger.app.domain.model.RecurringInterval
import com.ledger.app.domain.model.TransactionType
import java.time.LocalDate

@Entity(tableName = "recurring_configs")
data class RecurringConfigEntity(
    @PrimaryKey val id: String,
    val templateAmount: Double,
    val templateType: String,
    val templateCategoryId: String,
    val templateAccountId: String,
    val templateToAccountId: String?,
    val templateNote: String,
    val interval: String,
    val intervalCount: Int,
    val nextEpochDay: Long,
    val endEpochDay: Long?,
    val isActive: Boolean
) {
    fun toDomain() = RecurringConfig(
        id = id,
        templateAmount = templateAmount,
        templateType = TransactionType.valueOf(templateType),
        templateCategoryId = templateCategoryId,
        templateAccountId = templateAccountId,
        templateToAccountId = templateToAccountId,
        templateNote = templateNote,
        interval = RecurringInterval.valueOf(interval),
        intervalCount = intervalCount,
        nextDate = LocalDate.ofEpochDay(nextEpochDay),
        endDate = endEpochDay?.let { LocalDate.ofEpochDay(it) },
        isActive = isActive
    )

    companion object {
        fun fromDomain(r: RecurringConfig) = RecurringConfigEntity(
            id = r.id,
            templateAmount = r.templateAmount,
            templateType = r.templateType.name,
            templateCategoryId = r.templateCategoryId,
            templateAccountId = r.templateAccountId,
            templateToAccountId = r.templateToAccountId,
            templateNote = r.templateNote,
            interval = r.interval.name,
            intervalCount = r.intervalCount,
            nextEpochDay = r.nextDate.toEpochDay(),
            endEpochDay = r.endDate?.toEpochDay(),
            isActive = r.isActive
        )
    }
}
