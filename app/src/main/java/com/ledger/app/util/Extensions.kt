package com.ledger.app.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val RU_MONTHS = arrayOf(
    "янв", "фев", "мар", "апр", "май", "июн",
    "июл", "авг", "сен", "окт", "ноя", "дек"
)

fun LocalDate.formatShort(): String = "${dayOfMonth} ${RU_MONTHS[monthValue - 1]}"

fun LocalDate.formatMedium(): String = "${dayOfMonth} ${RU_MONTHS[monthValue - 1]} ${year}"

fun LocalDate.formatFull(): String {
    val dayOfWeek = when (dayOfWeek.value) {
        1 -> "пн"; 2 -> "вт"; 3 -> "ср"; 4 -> "чт"; 5 -> "пт"; 6 -> "сб"; else -> "вс"
    }
    return "${dayOfWeek.uppercase()}, ${dayOfMonth} ${RU_MONTHS[monthValue - 1].uppercase()} ${year}"
}

fun LocalDate.isToday(): Boolean = this == LocalDate.now()
fun LocalDate.isYesterday(): Boolean = this == LocalDate.now().minusDays(1)

fun LocalTime.formatHm(): String = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

fun Double.formatMoney(currency: String = "RUB"): String {
    val abs = Math.abs(this)
    val grouped = String.format(Locale("ru", "RU"), "%,.0f", abs)
        .replace(',', ' ')
    return when (currency) {
        "USD" -> "\$$grouped"
        "EUR" -> "€$grouped"
        else  -> "$grouped ₽"
    }
}

fun Double.formatMoneyFull(currency: String = "RUB"): String {
    val abs = Math.abs(this)
    val grouped = String.format(Locale("ru", "RU"), "%,.2f", abs)
        .replace(',', ' ')
    return when (currency) {
        "USD" -> "\$$grouped"
        "EUR" -> "€$grouped"
        else  -> "$grouped ₽"
    }
}

fun Double.formatSigned(currency: String = "RUB"): String {
    val prefix = if (this >= 0) "+" else "−"
    return "$prefix${Math.abs(this).formatMoney(currency)}"
}
