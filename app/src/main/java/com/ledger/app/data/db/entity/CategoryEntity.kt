package com.ledger.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ledger.app.domain.model.Category
import com.ledger.app.domain.model.CategoryType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconCode: String,
    val color: String,
    val type: String,
    val budget: Double?,
    val sortOrder: Int
) {
    fun toDomain() = Category(
        id = id,
        name = name,
        iconCode = iconCode,
        color = color,
        type = CategoryType.valueOf(type),
        budget = budget,
        sortOrder = sortOrder
    )

    companion object {
        fun fromDomain(c: Category) = CategoryEntity(
            id = c.id,
            name = c.name,
            iconCode = c.iconCode,
            color = c.color,
            type = c.type.name,
            budget = c.budget,
            sortOrder = c.sortOrder
        )
    }
}
