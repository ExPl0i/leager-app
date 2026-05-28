package com.ledger.app.ui.screen.pin

import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ledger.app.LedgerApplication
import com.ledger.app.ui.theme.IbmPlexMonoFamily
import com.ledger.app.ui.theme.IbmPlexSansFamily
import com.ledger.app.ui.theme.ledger

@Composable
fun PinScreen(
    app: LedgerApplication,
    onSuccess: () -> Unit,
    forceSetup: Boolean = false
) {
    val vm: PinViewModel = viewModel(factory = PinViewModel.Factory(app))
    val state by vm.state.collectAsState()
    val c = MaterialTheme.ledger
    val context = LocalContext.current
    val biometricEnabled by app.securityManager.isBiometricEnabled.collectAsState(initial = false)

    LaunchedEffect(forceSetup) {
        if (forceSetup) vm.forceSetMode()
    }

    LaunchedEffect(state.success) {
        if (state.success) onSuccess()
    }

    LaunchedEffect(biometricEnabled) {
        if (biometricEnabled && state.mode == PinMode.VERIFY) {
            val activity = context as? FragmentActivity ?: return@LaunchedEffect
            val executor = ContextCompat.getMainExecutor(activity)
            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }
            }
            val prompt = BiometricPrompt(activity, executor, callback)
            val info = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Войти в Ledger")
                .setSubtitle("Подтвердите личность")
                .setNegativeButtonText("Использовать PIN")
                .build()
            prompt.authenticate(info)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(c.bg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "LEDGER",
            fontFamily = IbmPlexMonoFamily,
            fontSize = 11.sp,
            letterSpacing = 3.sp,
            color = c.lime
        )
        Spacer(Modifier.height(8.dp))
        Text(
            when (state.mode) {
                PinMode.VERIFY -> "Введите PIN"
                PinMode.SET -> "Создайте PIN"
                PinMode.CONFIRM -> "Подтвердите PIN"
            },
            fontFamily = IbmPlexSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            color = c.text
        )

        if (state.error.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(state.error, fontFamily = IbmPlexMonoFamily, fontSize = 11.sp, color = c.red)
        }

        Spacer(Modifier.height(32.dp))

        // Dot indicators
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { idx ->
                val filled = idx < state.digits.length
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(if (filled) c.lime else Color.Transparent)
                        .border(1.dp, if (filled) c.lime else c.border)
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        // Keypad
        val keys = listOf("1","2","3","4","5","6","7","8","9","","0","⌫")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            keys.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { key ->
                        if (key.isEmpty()) {
                            Spacer(Modifier.size(80.dp, 56.dp))
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(80.dp, 56.dp)
                                    .border(1.dp, c.border)
                                    .clickable {
                                        if (key == "⌫") vm.onBackspace() else vm.onDigit(key)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    key,
                                    fontFamily = IbmPlexMonoFamily,
                                    fontSize = if (key == "⌫") 18.sp else 20.sp,
                                    color = if (key == "⌫") c.muted else c.text
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
