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
import java.time.LocalDate

enum class StatsPeriod(val label: String) { DAY("DAY"), WEEK("WEEK"), MONTH("MONTH"), YEAR("YEAR") }

data class CategoryBreakdown(val category: Category, val spent: Double, val pct: Float)

data class StatsState(
    val period: StatsPeriod = StatsPeriod.MONTH,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val donutSegments: List<DonutSegment> = emptyList(),
    val categoryBreakdown: List<CategoryBreakdown> = emptyList(),
    val monthlyBars: List<BarEntry> = emptyList(),
    val heatmapValues: List<Float> = emptyList(),
    val isLoading: Boolean = true
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as LedgerApplication
    private val _state = MutableStateFlow(StatsState())
    val state: StateFlow<StatsState> = _state.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            val categories = app.categoryRepo.getAll().first()
            val catMap = categories.associateBy { it.id }

            // Current period
            val now = LocalDate.now()
            val (from, to) = getPeriodRange(now, _state.value.period)

            val totalIncome  = app.transactionRepo.getTotalIncome(from, to)
            val totalExpense = app.transactionRepo.getTotalExpense(from, to)
            val spentByCategory = app.transactionRepo.getExpenseByCategory(from, to)

            val donutSegments = spentByCategory
                .filter { it.total > 0 }
                .sortedByDescending { it.total }
                .mapNotNull { ct ->
                    val cat = catMap[ct.categoryId] ?: return@mapNotNull null
                    DonutSegment(
                        value = ct.total.toFloat(),
                        color = parseHexColor(cat.color),
                        label = cat.name,
                        id = cat.id
                    )
                }

            val breakdown = spentByCategory
                .filter { it.total > 0 }
                .sortedByDescending { it.total }
                .mapNotNull { ct ->
                    val cat = catMap[ct.categoryId] ?: return@mapNotNull null
                    CategoryBreakdown(
                        category = cat,
                        spent = ct.total,
                        pct = if (totalExpense > 0) (ct.total / totalExpense).toFloat() else 0f
                    )
                }

            // 12-month bars (simplified)
            val monthBars = (11 downTo 0).map { monthsAgo ->
                val month = now.minusMonths(monthsAgo.toLong())
                val mStart = month.withDayOfMonth(1)
                val mEnd = month.withDayOfMonth(month.lengthOfMonth())
                val inc = app.transactionRepo.getTotalIncome(mStart, mEnd).toFloat()
                val exp = app.transactionRepo.getTotalExpense(mStart, mEnd).toFloat()
                val label = when (month.monthValue) {
                    1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"; 5 -> "May"; 6 -> "Jun"
                    7 -> "Jul"; 8 -> "Aug"; 9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; else -> "Dec"
                }
                BarEntry(income = inc, expense = exp, label = label)
            }

            // 30-day heatmap
            val daily = app.transactionRepo.getDailyExpense(now.minusDays(29), now)
            val dailyMap = daily.associateBy { it.dateEpochDay }
            val maxDaily = daily.maxOfOrNull { it.total }?.coerceAtLeast(1.0) ?: 1.0
            val heatmap = (29 downTo 0).map { daysAgo ->
                val day = now.minusDays(daysAgo.toLong())
                val v = dailyMap[day.toEpochDay()]?.total ?: 0.0
                (v / maxDaily).toFloat()
            }

            _state.value = _state.value.copy(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                donutSegments = donutSegments,
                categoryBreakdown = breakdown,
                monthlyBars = monthBars,
                heatmapValues = heatmap,
                isLoading = false
            )
        }
    }

    fun setPeriod(period: StatsPeriod) {
        _state.value = _state.value.copy(period = period, isLoading = true)
        load()
    }

    private fun getPeriodRange(now: LocalDate, period: StatsPeriod): Pair<LocalDate, LocalDate> = when (period) {
        StatsPeriod.DAY   -> Pair(now, now)
        StatsPeriod.WEEK  -> Pair(now.minusDays(6), now)
        StatsPeriod.MONTH -> Pair(now.withDayOfMonth(1), now)
        StatsPeriod.YEAR  -> Pair(now.withDayOfYear(1), now)
    }
}
