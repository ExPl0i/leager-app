package com.ledger.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class LedgerColors(
    val bg: Color,
    val surface: Color,
    val surface2: Color,
    val border: Color,
    val borderStrong: Color,
    val text: Color,
    val muted: Color,
    val faint: Color,
    val lime: Color,
    val red: Color,
    val isDark: Boolean
)

val LedgerDarkColors = LedgerColors(
    bg           = LedgerBgDark,
    surface      = LedgerSurfDark,
    surface2     = LedgerSurf2Dark,
    border       = LedgerBorderDark,
    borderStrong = LedgerBorderStrongDark,
    text         = LedgerTextDark,
    muted        = LedgerMutedDark,
    faint        = LedgerFaintDark,
    lime         = LedgerLimeDark,
    red          = LedgerRedDark,
    isDark       = true
)

val LedgerLightColors = LedgerColors(
    bg           = LedgerBgLight,
    surface      = LedgerSurfLight,
    surface2     = LedgerSurf2Light,
    border       = LedgerBorderLight,
    borderStrong = LedgerBorderStrongLight,
    text         = LedgerTextLight,
    muted        = LedgerMutedLight,
    faint        = LedgerFaintLight,
    lime         = LedgerLimeLight,
    red          = LedgerRedLight,
    isDark       = false
)

val LocalLedgerColors = staticCompositionLocalOf { LedgerDarkColors }

private fun buildColorScheme(c: LedgerColors): ColorScheme = darkColorScheme(
    primary            = c.lime,
    onPrimary          = Color(0xFF0A0A0A),
    secondary          = c.text,
    onSecondary        = c.bg,
    tertiary           = c.red,
    background         = c.bg,
    surface            = c.surface,
    surfaceVariant     = c.surface2,
    onBackground       = c.text,
    onSurface          = c.text,
    onSurfaceVariant   = c.muted,
    outline            = c.border,
    outlineVariant     = c.borderStrong,
    error              = c.red,
    onError            = Color.White
)

@Composable
fun LedgerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val ledgerColors = if (darkTheme) LedgerDarkColors else LedgerLightColors
    val colorScheme = buildColorScheme(ledgerColors)

    CompositionLocalProvider(LocalLedgerColors provides ledgerColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LedgerTypography,
            content = content
        )
    }
}

// Convenient accessor
val MaterialTheme.ledger: LedgerColors
    @Composable get() = LocalLedgerColors.current
