package com.ledger.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledger.app.ui.navigation.Routes
import com.ledger.app.ui.theme.ledger

sealed class NavTab(val route: String, val label: String) {
    object Home        : NavTab(Routes.HOME,        "HOME")
    object Ops         : NavTab(Routes.TRANSACTIONS, "OPS")
    object Add         : NavTab("add",               "+")
    object Stats       : NavTab(Routes.STATS,        "STATS")
    object More        : NavTab(Routes.SETTINGS,     "MORE")
}

val NavTabs = listOf(NavTab.Home, NavTab.Ops, NavTab.Add, NavTab.Stats, NavTab.More)

@Composable
fun LedgerBottomNav(
    currentRoute: String,
    onTabSelected: (NavTab) -> Unit
) {
    val c = MaterialTheme.ledger
    val borderColor = c.border

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(c.bg)
            .navigationBarsPadding()
    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            },
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavTabs.forEach { tab ->
            if (tab is NavTab.Add) {
                // Central + button
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(c.lime)
                        .border(1.dp, c.text)
                        .clickable { onTabSelected(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        fontFamily = com.ledger.app.ui.theme.IbmPlexMonoFamily,
                        fontSize = 26.sp,
                        color = Color(0xFF0A0A0A),
                        lineHeight = 26.sp
                    )
                }
            } else {
                val isActive = currentRoute == tab.route
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onTabSelected(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .border(
                                width = 1.5.dp,
                                color = if (isActive) c.lime else c.muted
                            )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = tab.label,
                        fontFamily = com.ledger.app.ui.theme.IbmPlexMonoFamily,
                        fontSize = 9.sp,
                        letterSpacing = 1.2.sp,
                        color = if (isActive) c.lime else c.muted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    }
}
