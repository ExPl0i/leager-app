package com.ledger.app.ui.screen.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ledger.app.LedgerApplication
import com.ledger.app.ui.components.*
import com.ledger.app.ui.theme.IbmPlexMonoFamily
import com.ledger.app.ui.theme.IbmPlexSansFamily
import com.ledger.app.ui.theme.ledger
import com.ledger.app.util.formatMoney
import com.ledger.app.util.formatSigned
import java.time.LocalDate

@Composable
fun StatsScreen(
    app: LedgerApplication,
    onBackClick: () -> Unit
) {
    val vm: StatsViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val c = MaterialTheme.ledger
    val now = LocalDate.now()

    LazyColumn(modifier = Modifier.fillMaxSize().background(c.bg)) {

        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("STATS · ${now.month.name.take(3).uppercase()} ${now.year}",
                        fontFamily = IbmPlexMonoFamily, fontSize = 10.sp, letterSpacing = 1.2.sp, color = c.faint)
                    Text("${now.withDayOfMonth(1).dayOfMonth}—${now.dayOfMonth} ${now.month.name.take(3).uppercase()} · ${now.dayOfMonth} ДНЕЙ",
                        fontFamily = IbmPlexMonoFamily, fontSize = 14.sp, color = c.muted, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }

        // Period tabs
        item {
            Row(modifier = Modifier.fillMaxWidth().drawBehind {
                drawLine(c.border, androidx.compose.ui.geometry.Offset(0f, size.height), androidx.compose.ui.geometry.Offset(size.width, size.height), 1.dp.toPx())
            }) {
                StatsPeriod.entries.forEachIndexed { idx, period ->
                    val isSelected = state.period == period
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isSelected) c.surface else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { vm.setPeriod(period) }
                            .padding(vertical = 12.dp)
                            .drawBehind {
                                if (isSelected) drawLine(c.lime,
                                    androidx.compose.ui.geometry.Offset(0f, size.height),
                                    androidx.compose.ui.geometry.Offset(size.width, size.height), 2.dp.toPx())
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(period.label, fontFamily = IbmPlexMonoFamily, fontSize = 11.sp,
                            letterSpacing = 1.4.sp, color = if (isSelected) c.text else c.muted)
                    }
                }
            }
        }

        // Donut + breakdown
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp)) {
                DonutChart(
                    segments = state.donutSegments,
                    size = 156.dp,
                    thickness = 22.dp,
                    trackColor = c.surface2,
                    center = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SPENT", fontFamily = IbmPlexMonoFamily, fontSize = 9.sp, letterSpacing = 1.2.sp, color = c.faint)
                            Text(state.totalExpense.formatMoney(), fontFamily = IbmPlexMonoFamily,
                                fontWeight = FontWeight.Medium, fontSize = 20.sp, letterSpacing = (-0.5).sp, color = c.text)
                        }
                    }
                )
                Spacer(Modifier.width(18.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("BREAKDOWN", fontFamily = IbmPlexMonoFamily, fontSize = 10.sp, letterSpacing = 1.4.sp, color = c.faint)
                    Spacer(Modifier.height(8.dp))
                    state.categoryBreakdown.take(5).forEach { breakdown ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(parseHexColor(breakdown.category.color)))
                            Spacer(Modifier.width(8.dp))
                            Text(breakdown.category.name, fontFamily = IbmPlexSansFamily, fontSize = 12.sp, color = c.text, modifier = Modifier.weight(1f))
                            Text("${(breakdown.pct * 100).toInt()}%", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp, color = c.muted)
                        }
                    }
                    if (state.categoryBreakdown.size > 5) {
                        Text("+${state.categoryBreakdown.size - 5} more", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp, color = c.faint, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        // KPI rings
        item {
            val incomeTarget = state.totalIncome.coerceAtLeast(1.0)
            val budgetTotal = state.categoryBreakdown.sumOf { it.category.budget ?: 0.0 }.coerceAtLeast(1.0)
            val net = state.totalIncome - state.totalExpense
            val netRatio = if (incomeTarget > 0) (net / incomeTarget).coerceIn(-1.0, 1.0) else 0.0
            Row(modifier = Modifier.fillMaxWidth()
                .drawBehind {
                    drawLine(c.border, androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Offset(size.width, 0f), 1.dp.toPx())
                    drawLine(c.border, androidx.compose.ui.geometry.Offset(0f, size.height), androidx.compose.ui.geometry.Offset(size.width, size.height), 1.dp.toPx())
                }
            ) {
                listOf(
                    Triple(state.totalIncome / incomeTarget, "INCOME", state.totalIncome.formatMoney()),
                    Triple(state.totalExpense / budgetTotal, "EXPENSE", "−${state.totalExpense.formatMoney()}"),
                    Triple(netRatio, "NET", net.formatSigned())
                ).forEachIndexed { idx, (ratio, kpiLabel, amt) ->
                    Column(
                        modifier = Modifier.weight(1f).padding(12.dp, 16.dp, 12.dp, 18.dp)
                            .drawBehind {
                                if (idx < 2) drawLine(c.border, androidx.compose.ui.geometry.Offset(size.width, 0f), androidx.compose.ui.geometry.Offset(size.width, size.height), 1.dp.toPx())
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RadialRing(
                            value = ratio.toFloat().coerceIn(0f, 1f),
                            size = 54.dp,
                            thickness = 3.dp,
                            color = if (ratio > 0.9 && kpiLabel == "EXPENSE") c.red else c.lime,
                            label = "${(ratio * 100).toInt()}%",
                            sublabel = when (kpiLabel) { "INCOME" -> "OF GOAL"; "EXPENSE" -> "BUDGET"; else -> "DAYS" },
                            labelSize = 12.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(kpiLabel, fontFamily = IbmPlexMonoFamily, fontSize = 9.sp, letterSpacing = 1.2.sp, color = c.faint)
                        Text(amt, fontFamily = IbmPlexMonoFamily, fontWeight = FontWeight.Medium, fontSize = 13.sp,
                            color = if (kpiLabel == "EXPENSE") c.text else c.lime, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        // Monthly trend
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("TREND · 12 MONTHS", fontFamily = IbmPlexMonoFamily, fontSize = 10.sp, letterSpacing = 1.2.sp, color = c.faint)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        LegendItem("IN", c.lime)
                        LegendItem("OUT", c.red)
                    }
                }
                Spacer(Modifier.height(14.dp))
                StackedBarChart(entries = state.monthlyBars, modifier = Modifier.fillMaxWidth())
            }
        }

        // Heatmap
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("DAILY ACTIVITY · 30D", fontFamily = IbmPlexMonoFamily, fontSize = 10.sp, letterSpacing = 1.2.sp, color = c.faint)
                }
                HeatmapGrid(values = state.heatmapValues, columns = 15, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("LESS", fontFamily = IbmPlexMonoFamily, fontSize = 9.sp, color = c.faint)
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        listOf(0.08f, 0.25f, 0.5f, 0.75f, 1f).forEach { opacity ->
                            Box(modifier = Modifier.size(10.dp).background(c.lime.copy(alpha = opacity)))
                        }
                    }
                    Text("MORE", fontFamily = IbmPlexMonoFamily, fontSize = 9.sp, color = c.faint)
                }
            }
        }

        // Category list
        item {
            Text("CATEGORIES · ${LocalDate.now().month.name.take(3).uppercase()}",
                fontFamily = IbmPlexMonoFamily, fontSize = 10.sp, letterSpacing = 1.4.sp, color = c.faint,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
        }
        items(state.categoryBreakdown.take(6)) { item ->
            val cat = item.category
            val budget = cat.budget
            val pct = if (budget != null && budget > 0) (item.spent / budget).toFloat() else 0f
            val over = pct > 1f
            val color = parseHexColor(cat.color)

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp).drawBehind {
                drawLine(c.border, androidx.compose.ui.geometry.Offset(0f, size.height), androidx.compose.ui.geometry.Offset(size.width, size.height), 1.dp.toPx())
            }, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(6.dp).height(32.dp).background(color))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(cat.name, fontFamily = IbmPlexSansFamily, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = c.text)
                        Text("−${item.spent.formatMoney()}", fontFamily = IbmPlexMonoFamily, fontSize = 12.sp, color = if (over) c.red else c.text)
                    }
                    if (budget != null) {
                        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(c.surface2).padding(top = 6.dp)) {
                            Box(modifier = Modifier.fillMaxWidth(pct.coerceIn(0f, 1f)).fillMaxHeight().background(if (over) c.red else color))
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${(pct * 100).toInt()}% of ${budget.formatMoney()}", fontFamily = IbmPlexMonoFamily, fontSize = 9.sp, letterSpacing = 0.6.sp, color = c.faint)
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun LegendItem(label: String, color: androidx.compose.ui.graphics.Color) {
    val c = MaterialTheme.ledger
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(modifier = Modifier.size(8.dp).background(color))
        Text(label, fontFamily = IbmPlexMonoFamily, fontSize = 9.sp, color = c.muted, letterSpacing = 0.8.sp)
    }
}

