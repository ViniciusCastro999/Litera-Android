package com.litera.app.core.theme

import androidx.compose.ui.graphics.Color

// Exact design tokens read from the LiteraUX Figma Style Guide
// (file HjDOMSiT5S17DZKR77b8SY). These are the real palettes, not
// approximations — see "Cores" page in the Figma file.

// "Daisy Bush" — primary purple scale
val DaisyBush50 = Color(0xFFF4F1FF)
val DaisyBush100 = Color(0xFFEBE5FF)
val DaisyBush200 = Color(0xFFDACFFF)
val DaisyBush300 = Color(0xFFBFA9FF)
val DaisyBush400 = Color(0xFFA178FF)
val DaisyBush500 = Color(0xFF8542FF)
val DaisyBush600 = Color(0xFF781BFF)
val DaisyBush700 = Color(0xFF6A0AF7)
val DaisyBush800 = Color(0xFF5908CF)
val DaisyBush900 = Color(0xFF46089F)
val DaisyBush950 = Color(0xFF2C0174)

// "Bunker" — neutral gray scale
val Bunker50 = Color(0xFFF7F8F8)
val Bunker100 = Color(0xFFEDEDF1)
val Bunker200 = Color(0xFFD7D9E0)
val Bunker300 = Color(0xFFB5B9C4)
val Bunker400 = Color(0xFF8C93A4)
val Bunker500 = Color(0xFF6E7689)
val Bunker600 = Color(0xFF585E71)
val Bunker700 = Color(0xFF484D5C)
val Bunker800 = Color(0xFF3E424E)
val Bunker900 = Color(0xFF373A43)
val Bunker950 = Color(0xFF17181C)

// Alert colors ("Cor de alerta sucesso e erro" in the Figma file)
val AlertSuccess = Color(0xFF068932)
val AlertError = Color(0xFF9F0808)
val AlertWarning = Color(0xFFCDA823)

// --- Semantic aliases used across the app ---
// Kept so existing screen code (written against the old approximate names)
// keeps compiling; they now point at the real tokens above.
val PurplePrimary = DaisyBush800        // Primary buttons, active states, links (#5908CF)
val PurplePrimaryDark = DaisyBush900    // Hero banners, headers (#4B18A3)
val PurpleContainer = DaisyBush50       // Onboarding background, light containers (#F4F1FF)
val PurpleContainerAlt = DaisyBush200
val PurpleMuted = DaisyBush400          // Category chip fill (#A178FF)

val SurfaceWhite = Color(0xFFFFFFFF)
val OnSurfaceDark = Bunker950
val OnSurfaceVariant = Bunker500
val OutlineLight = Bunker200

val ErrorRed = AlertError
val SuccessGreen = AlertSuccess
val WarningYellow = AlertWarning
