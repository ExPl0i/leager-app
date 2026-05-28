package com.ledger.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ledger.app.ui.components.LedgerBottomNav
import com.ledger.app.ui.components.NavTab
import com.ledger.app.ui.navigation.LedgerNavHost
import com.ledger.app.ui.navigation.Routes
import com.ledger.app.ui.theme.LedgerTheme
import com.ledger.app.ui.theme.ledger
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val app = application as LedgerApplication

        val darkTheme = runBlocking { app.prefsManager.isDarkTheme.first() }
        val pinEnabled = runBlocking { app.securityManager.isPinEnabled.first() }

        val startDestination = if (pinEnabled) Routes.PIN else Routes.HOME

        setContent {
            val darkState by app.prefsManager.isDarkTheme.collectAsState(initial = darkTheme)

            LedgerTheme(darkTheme = darkState) {
                val navController = rememberNavController()
                val backEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backEntry?.destination?.route

                val showBottomNav = currentRoute != null
                    && currentRoute != Routes.PIN
                    && currentRoute != Routes.PIN_SETUP

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomNav) {
                            LedgerBottomNav(
                                currentRoute = currentRoute ?: Routes.HOME,
                                onTabSelected = { tab ->
                                    when (tab) {
                                        is NavTab.Add -> navController.navigate(Routes.ADD_TRANSACTION_BASE)
                                        else -> navController.navigate(tab.route) {
                                            launchSingleTop = true
                                            restoreState = tab.route != Routes.HOME
                                            popUpTo(Routes.HOME) {
                                                saveState = tab.route != Routes.HOME
                                                inclusive = tab.route == Routes.HOME
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    },
                    containerColor = MaterialTheme.ledger.bg
                ) { paddingValues ->
                    LedgerNavHost(
                        navController = navController,
                        app = app,
                        startDestination = startDestination,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}
