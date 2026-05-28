package com.ledger.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.ledger.app.ui.theme.ledger

@Composable
fun HeatmapGrid(
    values: List<Float>,
    columns: Int = 15,
    modifier: Modifier = Modifier
) {
    val c = MaterialTheme.ledger
    val rows = (values.size + columns - 1) / columns

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(rows) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(columns) { col ->
                    val idx = row * columns + col
                    val v = if (idx < values.size) values[idx] else 0f
                    val alpha = when {
                        v == 0f  -> 0.08f
                        v < 0.2f -> 0.08f
                        v < 0.4f -> 0.25f
                        v < 0.6f -> 0.5f
                        v < 0.8f -> 0.75f
                        else     -> 1f
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .alpha(alpha)
                            .background(if (v == 0f) c.surface2 else c.lime)
                    )
                }
            }
        }
    }
}
