package com.ledger.app.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ledger.app.domain.model.Account
import com.ledger.app.ui.components.AccountDialog
import com.ledger.app.ui.components.SectionHeader
import com.ledger.app.ui.components.TransactionRow
import com.ledger.app.ui.components.parseHexColor
import com.ledger.app.ui.theme.IbmPlexMonoFamily
import com.ledger.app.ui.theme.ledger
import com.ledger.app.util.formatFull
import com.ledger.app.util.formatMoney
import com.ledger.app.util.formatShort
import com.ledger.app.util.isToday
import com.ledger.app.util.isYesterday
import java.time.LocalDate

@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onTransactionClick: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    onMenuClick: () -> Unit = {}
) {
    val vm: HomeViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val c = MaterialTheme.ledger

    if (state.showAccountDialog) {
        AccountDialog(
            editTarget = state.editingAccount,
            onDismiss = vm::dismissAccountDialog,
            onSave = { name, type, currency, color, includeInTotal, balance ->
                vm.saveAccount(name, type, currency, color, includeInTotal, balance)
            },
            onArchive = state.editingAccount?.let { acc -> { vm.archiveAccount(acc) } }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(c.bg)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = LocalDate.now().formatFull(),
                        fontFamily = IbmPlexMonoFamily, fontSize = 13.sp,
                        letterSpacing = 1.0.sp, color = c.faint
                    )
                    Text(
                        text = "LEDGER",
                        fontFamily = IbmPlexMonoFamily, fontWeight = FontWeight.Medium,
                        fontSize = 28.sp, letterSpacing = 2.sp, color = c.text,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(c.surface)
                        .border(1.dp, c.border)
                        .clickable(onClick = onMenuClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "≡", fontFamily = IbmPlexMonoFamily, fontSize = 14.sp, color = c.muted)
                }
            }
        }

        // Net worth
        item {
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 16.dp)) {
                val currencySymbol = when (state.netWorthCurrency) { "USD" -> "$"; "EUR" -> "€"; else -> "₽" }
                Text(
                    text = "NET WORTH · $currencySymbol EQUIVALENT",
                    fontFamily = IbmPlexMonoFamily, fontSize = 10.sp,
                    letterSpacing = 1.4.sp, color = c.faint
                )
                Text(
                    text = state.netWorth.formatMoney(state.netWorthCurrency),
                    fontFamily = IbmPlexMonoFamily, fontWeight = FontWeight.Medium,
                    fontSize = 44.sp, letterSpacing = (-1.5).sp, lineHeight = 48.sp, color = c.text,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Account chips (clickable) + "+" chip at the end
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.accounts.forEach { account ->
                    AccountChip(account = account, onClick = { vm.openAccountEdit(account) })
                }
                AddAccountChip(onClick = { vm.openAccountCreate() })
            }
        }

        // Today summary + net worth
        item {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .fillMaxWidth()
                    .background(c.surface)
                    .border(1.dp, c.border)
            ) {
                SummaryCell("ДОХОД", "+${state.todayIncome.formatMoney()}", c.lime, Modifier.weight(1f))
                Box(Modifier.width(1.dp).height(56.dp).background(c.border))
                SummaryCell("РАСХОД", "−${state.todayExpense.formatMoney()}", c.red, Modifier.weight(1f))
                Box(Modifier.width(1.dp).height(56.dp).background(c.border))
                SummaryCell("ОПС", "${state.todayOpsCount}", c.text, Modifier.weight(1f))
                Box(Modifier.width(1.dp).height(56.dp).background(c.border))
                SummaryCell("ИТОГО", state.netWorth.formatMoney(), c.text, Modifier.weight(1f))
            }
        }

        // Recent ops header
        item {
            SectionHeader(
                label = "RECENT OPERATIONS",
                action = {
                    Text(
                        text = "ALL →",
                        fontFamily = IbmPlexMonoFamily, fontSize = 10.sp, letterSpacing = 1.2.sp,
                        color = c.lime, modifier = Modifier.clickable(onClick = onSeeAllClick)
                    )
                }
            )
        }

        // Grouped transactions
        val grouped = state.recentTransactions.groupBy { it.date }
        grouped.entries.sortedByDescending { it.key }.forEach { (date, txns) ->
            item(key = date.toString()) {
                val label = when {
                    date.isToday()     -> "TODAY · ${date.formatShort().uppercase()}"
                    date.isYesterday() -> "YESTERDAY · ${date.formatShort().uppercase()}"
                    else               -> date.formatShort().uppercase()
                }
                Text(
                    text = label,
                    fontFamily = IbmPlexMonoFamily, fontSize = 10.sp, letterSpacing = 1.2.sp,
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

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun AccountChip(account: Account, onClick: () -> Unit) {
    val c = MaterialTheme.ledger
    val color = parseHexColor(account.color)

    Column(
        modifier = Modifier
            .width(132.dp)
            .background(c.surface)
            .border(1.dp, c.border)
            .clickable(onClick = onClick)
            .padding(12.dp, 12.dp, 12.dp, 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(6.dp).background(color))
            Text(text = account.currency, fontFamily = IbmPlexMonoFamily, fontSize = 10.sp, color = c.faint)
        }
        Spacer(Modifier.height(18.dp))
        Text(text = account.name, fontFamily = IbmPlexMonoFamily, fontSize = 11.sp, color = c.muted)
        Spacer(Modifier.height(4.dp))
        Text(
            text = account.balance.formatMoney(account.currency),
            fontFamily = IbmPlexMonoFamily, fontWeight = FontWeight.Medium,
            fontSize = 17.sp, letterSpacing = (-0.5).sp, color = c.text
        )
    }
}

@Composable
private fun AddAccountChip(onClick: () -> Unit) {
    val c = MaterialTheme.ledger
    Column(
        modifier = Modifier
            .width(72.dp)
            .height(96.dp)
            .background(c.surface)
            .border(1.dp, c.border)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("+", fontFamily = IbmPlexMonoFamily, fontSize = 22.sp, color = c.muted)
        Spacer(Modifier.height(4.dp))
        Text("СЧЁТ", fontFamily = IbmPlexMonoFamily, fontSize = 8.sp,
            letterSpacing = 1.sp, color = c.faint)
    }
}

@Composable
private fun SummaryCell(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val c = MaterialTheme.ledger
    Column(modifier = modifier.padding(horizontal = 8.dp, vertical = 10.dp)) {
        Text(text = label, fontFamily = IbmPlexMonoFamily, fontSize = 8.sp,
            letterSpacing = 0.8.sp, color = c.faint)
        Text(
            text = value, fontFamily = IbmPlexMonoFamily, fontWeight = FontWeight.Medium,
            fontSize = 13.sp, letterSpacing = (-0.3).sp, color = valueColor,
            modifier = Modifier.padding(top = 4.dp), maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Clip
        )
    }
}
