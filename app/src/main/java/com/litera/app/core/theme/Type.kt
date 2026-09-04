package com.litera.app.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.litera.app.R

// The LiteraUX Figma Style Guide uses "Raleway" everywhere (SemiBold on
// buttons/chips, Bold on titles) with a type scale of 32/24/20 for titles
// and 18/16/14/8 for body/labels. We resolve Raleway through Compose's
// downloadable Google Fonts API: it fetches the font from Google Play
// services on the end-user's device at runtime, so no font binaries need to
// be bundled in (or downloaded into) this project. Requires the
// androidx.compose.ui:ui-text-google-fonts dependency and
// res/values/font_certs.xml (both already wired up in this project).

private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val ralewayFontName = GoogleFont("Raleway")

private fun ralewayFamily(weight: FontWeight) = FontFamily(
    Font(googleFont = ralewayFontName, fontProvider = googleFontProvider, weight = weight)
)

val RalewayRegular = ralewayFamily(FontWeight.Normal)
val RalewayMedium = ralewayFamily(FontWeight.Medium)
val RalewaySemiBold = ralewayFamily(FontWeight.SemiBold)
val RalewayBold = ralewayFamily(FontWeight.Bold)

// Material3 slots mapped onto the Figma type scale (32/24/20 for
// titles, 18/16/14/8 for body/labels).
val LiteraTypography = Typography(
    displaySmall = TextStyle( // 32 — biggest screen titles
        fontFamily = RalewayBold,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp
    ),
    headlineMedium = TextStyle( // 24 — section/screen titles
        fontFamily = RalewayBold,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = RalewaySemiBold,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleLarge = TextStyle( // 20 — used interchangeably with headlineSmall
        fontFamily = RalewaySemiBold,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RalewaySemiBold,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RalewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RalewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle( // 14, lighter use
        fontFamily = RalewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle( // buttons/chips — SemiBold per the Figma component specs
        fontFamily = RalewaySemiBold,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = RalewayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle( // 8 — smallest labels/badges in the Figma scale
        fontFamily = RalewayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 8.sp,
        lineHeight = 10.sp
    )
)
