package com.example.kihez.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = androidx.core.R.array.com_google_android_gms_fonts_certs
)

val NewsreaderFont = GoogleFont("Newsreader")
val ManropeFont = GoogleFont("Manrope")

val NewsreaderFontFamily = FontFamily(
    Font(googleFont = NewsreaderFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = NewsreaderFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = NewsreaderFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = NewsreaderFont, fontProvider = fontProvider, weight = FontWeight.Bold)
)

val ManropeFontFamily = FontFamily(
    Font(googleFont = ManropeFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = ManropeFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = ManropeFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = ManropeFont, fontProvider = fontProvider, weight = FontWeight.Bold)
)

val Typography = Typography(
    // headline-xl
    displayLarge = TextStyle(
        fontFamily = NewsreaderFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.02).sp
    ),
    // headline-xl-mobile
    displayMedium = TextStyle(
        fontFamily = NewsreaderFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.01).sp
    ),
    // headline-lg
    displaySmall = TextStyle(
        fontFamily = NewsreaderFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    // headline-md
    headlineMedium = TextStyle(
        fontFamily = NewsreaderFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    // body-lg
    bodyLarge = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp
    ),
    // body-md
    bodyMedium = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    // label-md
    labelMedium = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.7.sp
    ),
    // label-sm
    labelSmall = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.96.sp
    )
)

