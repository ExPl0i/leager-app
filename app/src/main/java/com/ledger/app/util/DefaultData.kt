package com.ledger.app.util

import com.ledger.app.domain.model.Account
import com.ledger.app.domain.model.AccountType
import com.ledger.app.domain.model.Category
import com.ledger.app.domain.model.CategoryType

object DefaultData {

    val accounts = listOf(
        Account(id = "main",     name = "Основной",   currency = "RUB", balance = 0.0,  type = AccountType.CARD,    color = "#C5FF4A", last4 = null, sortOrder = 0),
        Account(id = "cash",     name = "Наличные",   currency = "RUB", balance = 0.0,  type = AccountType.CASH,    color = "#FF6B5B", sortOrder = 1),
        Account(id = "savings",  name = "Накопления", currency = "RUB", balance = 0.0,  type = AccountType.SAVINGS, color = "#9B8BFF", sortOrder = 2)
    )

    val categories = listOf(
        Category(id = "food",      name = "Продукты",    iconCode = "food",      color = "#C5FF4A", type = CategoryType.EXPENSE, budget = 25000.0, sortOrder = 0),
        Category(id = "cafe",      name = "Кафе",        iconCode = "cafe",      color = "#FF6B5B", type = CategoryType.EXPENSE, budget = 12000.0, sortOrder = 1),
        Category(id = "transport", name = "Транспорт",   iconCode = "transport", color = "#52E0C4", type = CategoryType.EXPENSE, budget = 6000.0,  sortOrder = 2),
        Category(id = "subs",      name = "Подписки",    iconCode = "subs",      color = "#9B8BFF", type = CategoryType.EXPENSE, budget = 3500.0,  sortOrder = 3),
        Category(id = "health",    name = "Здоровье",    iconCode = "health",    color = "#FFD24A", type = CategoryType.EXPENSE, budget = 8000.0,  sortOrder = 4),
        Category(id = "fun",       name = "Развлечения", iconCode = "fun",       color = "#FF9DC4", type = CategoryType.EXPENSE, budget = 10000.0, sortOrder = 5),
        Category(id = "home",      name = "Дом",         iconCode = "home",      color = "#7BB8FF", type = CategoryType.EXPENSE, budget = null,    sortOrder = 6),
        Category(id = "clothes",   name = "Одежда",      iconCode = "clothes",   color = "#E89AFF", type = CategoryType.EXPENSE, budget = 6000.0,  sortOrder = 7),
        Category(id = "other_exp", name = "Прочее",      iconCode = "other",     color = "#A0A0A0", type = CategoryType.EXPENSE, budget = null,    sortOrder = 8),
        Category(id = "salary",    name = "Зарплата",    iconCode = "salary",    color = "#C5FF4A", type = CategoryType.INCOME,  budget = null,    sortOrder = 0),
        Category(id = "freelance", name = "Фриланс",     iconCode = "freelance", color = "#52E0C4", type = CategoryType.INCOME,  budget = null,    sortOrder = 1),
        Category(id = "gift",      name = "Подарки",     iconCode = "gift",      color = "#FFD24A", type = CategoryType.INCOME,  budget = null,    sortOrder = 2),
        Category(id = "other_inc", name = "Прочее",      iconCode = "other",     color = "#A0A0A0", type = CategoryType.INCOME,  budget = null,    sortOrder = 3)
    )
}
