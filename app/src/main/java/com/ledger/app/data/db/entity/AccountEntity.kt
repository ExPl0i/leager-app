package com.ledger.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ledger.app.domain.model.Account
import com.ledger.app.domain.model.AccountType

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val currency: String,
    val balance: Double,
    val type: String,
    val color: String,
    val last4: String?,
    val isArchived: Boolean,
    val sortOrder: Int,
    val includeInTotal: Boolean = true
) {
    fun toDomain() = Account(
        id = id,
        name = name,
        currency = currency,
        balance = balance,
        type = AccountType.valueOf(type),
        color = color,
        last4 = last4,
        isArchived = isArchived,
        sortOrder = sortOrder,
        includeInTotal = includeInTotal
    )

    companion object {
        fun fromDomain(a: Account) = AccountEntity(
            id = a.id,
            name = a.name,
            currency = a.currency,
            balance = a.balance,
            type = a.type.name,
            color = a.color,
            last4 = a.last4,
            isArchived = a.isArchived,
            sortOrder = a.sortOrder,
            includeInTotal = a.includeInTotal
        )
    }
}
