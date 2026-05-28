package com.ledger.app.data.repository

import com.ledger.app.data.db.dao.CategoryDao
import com.ledger.app.data.db.entity.CategoryEntity
import com.ledger.app.domain.model.Category
import com.ledger.app.domain.model.CategoryType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(private val dao: CategoryDao) {

    fun getAll(): Flow<List<Category>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    fun getExpenseCategories(): Flow<List<Category>> =
        dao.getByType(CategoryType.EXPENSE.name).map { list -> list.map { it.toDomain() } }

    fun getIncomeCategories(): Flow<List<Category>> =
        dao.getByType(CategoryType.INCOME.name).map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: String): Category? = dao.getById(id)?.toDomain()

    suspend fun save(category: Category) = dao.insert(CategoryEntity.fromDomain(category))

    suspend fun update(category: Category) = dao.update(CategoryEntity.fromDomain(category))

    suspend fun delete(category: Category) = dao.delete(CategoryEntity.fromDomain(category))

    suspend fun seedIfEmpty(categories: List<Category>) {
        if (dao.count() == 0) {
            dao.insertAll(categories.map { CategoryEntity.fromDomain(it) })
        }
    }
}
