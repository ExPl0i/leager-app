package com.ledger.app.ui.screen.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ledger.app.LedgerApplication
import com.ledger.app.domain.model.Transaction
import com.ledger.app.ui.components.SectionHeader
import com.ledger.app.ui.components.TransactionRow
import com.ledger.app.ui.theme.IbmPlexMonoFamily
import com.ledger.app.ui.theme.ledger
import com.ledger.app.util.formatShort
import com.ledger.app.util.isToday
import com.ledger.app.util.isYesterday

@Composable
fun TransactionsScreen(
    app: LedgerApplication,
    onTransactionClick: (String) -> Unit,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val vm: TransactionsViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val c = MaterialTheme.ledger

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.bg)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "← BACK",
                fontFamily = IbmPlexMonoFamily,
                fontSize = 12.sp,
                letterSpacing = 0.6.sp,
                color = c.muted,
                modifier = Modifier
                    .clickable(onClick = onBackClick)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "OPERATIONS",
                fontFamily = IbmPlexMonoFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                letterSpacing = 1.6.sp,
                color = c.text
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "+ NEW",
                fontFamily = IbmPlexMonoFamily,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp,
                color = c.lime,
                modifier = Modifier.clickable(onClick = onAddClick)
            )
        }

        // Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .background(c.surface)
                .border(1.dp, c.border)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⌕", fontFamily = IbmPlexMonoFamily, fontSize = 14.sp, color = c.muted)
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = state.searchQuery,
                onValueChange = vm::onSearch,
                textStyle = TextStyle(
                    fontFamily = IbmPlexMonoFamily,
                    fontSize = 13.sp,
                    color = c.text
                ),
                decorationBox = { inner ->
                    if (state.searchQuery.isEmpty()) {
                        Text(
                            "искать операцию…",
                            fontFamily = IbmPlexMonoFamily,
                            fontSize = 13.sp,
                            color = c.muted
                        )
                    }
                    inner()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(8.dp))

        // Transaction list grouped by date
        val grouped = state.filteredTransactions.groupBy { it.date }
        LazyColumn {
            grouped.entries.sortedByDescending { it.key }.forEach { (date, txns) ->
                item(key = date.toString()) {
                    val label = when {
                        date.isToday()     -> "TODAY · ${date.formatShort().uppercase()}"
                        date.isYesterday() -> "YESTERDAY · ${date.formatShort().uppercase()}"
                        else               -> date.formatShort().uppercase()
                    }
                    Text(
                        text = label,
                        fontFamily = IbmPlexMonoFamily,
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp,
                        color = c.faint,
                        modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp)
                    )
                }
                itemsIndexed(txns, key = { _, tx -> tx.id }) { idx, tx ->
                    TransactionRow(
                        transaction = tx,
                        category = state.categories[tx.categoryId],
                        accountName = state.accountNames[tx.accountId] ?: "",
                        onClick = { onTransactionClick(tx.id) },
                        showDivider = idx < txns.lastIndex
                    )
                }
            }

            if (state.filteredTransactions.isEmpty() && !state.isLoading) {
                item {
                    EmptyState()
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun EmptyState() {
    val c = MaterialTheme.ledger
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .border(1.dp, c.muted, androidx.compose.ui.graphics.RectangleShape)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "NO OPERATIONS",
            fontFamily = IbmPlexMonoFamily,
            fontSize = 10.sp,
            letterSpacing = 1.2.sp,
            color = c.muted
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Журнал пуст. Нажмите +.",
            fontFamily = com.ledger.app.ui.theme.IbmPlexSansFamily,
            fontSize = 13.sp,
            color = c.faint
        )
    }
}

