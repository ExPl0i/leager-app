package com.ledger.app.ui.screen.accounts

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
import com.ledger.app.domain.model.Account
import com.ledger.app.ui.components.AccountDialog
import com.ledger.app.ui.components.parseHexColor
import com.ledger.app.ui.theme.IbmPlexMonoFamily
import com.ledger.app.ui.theme.IbmPlexSansFamily
import com.ledger.app.ui.theme.ledger
import com.ledger.app.util.formatMoney

@Composable
fun AccountsScreen(
    app: LedgerApplication,
    onBackClick: () -> Unit
) {
    val vm: AccountsViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val c = MaterialTheme.ledger

    if (state.showDialog) {
        AccountDialog(
            editTarget = state.editTarget,
            onDismiss = vm::dismissDialog,
            onSave = { name, type, currency, color, includeInTotal, balance ->
                vm.saveAccount(name, type, currency, color, includeInTotal, balance)
            },
            onArchive = state.editTarget?.let { acc -> { vm.archiveAccount(acc) } }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(c.bg)) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 16.dp)
                    .drawBehind {
                        drawLine(c.border,
                            androidx.compose.ui.geometry.Offset(0f, size.height),
                            androidx.compose.ui.geometry.Offset(size.width, size.height),
                            1.dp.toPx())
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("${state.accounts.size} СЧЕТОВ",
                        fontFamily = IbmPlexMonoFamily, fontSize = 10.sp,
                        letterSpacing = 1.2.sp, color = c.faint)
                    Text(state.netWorth.formatMoney(),
                        fontFamily = IbmPlexMonoFamily, fontWeight = FontWeight.Medium,
                        fontSize = 30.sp, letterSpacing = (-1).sp, lineHeight = 34.sp,
                        color = c.text, modifier = Modifier.padding(top = 6.dp))
                    Text("ИТОГО · ₽ ЭКВИВАЛЕНТ",
                        fontFamily = IbmPlexMonoFamily, fontSize = 11.sp,
                        letterSpacing = 1.0.sp, color = c.muted)
                }
                Box(
                    modifier = Modifier.size(36.dp).border(1.dp, c.text).clickable { vm.openCreate() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontFamily = IbmPlexMonoFamily, fontSize = 20.sp, color = c.text)
                }
            }
        }

        items(state.accounts) { account ->
            AccountRow(account = account, totalRub = state.netWorth, onClick = { vm.openEdit(account) })
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text("ЗА МЕСЯЦ", fontFamily = IbmPlexMonoFamily, fontSize = 10.sp,
                    letterSpacing = 1.4.sp, color = c.faint)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatBox("ПОСТУПИЛО", "+${state.monthIncome.formatMoney()}", c.lime, Modifier.weight(1f))
                    StatBox("ПОТРАЧЕНО", "−${state.monthExpense.formatMoney()}", c.red, Modifier.weight(1f))
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun AccountRow(account: Account, totalRub: Double, onClick: () -> Unit) {
    val c = MaterialTheme.ledger
    val color = parseHexColor(account.color)
    val rate = when (account.currency) { "USD" -> 92.0; "EUR" -> 99.0; else -> 1.0 }
    val pct = if (totalRub > 0 && account.includeInTotal) (account.balance * rate) / totalRub else 0.0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .drawBehind {
                drawLine(c.border,
                    androidx.compose.ui.geometry.Offset(0f, size.height),
                    androidx.compose.ui.geometry.Offset(size.width, size.height),
                    1.dp.toPx())
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(8.dp).height(56.dp).background(color))
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text(account.name, fontFamily = IbmPlexSansFamily,
                    fontWeight = FontWeight.Medium, fontSize = 16.sp, color = c.text)
                Text(account.balance.formatMoney(account.currency), fontFamily = IbmPlexMonoFamily,
                    fontWeight = FontWeight.Medium, fontSize = 18.sp, color = c.text)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                val suffix = if (!account.includeInTotal) " · НЕ В ИТОГЕ" else ""
                Text("${account.type.label.uppercase()}${account.last4?.let { " · ****$it" } ?: ""} · ${account.currency}$suffix",
                    fontFamily = IbmPlexMonoFamily, fontSize = 10.sp, letterSpacing = 1.0.sp, color = c.muted)
                Text("${"%.1f".format(pct * 100)}%",
                    fontFamily = IbmPlexMonoFamily, fontSize = 10.sp, color = c.faint)
            }
            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(c.border).padding(top = 8.dp)) {
                Box(modifier = Modifier.fillMaxWidth(pct.toFloat().coerceIn(0f, 1f)).fillMaxHeight().background(color))
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    val c = MaterialTheme.ledger
    Column(modifier = modifier.border(1.dp, c.border).padding(12.dp)) {
        Text(label, fontFamily = IbmPlexMonoFamily, fontSize = 9.sp, letterSpacing = 1.2.sp, color = c.faint)
        Text(value, fontFamily = IbmPlexMonoFamily, fontWeight = FontWeight.Medium,
            fontSize = 16.sp, color = valueColor, modifier = Modifier.padding(top = 6.dp))
    }
}
