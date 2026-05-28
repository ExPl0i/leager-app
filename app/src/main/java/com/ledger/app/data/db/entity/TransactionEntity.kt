package com.ledger.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ledger.app.domain.model.Transaction
import com.ledger.app.domain.model.TransactionType
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val type: String,
    val categoryId: String,
    val accountId: String,
    val toAccountId: String?,
    val note: String,
    val dateEpochDay: Long,
    val timeMinuteOfDay: Int,
    val tags: String,
    val recurringConfigId: String?,
    val attachmentUri: String?
) {
    fun toDomain() = Transaction(
        id = id,
        amount = amount,
        type = TransactionType.valueOf(type),
        categoryId = categoryId,
        accountId = accountId,
        toAccountId = toAccountId,
        note = note,
        date = LocalDate.ofEpochDay(dateEpochDay),
        time = LocalTime.of(timeMinuteOfDay / 60, timeMinuteOfDay % 60),
        tags = if (tags.isBlank()) emptyList() else tags.split(","),
        recurringConfigId = recurringConfigId,
        attachmentUri = attachmentUri
    )

    companion object {
        fun fromDomain(t: Transaction) = TransactionEntity(
            id = t.id,
            amount = t.amount,
            type = t.type.name,
            categoryId = t.categoryId,
            accountId = t.accountId,
            toAccountId = t.toAccountId,
            note = t.note,
            dateEpochDay = t.date.toEpochDay(),
            timeMinuteOfDay = t.time.hour * 60 + t.time.minute,
            tags = t.tags.joinToString(","),
            recurringConfigId = t.recurringConfigId,
            attachmentUri = t.attachmentUri
        )
    }
}
