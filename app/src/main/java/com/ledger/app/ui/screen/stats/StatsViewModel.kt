package com.ledger.app.ui.screen.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ledger.app.LedgerApplication
import com.ledger.app.domain.model.Category
import com.ledger.app.ui.components.BarEntry
import com.ledger.app.ui.components.DonutSegment
import com.ledger.app.ui.components.parseHexColor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class StatsPeriod(val label: String) { DAY("DAY"), WEEK("WEEK"), MONTH("MONTH"), YEAR("YEAR") }

data class CategoryBreakdown(val category: Category, val spent: Double, val pct: Float)

data class StatsState(
    val period: StatsPeriod = StatsPeriod.MONTH,
    val periodOffset: Int = 0,
    val periodLabel: String = "",
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val donutSegments: List<DonutSegment> = emptyList(),
    val categoryBreakdown: List<CategoryBreakdown> = emptyList(),
    val incomeDonutSegments: List<DonutSegment> = emptyList(),
    val incomeCategoryBreakdown: List<CategoryBreakdown> = emptyList(),
    val monthlyBars: List<BarEntry> = emptyList(),
    val heatmapValues: List<Float> = emptyList(),
    val isLoading: Boolean = true
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as LedgerApplication
    private val _state = MutableStateFlow(StatsState())
    val state: StateFlow<StatsState> = _state.asStateFlow()

    init { load() }

    fun setPeriod(period: StatsPeriod) {
        _state.value = _state.value.copy(period = period, periodOffset = 0, isLoading = true)
        load()
    }

    fun shiftPeriod(delta: Int) {
        _state.value = _state.value.copy(periodOffset = _state.value.periodOffset + delta, isLoading = true)
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val now = LocalDate.now()
            val period = _state.value.period
            val offset = _state.value.periodOffset
            val (from, to) = getPeriodRange(now, period, offset)
            val label = getPeriodLabel(now, period, offset)

            val categories = app.categoryRepo.getAll().first()
            val catMap = categories.associateBy { it.id }

            val totalIncome  = app.transactionRepo.getTotalIncome(from, to)
            val totalExpense = app.transactionRepo.getTotalExpense(from, to)

            // Expense donut
            val spentByCategory = app.transactionRepo.getExpenseByCategory(from, to)
            val donutSegments = spentByCategory
                .filter { it.total > 0 }
                .sortedByDescending { it.total }
                .mapNotNull { ct ->
                    val cat = catMap[ct.categoryId] ?: return@mapNotNull null
                    DonutSegment(value = ct.total.toFloat(), color = parseHexColor(cat.color), label = cat.name, id = cat.id)
                }
            val breakdown = spentByCategory
                .filter { it.total > 0 }
                .sortedByDescending { it.total }
                .mapNotNull { ct ->
                    val cat = catMap[ct.categoryId] ?: return@mapNotNull null
                    CategoryBreakdown(cat, ct.total, if (totalExpense > 0) (ct.total / totalExpense).toFloat() else 0f)
                }

            // Income donut
            val earnedByCategory = app.transactionRepo.getIncomeByCategory(from, to)
            val incomeSegments = earnedByCategory
                .filter { it.total > 0 }
                .sortedByDescending { it.total }
                .mapNotNull { ct ->
                    val cat = catMap[ct.categoryId] ?: return@mapNotNull null
                    DonutSegment(value = ct.total.toFloat(), color = parseHexColor(cat.color), label = cat.name, id = cat.id)
                }
            val incomeBreakdown = earnedByCategory
                .filter { it.total > 0 }
                .sortedByDescending { it.total }
                .mapNotNull { ct ->
                    val cat = catMap[ct.categoryId] ?: return@mapNotNull null
                    CategoryBreakdown(cat, ct.total, if (totalIncome > 0) (ct.total / totalIncome).toFloat() else 0f)
                }

            // 12-month bar chart (always last 12 calendar months)
            val monthBars = (11 downTo 0).map { monthsAgo ->
                val month = now.minusMonths(monthsAgo.toLong())
                val mStart = month.withDayOfMonth(1)
                val mEnd = month.withDayOfMonth(month.lengthOfMonth())
                val inc = app.transactionRepo.getTotalIncome(mStart, mEnd).toFloat()
                val exp = app.transactionRepo.getTotalExpense(mStart, mEnd).toFloat()
                BarEntry(income = inc, expense = exp, label = monthAbbr(month.monthValue))
            }

            // 30-day heatmap (always last 30 days)
            val daily = app.transactionRepo.getDailyExpense(now.minusDays(29), now)
            val dailyMap = daily.associateBy { it.dateEpochDay }
            val maxDaily = daily.maxOfOrNull { it.total }?.coerceAtLeast(1.0) ?: 1.0
            val heatmap = (29 downTo 0).map { daysAgo ->
                val day = now.minusDays(daysAgo.toLong())
                ((dailyMap[day.toEpochDay()]?.total ?: 0.0) / maxDaily).toFloat()
            }

            _state.value = _state.value.copy(
                periodLabel = label,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                donutSegments = donutSegments,
                categoryBreakdown = breakdown,
                incomeDonutSegments = incomeSegments,
                incomeCategoryBreakdown = incomeBreakdown,
                monthlyBars = monthBars,
                heatmapValues = heatmap,
                isLoading = false
            )
        }
    }

    private fun getPeriodRange(now: LocalDate, period: StatsPeriod, offset: Int): Pair<LocalDate, LocalDate> = when (period) {
        StatsPeriod.DAY -> {
            val day = now.plusDays(offset.toLong())
            Pair(day, day)
        }
        StatsPeriod.WEEK -> {
            val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(offset.toLong())
            Pair(monday, monday.plusDays(6))
        }
        StatsPeriod.MONTH -> {
            val month = now.plusMonths(offset.toLong())
            Pair(month.withDayOfMonth(1), month.withDayOfMonth(month.lengthOfMonth()))
        }
        StatsPeriod.YEAR -> {
            val year = now.plusYears(offset.toLong())
            Pair(year.withDayOfYear(1), year.withDayOfYear(year.lengthOfYear()))
        }
    }

    private fun getPeriodLabel(now: LocalDate, period: StatsPeriod, offset: Int): String = when (period) {
        StatsPeriod.DAY -> {
            val day = now.plusDays(offset.toLong())
            "${day.dayOfMonth} ${monthAbbr(day.monthValue)} ${day.year}"
        }
        StatsPeriod.WEEK -> {
            val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(offset.toLong())
            val sunday = monday.plusDays(6)
            "${monday.dayOfMonth} ${monthAbbr(monday.monthValue)} — ${sunday.dayOfMonth} ${monthAbbr(sunday.monthValue)} ${sunday.year}"
        }
        StatsPeriod.MONTH -> {
            val month = now.plusMonths(offset.toLong())
            "${monthAbbr(month.monthValue)} ${month.year}"
        }
        StatsPeriod.YEAR -> now.plusYears(offset.toLong()).year.toString()
    }

    private fun monthAbbr(m: Int) = when (m) {
        1 -> "ЯНВ"
        2 -> "ФЕВ"
        3 -> "МАР"
        4 -> "АПР"
        5 -> "МАЙ"
        6 -> "ИЮН"
        7 -> "ИЮЛ"
        8 -> "АВГ"
        9 -> "СЕН"
        10 -> "ОКТ"
        11 -> "НОЯ"
        else -> "ДЕК"
    }
}
