package com.ledger.app.ui.screen.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ledger.app.LedgerApplication
import com.ledger.app.ui.theme.IbmPlexMonoFamily
import com.ledger.app.ui.theme.IbmPlexSansFamily
import com.ledger.app.ui.theme.ledger

@Composable
fun SettingsScreen(
    app: LedgerApplication,
    onBackClick: () -> Unit,
    onSetPin: (() -> Unit)? = null
) {
    val vm: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(app)
    )
    val state by vm.state.collectAsState()
    val c = MaterialTheme.ledger

    LaunchedEffect(state.navigateToPinSetup) {
        if (state.navigateToPinSetup) {
            onSetPin?.invoke()
            vm.onNavigatedToPinSetup()
        }
    }

    val context = LocalContext.current
    var showClearConfirm by remember { mutableStateOf(false) }
    var showUsdDialog by remember { mutableStateOf(false) }
    var showEurDialog by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { vm.importCsv(it) } }

    when (val status = state.importStatus) {
        is ImportStatus.Done -> {
            AlertDialog(
                onDismissRequest = vm::dismissImport,
                containerColor = c.surface,
                titleContentColor = c.text,
                title = {
                    Text("Импорт завершён", fontFamily = IbmPlexMonoFamily,
                        fontSize = 12.sp, letterSpacing = 1.4.sp)
                },
                text = {
                    Text(
                        "Импортировано: ${status.imported}\nПропущено: ${status.skipped}",
                        fontFamily = IbmPlexMonoFamily, fontSize = 13.sp, color = c.muted
                    )
                },
                confirmButton = {
                    Text("OK", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp,
                        letterSpacing = 1.2.sp, color = c.lime,
                        modifier = Modifier.clickable(onClick = vm::dismissImport).padding(8.dp))
                }
            )
        }
        is ImportStatus.Error -> {
            AlertDialog(
                onDismissRequest = vm::dismissImport,
                containerColor = c.surface,
                titleContentColor = c.text,
                title = {
                    Text("Ошибка импорта", fontFamily = IbmPlexMonoFamily,
                        fontSize = 12.sp, letterSpacing = 1.4.sp)
                },
                text = {
                    Text(status.message, fontFamily = IbmPlexMonoFamily,
                        fontSize = 13.sp, color = c.red)
                },
                confirmButton = {
                    Text("OK", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp,
                        letterSpacing = 1.2.sp, color = c.lime,
                        modifier = Modifier.clickable(onClick = vm::dismissImport).padding(8.dp))
                }
            )
        }
        else -> {}
    }

    if (showUsdDialog) {
        RateEditDialog(
            title = "КУРС USD / RUB",
            initialValue = state.usdRate,
            onDismiss = { showUsdDialog = false },
            onSave = { vm.setUsdRate(it); showUsdDialog = false }
        )
    }
    if (showEurDialog) {
        RateEditDialog(
            title = "КУРС EUR / RUB",
            initialValue = state.eurRate,
            onDismiss = { showEurDialog = false },
            onSave = { vm.setEurRate(it); showEurDialog = false }
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Очистить данные?", fontFamily = IbmPlexSansFamily) },
            text = { Text("Все транзакции будут удалены. Действие необратимо.", fontFamily = IbmPlexSansFamily) },
            confirmButton = {
                Text(
                    "Удалить",
                    fontFamily = IbmPlexMonoFamily,
                    color = c.red,
                    modifier = Modifier.clickable { vm.clearData(); showClearConfirm = false }.padding(8.dp)
                )
            },
            dismissButton = {
                Text(
                    "Отмена",
                    fontFamily = IbmPlexMonoFamily,
                    color = c.muted,
                    modifier = Modifier.clickable { showClearConfirm = false }.padding(8.dp)
                )
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(c.bg)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 20.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Настройки", fontFamily = IbmPlexSansFamily, fontWeight = FontWeight.Medium, fontSize = 20.sp, color = c.text)
            }
        }

        item {
            SettingsSection("БЕЗОПАСНОСТЬ") {
                SettingsRow(
                    title = "PIN-защита",
                    subtitle = if (state.pinEnabled) "Включена" else "Выключена",
                    trailing = {
                        Switch(
                            checked = state.pinEnabled,
                            onCheckedChange = { vm.togglePin() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = c.bg,
                                checkedTrackColor = c.lime,
                                uncheckedThumbColor = c.muted,
                                uncheckedTrackColor = c.surface2
                            )
                        )
                    }
                )
                SettingsRow(
                    title = "Биометрия",
                    subtitle = if (state.biometricEnabled) "Включена" else "Выключена",
                    trailing = {
                        Switch(
                            checked = state.biometricEnabled,
                            onCheckedChange = { if (state.pinEnabled) vm.toggleBiometric() },
                            enabled = state.pinEnabled,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = c.bg,
                                checkedTrackColor = c.lime,
                                uncheckedThumbColor = c.muted,
                                uncheckedTrackColor = c.surface2
                            )
                        )
                    }
                )
            }
        }

        item {
            SettingsSection("ОФОРМЛЕНИЕ") {
                SettingsRow(
                    title = "Тема",
                    subtitle = if (state.darkTheme) "Тёмная" else "Светлая",
                    trailing = {
                        Switch(
                            checked = state.darkTheme,
                            onCheckedChange = { vm.toggleTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = c.bg,
                                checkedTrackColor = c.lime,
                                uncheckedThumbColor = c.muted,
                                uncheckedTrackColor = c.surface2
                            )
                        )
                    }
                )
            }
        }

        item {
            SettingsSection("ВАЛЮТА") {
                SettingsRow(
                    title = "USD / RUB",
                    subtitle = "%.2f".format(state.usdRate),
                    onClick = { showUsdDialog = true }
                )
                SettingsRow(
                    title = "EUR / RUB",
                    subtitle = "%.2f".format(state.eurRate),
                    onClick = { showEurDialog = true }
                )
                SettingsRow(
                    title = "Найти курс в Google",
                    subtitle = "Открыть браузер",
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=USD+RUB+EUR+rate+today"))
                        )
                    }
                )
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text("NET WORTH · ВАЛЮТА ОТОБРАЖЕНИЯ",
                        fontFamily = IbmPlexMonoFamily, fontSize = 9.sp,
                        letterSpacing = 1.2.sp, color = c.faint,
                        modifier = Modifier.padding(bottom = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("RUB", "USD", "EUR").forEach { cur ->
                            val sel = state.netWorthCurrency == cur
                            Box(
                                modifier = Modifier
                                    .border(1.dp, if (sel) c.text else c.border)
                                    .background(if (sel) c.text else androidx.compose.ui.graphics.Color.Transparent)
                                    .clickable { vm.setNetWorthCurrency(cur) }
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                Text(cur, fontFamily = IbmPlexMonoFamily, fontSize = 12.sp,
                                    color = if (sel) c.bg else c.muted)
                            }
                        }
                    }
                }
            }
        }

        item {
            SettingsSection("ДАННЫЕ") {
                val isImporting = state.importStatus is ImportStatus.Importing
                SettingsRow(
                    title = "Импорт CSV",
                    subtitle = if (isImporting) "Импортируем…" else "Загрузить транзакции из файла",
                    onClick = {
                        if (!isImporting) importLauncher.launch(arrayOf("text/*", "*/*"))
                    }
                )
                SettingsRow(title = "Экспорт CSV", subtitle = "Выгрузить все операции")
                SettingsRow(
                    title = "Очистить данные",
                    subtitle = "Удалить все операции",
                    danger = true,
                    onClick = { showClearConfirm = true }
                )
            }
        }

        item {
            Spacer(Modifier.height(40.dp))
            Text(
                "LEDGER · v1.0.0",
                fontFamily = IbmPlexMonoFamily,
                fontSize = 10.sp,
                letterSpacing = 1.4.sp,
                color = c.faint,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun RateEditDialog(
    title: String,
    initialValue: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    val c = MaterialTheme.ledger
    var text by remember { mutableStateOf("%.2f".format(initialValue)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.surface,
        titleContentColor = c.text,
        title = { Text(title, fontFamily = IbmPlexMonoFamily, fontSize = 12.sp, letterSpacing = 1.4.sp) },
        text = {
            BasicTextField(
                value = text,
                onValueChange = { s ->
                    if (s.isEmpty() || s.matches(Regex("\\d*\\.?\\d*"))) text = s
                },
                textStyle = TextStyle(fontFamily = IbmPlexMonoFamily, fontSize = 18.sp, color = c.text),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(c.borderStrong,
                            androidx.compose.ui.geometry.Offset(0f, size.height),
                            androidx.compose.ui.geometry.Offset(size.width, size.height),
                            1.dp.toPx())
                    }
                    .padding(bottom = 8.dp)
            )
        },
        confirmButton = {
            Text("СОХРАНИТЬ", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp,
                letterSpacing = 1.2.sp, color = c.lime,
                modifier = Modifier.clickable {
                    text.toDoubleOrNull()?.let { onSave(it) }
                }.padding(8.dp))
        },
        dismissButton = {
            Text("ОТМЕНА", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp, color = c.muted,
                modifier = Modifier.clickable(onClick = onDismiss).padding(8.dp))
        }
    )
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    val c = MaterialTheme.ledger
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            title,
            fontFamily = IbmPlexMonoFamily,
            fontSize = 9.sp,
            letterSpacing = 1.4.sp,
            color = c.faint,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        Column(content = content)
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String = "",
    danger: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    val c = MaterialTheme.ledger
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .drawBehind {
                drawLine(
                    color = c.border,
                    start = androidx.compose.ui.geometry.Offset(0f, size.height),
                    end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontFamily = IbmPlexSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = if (danger) c.red else c.text
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    fontFamily = IbmPlexMonoFamily,
                    fontSize = 11.sp,
                    color = c.muted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        trailing?.invoke()
        if (trailing == null && onClick != null) {
            Text("›", fontFamily = IbmPlexMonoFamily, fontSize = 18.sp, color = c.muted)
        }
    }
}

