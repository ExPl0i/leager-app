package com.ledger.app.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ledger.app.LedgerApplication
import com.ledger.app.domain.model.TransactionType
import com.ledger.app.ui.components.parseHexColor
import com.ledger.app.ui.theme.IbmPlexMonoFamily
import com.ledger.app.ui.theme.IbmPlexSansFamily
import com.ledger.app.ui.theme.ledger
import com.ledger.app.util.formatMedium
import com.ledger.app.util.formatHm
import com.ledger.app.util.formatMoney

@Composable
fun TransactionDetailScreen(
    app: LedgerApplication,
    transactionId: String,
    onEdit: () -> Unit,
    onBackClick: () -> Unit
) {
    val vm: TransactionDetailViewModel = viewModel(
        factory = TransactionDetailViewModel.Factory(app, transactionId)
    )
    val state by vm.state.collectAsState()
    val c = MaterialTheme.ledger
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.deleted) {
        if (state.deleted) onBackClick()
    }

    val tx = state.transaction ?: return
    val cat = state.category
    val acc = state.account
    val catColor = cat?.color?.let { parseHexColor(it) } ?: c.muted
    val isIncome = tx.type == TransactionType.INCOME
    val amountStr = "${if (isIncome) "+" else "−"}${Math.abs(tx.amount).formatMoney()}"

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
                "← BACK",
                fontFamily = IbmPlexMonoFamily,
                fontSize = 12.sp,
                letterSpacing = 0.6.sp,
                color = c.muted,
                modifier = Modifier.clickable(onClick = onBackClick)
            )
            Spacer(Modifier.weight(1f))
            Text(
                "OP · #${tx.id.takeLast(4).uppercase()}",
                fontFamily = IbmPlexMonoFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp,
                color = c.text
            )
            Spacer(Modifier.weight(1f))
            Text(
                "EDIT",
                fontFamily = IbmPlexMonoFamily,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp,
                color = c.lime,
                modifier = Modifier.clickable(onClick = onEdit)
            )
        }

        // Amount hero
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.dp, color = c.border)
                .padding(horizontal = 20.dp, vertical = 36.dp)
                .drawBehind {
                    drawLine(
                        color = c.border,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "${tx.type.label.uppercase()} · ${tx.date.formatMedium().uppercase()} · ${tx.time.formatHm()}",
                fontFamily = IbmPlexMonoFamily,
                fontSize = 9.sp,
                letterSpacing = 1.2.sp,
                color = c.faint
            )
            Text(
                amountStr,
                fontFamily = IbmPlexMonoFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 52.sp,
                letterSpacing = (-2).sp,
                lineHeight = 56.sp,
                color = c.text,
                modifier = Modifier.padding(top = 10.dp)
            )
            Row(
                modifier = Modifier
                    .padding(top = 14.dp)
                    .border(1.dp, c.border)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).background(catColor))
                Spacer(Modifier.width(8.dp))
                Text(
                    cat?.name?.uppercase() ?: "—",
                    fontFamily = IbmPlexMonoFamily,
                    fontSize = 11.sp,
                    letterSpacing = 1.0.sp,
                    color = c.text
                )
            }
        }

        // Meta rows
        Column(modifier = Modifier.weight(1f).padding(horizontal = 20.dp)) {
            listOfNotNull(
                "NOTE" to tx.note.ifBlank { "—" },
                "ACCOUNT" to (acc?.name ?: "—"),
                if (tx.tags.isNotEmpty()) "TAGS" to tx.tags.joinToString(" · ") else null,
                if (tx.type == TransactionType.TRANSFER) "TO ACCOUNT" to (state.toAccount?.name ?: "—") else null
            ).forEachIndexed { idx, (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp)
                        .drawBehind {
                            drawLine(
                                color = c.border,
                                start = androidx.compose.ui.geometry.Offset(0f, size.height),
                                end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        key,
                        fontFamily = IbmPlexMonoFamily,
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp,
                        color = c.faint
                    )
                    Text(
                        value,
                        fontFamily = IbmPlexMonoFamily,
                        fontSize = 13.sp,
                        color = c.text
                    )
                }
            }
        }

        // Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawLine(
                        color = c.border,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
        ) {
            Text(
                "DUPLICATE",
                fontFamily = IbmPlexMonoFamily,
                fontSize = 11.sp,
                letterSpacing = 1.4.sp,
                color = c.muted,
                modifier = Modifier
                    .weight(1f)
                    .clickable { vm.duplicate() }
                    .padding(18.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Box(modifier = Modifier.width(1.dp).height(56.dp).background(c.border))
            Text(
                "DELETE",
                fontFamily = IbmPlexMonoFamily,
                fontSize = 11.sp,
                letterSpacing = 1.4.sp,
                color = c.red,
                modifier = Modifier
                    .weight(1f)
                    .clickable { showDeleteDialog = true }
                    .padding(18.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить операцию?", fontFamily = IbmPlexMonoFamily, color = c.text) },
            text = { Text("Это действие нельзя отменить.", fontFamily = IbmPlexSansFamily, color = c.muted) },
            confirmButton = {
                Text(
                    "УДАЛИТЬ",
                    fontFamily = IbmPlexMonoFamily,
                    fontSize = 11.sp,
                    color = c.red,
                    modifier = Modifier.clickable {
                        showDeleteDialog = false
                        vm.delete()
                    }.padding(8.dp)
                )
            },
            dismissButton = {
                Text(
                    "ОТМЕНА",
                    fontFamily = IbmPlexMonoFamily,
                    fontSize = 11.sp,
                    color = c.muted,
                    modifier = Modifier.clickable { showDeleteDialog = false }.padding(8.dp)
                )
            },
            containerColor = c.surface,
            titleContentColor = c.text
        )
    }
}

