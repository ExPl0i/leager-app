package com.ledger.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledger.app.ui.theme.IbmPlexMonoFamily
import com.ledger.app.ui.theme.ledger

data class BarEntry(val income: Float, val expense: Float, val label: String)

@Composable
fun StackedBarChart(
    entries: List<BarEntry>,
    modifier: Modifier = Modifier,
    barHeightDp: Float = 110f,
    highlightLast: Boolean = true
) {
    val c = MaterialTheme.ledger
    if (entries.isEmpty()) return

    // Scale by combined sum so stacked bars always fit within canvas height
    val maxVal = entries.maxOf { it.income + it.expense }.coerceAtLeast(1f)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeightDp.dp)
        ) {
            val barWidth = (size.width - entries.size * 4.dp.toPx()) / entries.size
            val barH = size.height

            entries.forEachIndexed { i, entry ->
                val x = i * (barWidth + 4.dp.toPx())
                val isLast = i == entries.lastIndex
                val alphaIn  = if (isLast || !highlightLast) 1f else 0.6f
                val alphaOut = if (isLast || !highlightLast) 1f else 0.5f

                val incH  = (entry.income  / maxVal) * barH * 0.92f
                val expH  = (entry.expense / maxVal) * barH * 0.92f

                // Income bar (top)
                drawRect(
                    color = c.lime.copy(alpha = alphaIn),
                    topLeft = Offset(x, barH - incH - expH),
                    size = Size(barWidth, incH)
                )
                // Expense bar (below)
                drawRect(
                    color = c.red.copy(alpha = alphaOut),
                    topLeft = Offset(x, barH - expH),
                    size = Size(barWidth, expH)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            entries.forEachIndexed { i, entry ->
                val isLast = i == entries.lastIndex
                Text(
                    text = entry.label.take(1),
                    modifier = Modifier.weight(1f),
                    fontFamily = IbmPlexMonoFamily,
                    fontSize = 9.sp,
                    letterSpacing = 0.6.sp,
                    color = if (isLast) c.text else c.faint
                )
            }
        }
    }
}
