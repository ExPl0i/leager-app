package com.ledger.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledger.app.domain.model.Category
import com.ledger.app.domain.model.Transaction
import com.ledger.app.domain.model.TransactionType
import com.ledger.app.ui.theme.IbmPlexMonoFamily
import com.ledger.app.ui.theme.IbmPlexSansFamily
import com.ledger.app.ui.theme.ledger
import com.ledger.app.util.formatHm
import com.ledger.app.util.formatMoney

@Composable
fun TransactionRow(
    transaction: Transaction,
    category: Category?,
    accountName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    val c = MaterialTheme.ledger
    val catColor = category?.color?.let { parseHexColor(it) } ?: c.muted
    val isIncome = transaction.type == TransactionType.INCOME
    val amountColor = if (isIncome) c.lime else c.text

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color tick
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(28.dp)
                    .background(catColor)
            )

            Spacer(Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.note.ifBlank { category?.name ?: "—" },
                    fontFamily = IbmPlexSansFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = c.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${transaction.time.formatHm()} · ${category?.name?.uppercase() ?: "—"} · ${accountName.uppercase()}",
                    fontFamily = IbmPlexMonoFamily,
                    fontSize = 10.sp,
                    letterSpacing = 0.4.sp,
                    color = c.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))

            // Amount
            val prefix = if (isIncome) "+" else "−"
            Text(
                text = "$prefix${Math.abs(transaction.amount).formatMoney()}",
                fontFamily = IbmPlexMonoFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = amountColor
            )
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(start = 36.dp)
                    .background(c.border)
            )
        }
    }
}

fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.Gray
    }
}
