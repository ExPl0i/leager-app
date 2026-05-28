package com.ledger.app.ui.screen.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ledger.app.LedgerApplication
import com.ledger.app.domain.model.Category
import com.ledger.app.domain.model.CategoryType
import com.ledger.app.ui.components.ColorPickerRow
import com.ledger.app.ui.components.LEDGER_COLOR_PALETTE
import com.ledger.app.ui.components.parseHexColor
import com.ledger.app.ui.theme.IbmPlexMonoFamily
import com.ledger.app.ui.theme.IbmPlexSansFamily
import com.ledger.app.ui.theme.ledger
import com.ledger.app.util.formatMoney

@Composable
fun CategoriesScreen(
    app: LedgerApplication,
    onBackClick: () -> Unit
) {
    val vm: CategoriesViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val c = MaterialTheme.ledger

    if (state.showDialog) {
        CategoryDialog(
            editTarget = state.editTarget,
            defaultType = state.selectedTab,
            onDismiss = vm::dismissDialog,
            onSave = { name, type, color, budget -> vm.saveCategory(name, type, color, budget) },
            onDelete = state.editTarget?.let { cat -> { vm.deleteCategory(cat) } }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(c.bg)) {

        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Категории", fontFamily = IbmPlexSansFamily,
                    fontWeight = FontWeight.Medium, fontSize = 20.sp, color = c.text)
                Box(
                    modifier = Modifier.size(36.dp).border(1.dp, c.text)
                        .clickable { vm.openCreate() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontFamily = IbmPlexMonoFamily, fontSize = 20.sp, color = c.text)
                }
            }
        }

        // Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 18.dp)
                    .drawBehind {
                        drawLine(c.border, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                    }
            ) {
                listOf(
                    CategoryType.EXPENSE to "РАСХОДЫ · ${state.expenseCategories.size}",
                    CategoryType.INCOME  to "ДОХОДЫ · ${state.incomeCategories.size}"
                ).forEach { (type, label) ->
                    val isSelected = state.selectedTab == type
                    Box(
                        modifier = Modifier
                            .padding(end = 24.dp)
                            .clickable { vm.selectTab(type) }
                            .drawBehind {
                                if (isSelected) drawLine(c.lime,
                                    Offset(0f, size.height + 1.dp.toPx()),
                                    Offset(size.width, size.height + 1.dp.toPx()),
                                    2.dp.toPx())
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(label, fontFamily = IbmPlexMonoFamily, fontSize = 11.sp,
                            letterSpacing = 1.3.sp, color = if (isSelected) c.text else c.faint)
                    }
                }
            }
        }

        val displayList = if (state.selectedTab == CategoryType.EXPENSE)
            state.expenseCategories else state.incomeCategories

        items(displayList, key = { it.category.id }) { item ->
            val cat = item.category
            val budget = cat.budget
            val pct = if (budget != null && budget > 0) (item.spent / budget).toFloat() else 0f
            val over = pct > 1f
            val color = parseHexColor(cat.color)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { vm.openEdit(cat) }
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .drawBehind {
                        drawLine(c.border, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).background(color),
                        contentAlignment = Alignment.Center) {
                        Text("◐", fontFamily = IbmPlexMonoFamily, fontSize = 14.sp, color = c.bg)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(cat.name, fontFamily = IbmPlexSansFamily,
                                fontWeight = FontWeight.Medium, fontSize = 14.sp, color = c.text)
                            if (budget != null) {
                                Text("${item.spent.formatMoney()} / ${budget.formatMoney()}",
                                    fontFamily = IbmPlexMonoFamily, fontSize = 13.sp,
                                    color = if (over) c.red else c.text)
                            } else {
                                Text("${item.opsCount} ops",
                                    fontFamily = IbmPlexMonoFamily, fontSize = 12.sp, color = c.muted)
                            }
                        }
                        if (budget != null) {
                            Box(modifier = Modifier.fillMaxWidth().height(3.dp)
                                .background(c.surface2).padding(top = 8.dp)) {
                                Box(modifier = Modifier
                                    .fillMaxWidth(pct.coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .background(if (over) c.red else color))
                            }
                            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${(pct * 100).toInt()}% · ${(budget - item.spent).coerceAtLeast(0.0).formatMoney()} left",
                                    fontFamily = IbmPlexMonoFamily, fontSize = 10.sp,
                                    letterSpacing = 0.8.sp, color = c.faint)
                                Text("${item.opsCount} ops",
                                    fontFamily = IbmPlexMonoFamily, fontSize = 10.sp, color = c.faint)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun CategoryDialog(
    editTarget: Category?,
    defaultType: CategoryType,
    onDismiss: () -> Unit,
    onSave: (name: String, type: CategoryType, color: String, budget: Double?) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val c = MaterialTheme.ledger
    val isEditing = editTarget != null

    var name by remember(editTarget) { mutableStateOf(editTarget?.name ?: "") }
    var type by remember(editTarget) { mutableStateOf(editTarget?.type ?: defaultType) }
    var color by remember(editTarget) { mutableStateOf(editTarget?.color ?: LEDGER_COLOR_PALETTE[0]) }
    var budgetText by remember(editTarget) {
        mutableStateOf(editTarget?.budget?.let { "%.0f".format(it) } ?: "")
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = c.surface,
            titleContentColor = c.text,
            title = { Text("Удалить категорию?", fontFamily = IbmPlexSansFamily, color = c.text) },
            text = {
                Text("Операции с этой категорией сохранятся, но потеряют привязку.",
                    fontFamily = IbmPlexSansFamily, color = c.muted)
            },
            confirmButton = {
                Text("УДАЛИТЬ", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp, color = c.red,
                    modifier = Modifier.clickable { onDelete?.invoke() }.padding(8.dp))
            },
            dismissButton = {
                Text("ОТМЕНА", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp, color = c.muted,
                    modifier = Modifier.clickable { showDeleteConfirm = false }.padding(8.dp))
            }
        )
        return
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(c.surface)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                if (isEditing) "РЕДАКТИРОВАТЬ" else "НОВАЯ КАТЕГОРИЯ",
                fontFamily = IbmPlexMonoFamily, fontSize = 12.sp, letterSpacing = 1.4.sp, color = c.text
            )

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
                                Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                        }
                        .padding(bottom = 8.dp)
                )
            }

            // Type (locked when editing)
            Column {
                Text("ТИП", fontFamily = IbmPlexMonoFamily, fontSize = 9.sp,
                    letterSpacing = 1.2.sp, color = c.faint, modifier = Modifier.padding(bottom = 6.dp))
                Row(modifier = Modifier.fillMaxWidth().border(1.dp, c.border)) {
                    listOf(CategoryType.EXPENSE, CategoryType.INCOME).forEach { t ->
                        val sel = type == t
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (sel) c.text else androidx.compose.ui.graphics.Color.Transparent)
                                .then(if (!isEditing) Modifier.clickable { type = t } else Modifier)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(t.label, fontFamily = IbmPlexMonoFamily, fontSize = 11.sp,
                                color = if (sel) c.bg else if (isEditing) c.faint else c.muted)
                        }
                    }
                }
                if (isEditing) {
                    Text("Тип нельзя изменить после создания", fontFamily = IbmPlexMonoFamily,
                        fontSize = 9.sp, color = c.faint, modifier = Modifier.padding(top = 4.dp))
                }
            }

            // Color — 64-color scrollable palette
            Column {
                Text("ЦВЕТ", fontFamily = IbmPlexMonoFamily, fontSize = 9.sp,
                    letterSpacing = 1.2.sp, color = c.faint, modifier = Modifier.padding(bottom = 6.dp))
                ColorPickerRow(selected = color, onSelect = { color = it })
            }

            // Budget (only for EXPENSE)
            if (type == CategoryType.EXPENSE) {
                Column {
                    Text("БЮДЖЕТ НА МЕСЯЦ (необязательно)",
                        fontFamily = IbmPlexMonoFamily, fontSize = 9.sp,
                        letterSpacing = 1.2.sp, color = c.faint)
                    BasicTextField(
                        value = budgetText,
                        onValueChange = { s -> if (s.all { it.isDigit() }) budgetText = s },
                        textStyle = TextStyle(fontFamily = IbmPlexMonoFamily, fontSize = 15.sp, color = c.text),
                        singleLine = true,
                        decorationBox = { inner ->
                            if (budgetText.isEmpty()) {
                                Text("—", fontFamily = IbmPlexMonoFamily, fontSize = 15.sp, color = c.faint)
                            }
                            inner()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .drawBehind {
                                drawLine(c.borderStrong,
                                    Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                            }
                            .padding(bottom = 8.dp)
                    )
                }
            }

            // Delete button (edit mode only)
            if (isEditing && onDelete != null) {
                Text(
                    "УДАЛИТЬ КАТЕГОРИЮ",
                    fontFamily = IbmPlexMonoFamily, fontSize = 10.sp,
                    letterSpacing = 1.2.sp, color = c.red,
                    modifier = Modifier.clickable { showDeleteConfirm = true }.padding(vertical = 4.dp)
                )
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ОТМЕНА", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp, color = c.muted,
                    modifier = Modifier.clickable(onClick = onDismiss).padding(8.dp))
                Spacer(Modifier.width(16.dp))
                Text("СОХРАНИТЬ", fontFamily = IbmPlexMonoFamily, fontSize = 11.sp,
                    letterSpacing = 1.2.sp, color = c.lime,
                    modifier = Modifier.clickable {
                        onSave(name, type, color, budgetText.toDoubleOrNull())
                    }.padding(8.dp))
            }
        }
    }
}
