package com.ledger.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun SparklineChart(
    data: List<Float>,
    color: Color,
    modifier: Modifier = Modifier,
    areaAlpha: Float = 0.18f
) {
    if (data.size < 2) return

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val min = data.min()
        val max = data.max()
        val range = (max - min).coerceAtLeast(0.001f)

        val points = data.mapIndexed { i, v ->
            Offset(
                x = (i.toFloat() / (data.size - 1)) * w,
                y = h - ((v - min) / range) * (h - 2.dp.toPx()) - 1.dp.toPx()
            )
        }

        // Area path
        val areaPath = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(areaPath, color = color.copy(alpha = areaAlpha))

        // Line path
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(
            linePath,
            color = color,
            style = Stroke(
                width = 1.5.dp.toPx(),
                cap = StrokeCap.Square,
                join = StrokeJoin.Miter
            )
        )
    }
}
