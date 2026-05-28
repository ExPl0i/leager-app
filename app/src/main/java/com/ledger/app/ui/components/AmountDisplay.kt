package com.ledger.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledger.app.ui.theme.IbmPlexMonoFamily
import com.ledger.app.ui.theme.ledger

@Composable
fun BigAmountDisplay(
    amountText: String,
    onAmountChange: (String) -> Unit,
    isExpense: Boolean,
    modifier: Modifier = Modifier
) {
    val c = MaterialTheme.ledger

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = if (isExpense) "−" else "+",
                fontFamily = IbmPlexMonoFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 40.sp,
                color = c.text,
                lineHeight = 40.sp,
                modifier = Modifier.alignBy { it.measuredHeight }
            )

            BasicTextField(
                value = amountText,
                onValueChange = { raw ->
                    val filtered = raw.filter { it.isDigit() || it == '.' }
                    val dotCount = filtered.count { it == '.' }
                    if (dotCount <= 1) onAmountChange(filtered)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = TextStyle(
                    fontFamily = IbmPlexMonoFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 56.sp,
                    letterSpacing = (-2).sp,
                    color = c.lime
                ),
                singleLine = true,
                modifier = Modifier
                    .widthIn(min = 80.dp)
                    .wrapContentWidth()
            )
        }

        Spacer(Modifier.height(4.dp))
        val borderColor = c.borderStrong
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(1.dp)
                .drawBehind {
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, size.height / 2f),
                        end = Offset(size.width, size.height / 2f),
                        strokeWidth = size.height
                    )
                }
        )
    }
}
