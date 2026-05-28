package com.ledger.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledger.app.domain.model.Account
import com.ledger.app.domain.model.AccountType
import com.ledger.app.ui.theme.IbmPlexMonoFamily
import com.ledger.app.ui.theme.IbmPlexSansFamily
import com.ledger.app.ui.theme.ledger

val ACCOUNT_PRESET_COLORS = listOf(
    "#C5FF4A", "#FF6B5B", "#52E0C4", "#9B8BFF",
    "#FFD24A", "#FF9DC4", "#7BB8FF", "#A0A0A0"
)

@Composable
fun AccountDialog(
    editTarget: Account?,
    onDismiss: () -> Unit,
    onSave: (name: String, type: AccountType, currency: String, color: String, includeInTotal: Boolean, balance: Double) -> Unit,
    onArchive: (() -> Unit)? = null
) {
    val c = MaterialTheme.ledger
    val isEditing = editTarget != null

    var name by remember(editTarget) { mutableStateOf(editTarget?.name ?: "") }
    var type by remember(editTarget) { mutableStateOf(editTarget?.type ?: AccountType.CARD) }
    var currency by remember(editTarget) { mutableStateOf(editTarget?.currency ?: "RUB") }
    var color by remember(editTarget) { mutableStateOf(editTarget?.color ?: ACCOUNT_PRESET_COLORS[0]) }
    var includeInTotal by remember(editTarget) { mutableStateOf(editTarget?.includeInTotal ?: true) }
    var balanceText by remember(editTarget) { mutableStateOf(editTarget?.balance?.let { "%.2f".format(it) } ?: "0") }
    var showArchiveConfirm by remember { mutableStateOf(false) }

    if (showArchiveConfirm) {
        AlertDialog(
            onDismissRequest = { showArchiveConfirm = false },
            containerColor = c.surface,
            titleContentColor = c.text,
            title = { Text("Архивировать счёт?", fontFamily = IbmPlexSansFamily, color = c.text) },
            text = { Text("Счёт скроется из списка. История операций сохранится.", fontFamily = IbmPlexSansFamily, color = c.muted) },
            confirmButton = {
                Text("АРХИВИРОВАТЬ", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp, color = c.red,
                    modifier = Modifier.clickable { onArchive?.invoke() }.padding(8.dp))
            },
            dismissButton = {
                Text("ОТМЕНА", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp, color = c.muted,
                    modifier = Modifier.clickable { showArchiveConfirm = false }.padding(8.dp))
            }
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.surface,
        titleContentColor = c.text,
        title = {
            Text(
                if (isEditing) "РЕДАКТИРОВАТЬ СЧЁТ" else "НОВЫЙ СЧЁТ",
                fontFamily = IbmPlexMonoFamily, fontSize = 12.sp, letterSpacing = 1.4.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {

                // Name
                Column {
                    Text("НАЗВАНИЕ", fontFamily = IbmPlexMonoFamily, fontSize = 9.sp,
                        letterSpacing = 1.2.sp, color = c.faint)
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        textStyle = TextStyle(fontFamily = IbmPlexMonoFamily, fontSize = 15.sp, color = c.text),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .drawBehind {
                                drawLine(c.borderStrong,
                                    androidx.compose.ui.geometry.Offset(0f, size.height),
                                    androidx.compose.ui.geometry.Offset(size.width, size.height),
                                    1.dp.toPx())
                            }
                            .padding(bottom = 8.dp)
                    )
                }

                // Type
                Column {
                    Text("ТИП", fontFamily = IbmPlexMonoFamily, fontSize = 9.sp,
                        letterSpacing = 1.2.sp, color = c.faint, modifier = Modifier.padding(bottom = 6.dp))
                    Row(modifier = Modifier.fillMaxWidth().border(1.dp, c.border)) {
                        AccountType.entries.forEach { t ->
                            val sel = type == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (sel) c.text else androidx.compose.ui.graphics.Color.Transparent)
                                    .clickable { type = t }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(t.label, fontFamily = IbmPlexMonoFamily, fontSize = 9.sp,
                                    color = if (sel) c.bg else c.muted)
                            }
                        }
                    }
                }

                // Currency
                Column {
                    Text("ВАЛЮТА", fontFamily = IbmPlexMonoFamily, fontSize = 9.sp,
                        letterSpacing = 1.2.sp, color = c.faint, modifier = Modifier.padding(bottom = 6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("RUB", "USD", "EUR").forEach { cur ->
                            val sel = currency == cur
                            Box(
                                modifier = Modifier
                                    .border(1.dp, if (sel) c.text else c.border)
                                    .background(if (sel) c.text else androidx.compose.ui.graphics.Color.Transparent)
                                    .clickable { currency = cur }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(cur, fontFamily = IbmPlexMonoFamily, fontSize = 12.sp,
                                    color = if (sel) c.bg else c.muted)
                            }
                        }
                    }
                }

                // Color
                Column {
                    Text("ЦВЕТ", fontFamily = IbmPlexMonoFamily, fontSize = 9.sp,
                        letterSpacing = 1.2.sp, color = c.faint, modifier = Modifier.padding(bottom = 6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ACCOUNT_PRESET_COLORS.forEach { hex ->
                            val sel = color == hex
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(parseHexColor(hex))
                                    .border(2.dp, if (sel) c.text else androidx.compose.ui.graphics.Color.Transparent)
                                    .clickable { color = hex }
                            )
                        }
                    }
                }

                // Balance
                Column {
                    Text("БАЛАНС", fontFamily = IbmPlexMonoFamily, fontSize = 9.sp,
                        letterSpacing = 1.2.sp, color = c.faint)
                    BasicTextField(
                        value = balanceText,
                        onValueChange = { s ->
                            if (s.isEmpty() || s.matches(Regex("\\d*\\.?\\d*"))) balanceText = s
                        },
                        textStyle = TextStyle(fontFamily = IbmPlexMonoFamily, fontSize = 15.sp, color = c.text),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .drawBehind {
                                drawLine(c.borderStrong,
                                    androidx.compose.ui.geometry.Offset(0f, size.height),
                                    androidx.compose.ui.geometry.Offset(size.width, size.height),
                                    1.dp.toPx())
                            }
                            .padding(bottom = 8.dp)
                    )
                }

                // Include in total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text("УЧИТЫВАТЬ В ИТОГЕ", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp, color = c.text)
                        Text("Включить баланс в общую сумму", fontFamily = IbmPlexMonoFamily,
                            fontSize = 9.sp, color = c.muted, modifier = Modifier.padding(top = 2.dp))
                    }
                    Switch(
                        checked = includeInTotal,
                        onCheckedChange = { includeInTotal = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = c.bg, checkedTrackColor = c.lime,
                            uncheckedThumbColor = c.muted, uncheckedTrackColor = c.surface2
                        )
                    )
                }

                if (isEditing && onArchive != null) {
                    Text(
                        "АРХИВИРОВАТЬ СЧЁТ",
                        fontFamily = IbmPlexMonoFamily, fontSize = 10.sp,
                        letterSpacing = 1.2.sp, color = c.red,
                        modifier = Modifier.clickable { showArchiveConfirm = true }.padding(vertical = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Text("СОХРАНИТЬ", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp,
                letterSpacing = 1.2.sp, color = c.lime,
                modifier = Modifier.clickable {
                    onSave(name, type, currency, color, includeInTotal, balanceText.toDoubleOrNull() ?: 0.0)
                }.padding(8.dp))
        },
        dismissButton = {
            Text("ОТМЕНА", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp, color = c.muted,
                modifier = Modifier.clickable(onClick = onDismiss).padding(8.dp))
        }
    )
}
