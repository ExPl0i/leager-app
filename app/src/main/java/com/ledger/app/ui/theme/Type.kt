package com.ledger.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.ledger.app.R

private val fontsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val ibmPlexMono = GoogleFont("IBM Plex Mono")
private val ibmPlexSans = GoogleFont("IBM Plex Sans")

val IbmPlexMonoFamily = FontFamily(
    Font(googleFont = ibmPlexMono, fontProvider = fontsProvider, weight = FontWeight.Normal),
    Font(googleFont = ibmPlexMono, fontProvider = fontsProvider, weight = FontWeight.Medium),
    Font(googleFont = ibmPlexMono, fontProvider = fontsProvider, weight = FontWeight.SemiBold),
)

val IbmPlexSansFamily = FontFamily(
    Font(googleFont = ibmPlexSans, fontProvider = fontsProvider, weight = FontWeight.Normal),
    Font(googleFont = ibmPlexSans, fontProvider = fontsProvider, weight = FontWeight.Medium),
    Font(googleFont = ibmPlexSans, fontProvider = fontsProvider, weight = FontWeight.SemiBold),
    Font(googleFont = ibmPlexSans, fontProvider = fontsProvider, weight = FontWeight.Bold),
)

val LedgerTypography = Typography(
    // Display hero number (net worth, big amounts)
    displayLarge = TextStyle(
        fontFamily = IbmPlexMonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 44.sp,
        letterSpacing = (-1.5).sp,
        lineHeight = 48.sp
    ),
    // Large stat number
    displayMedium = TextStyle(
        fontFamily = IbmPlexMonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        letterSpacing = (-0.8).sp,
        lineHeight = 32.sp
    ),
    // Screen title
    titleLarge = TextStyle(
        fontFamily = IbmPlexSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    // Section / card title
    titleMedium = TextStyle(
        fontFamily = IbmPlexSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    // Primary row text
    bodyLarge = TextStyle(
        fontFamily = IbmPlexSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Secondary row text
    bodyMedium = TextStyle(
        fontFamily = IbmPlexSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Small UI text
    bodySmall = TextStyle(
        fontFamily = IbmPlexSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    // Mono number (amounts in rows)
    labelLarge = TextStyle(
        fontFamily = IbmPlexMonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Caption / meta (time, category label)
    labelMedium = TextStyle(
        fontFamily = IbmPlexMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 0.4.sp,
        lineHeight = 16.sp
    ),
    // Section header (all caps)
    labelSmall = TextStyle(
        fontFamily = IbmPlexMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 1.4.sp,
        lineHeight = 14.sp
    )
)
