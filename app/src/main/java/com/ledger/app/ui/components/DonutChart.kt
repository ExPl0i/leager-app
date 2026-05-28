package com.ledger.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DonutSegment(
    val value: Float,
    val color: Color,
    val label: String = "",
    val id: String = ""
)

@Composable
fun DonutChart(
    segments: List<DonutSegment>,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    thickness: Dp = 28.dp,
    trackColor: Color = Color(0xFF1C1C1C),
    gap: Float = 2f,
    center: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = thickness.toPx()
            val radius = (this.size.minDimension / 2f) - stroke / 2f
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val topLeft = Offset(cx - radius, cy - radius)
            val arcSize = Size(radius * 2f, radius * 2f)

            // Track
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Butt)
            )

            if (segments.isEmpty()) return@Canvas
            val total = segments.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(0.001f)

            var startAngle = -90f
            segments.forEach { seg ->
                val sweep = (seg.value / total) * 360f
                val gapHalf = if (segments.size > 1) gap / 2f else 0f
                val adjStart = startAngle + gapHalf
                val adjSweep = (sweep - gap).coerceAtLeast(0f)
                if (adjSweep > 0f) {
                    drawArc(
                        color = seg.color,
                        startAngle = adjStart,
                        sweepAngle = adjSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Butt)
                    )
                }
                startAngle += sweep
            }
        }
        center()
    }
}
