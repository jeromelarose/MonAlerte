package org.jelarose.monalerte.core.theme

import androidx.compose.ui.graphics.Color

// Couleurs principales de la marque MonAlerte
val MGA_Blue_Primary = Color(0xFF007BFF)
val MGA_Orange_Accent = Color(0xFFFF8C00)
val MGA_Cyan_Secondary = Color(0xFF00BCD4)
val MGA_White = Color(0xFFFFFFFF)
val MGA_Black = Color(0xFF000000)
val MGA_Light_Blue_Background = Color(0xFFF3F6FF)
val MGA_Dark_Gray_Text = Color(0xFF1A1A1A)
val MGA_Medium_Gray_Text = Color(0xFF4D4D4D)
val MGA_Light_Gray_Borders = Color(0xFFBDBDBD)
val MGA_Alert_Red = Color(0xFFD32F2F)

// Couleurs pour le thème sombre
val MGA_Dark_Background = Color(0xFF121212)
val MGA_Dark_Surface = Color(0xFF1E1E1E)
val MGA_Light_Text_Dark_Theme = Color(0xFFE0E0E0)
val MGA_Medium_Text_Dark_Theme = Color(0xFFA0A0A0)

// --- SCHÉMA DE COULEURS POUR LE THÈME CLAIR ---

// Rôles Primaires
val md_theme_light_primary = MGA_Blue_Primary
val md_theme_light_onPrimary = MGA_White
val md_theme_light_primaryContainer = Color(0xFFCCE5FF)
val md_theme_light_onPrimaryContainer = Color(0xFF001E33)

// Rôles Secondaires
val md_theme_light_secondary = MGA_Cyan_Secondary
val md_theme_light_onSecondary = MGA_Black
val md_theme_light_secondaryContainer = Color(0xFFAAF2FF)
val md_theme_light_onSecondaryContainer = Color(0xFF002025)

// Rôles Tertiaires
val md_theme_light_tertiary = MGA_Orange_Accent
val md_theme_light_onTertiary = MGA_White
val md_theme_light_tertiaryContainer = Color(0xFFFFDDB3)
val md_theme_light_onTertiaryContainer = Color(0xFF2F1500)

// Rôles d'Erreur
val md_theme_light_error = MGA_Alert_Red
val md_theme_light_onError = MGA_White
val md_theme_light_errorContainer = Color(0xFFFFDAD4)
val md_theme_light_onErrorContainer = Color(0xFF410001)

// Rôles de Fond et de Surface
val md_theme_light_background = MGA_Light_Blue_Background
val md_theme_light_onBackground = MGA_Dark_Gray_Text
val md_theme_light_surface = MGA_White
val md_theme_light_onSurface = MGA_Dark_Gray_Text
val md_theme_light_surfaceVariant = Color(0xFFE0E2EC)
val md_theme_light_onSurfaceVariant = MGA_Medium_Gray_Text

// Rôles Divers
val md_theme_light_outline = MGA_Light_Gray_Borders
val md_theme_light_inverseOnSurface = Color(0xFFF1F0F4)
val md_theme_light_inverseSurface = Color(0xFF2F3033)
val md_theme_light_inversePrimary = Color(0xFF9ACBFF)
val md_theme_light_surfaceTint = md_theme_light_primary
val md_theme_light_outlineVariant = Color(0xFFC4C6CF)
val md_theme_light_scrim = Color(0x99000000)

// --- SCHÉMA DE COULEURS POUR LE THÈME SOMBRE ---

val md_theme_dark_primary = Color(0xFF9ACBFF)
val md_theme_dark_onPrimary = Color(0xFF00325A)
val md_theme_dark_primaryContainer = Color(0xFF00497F)
val md_theme_dark_onPrimaryContainer = Color(0xFFD1E7FF)

val md_theme_dark_secondary = Color(0xFF86D7E9)
val md_theme_dark_onSecondary = Color(0xFF00363F)
val md_theme_dark_secondaryContainer = Color(0xFF004F58)
val md_theme_dark_onSecondaryContainer = Color(0xFFA0F2FF)

val md_theme_dark_tertiary = Color(0xFFFFB972)
val md_theme_dark_onTertiary = Color(0xFF492900)
val md_theme_dark_tertiaryContainer = Color(0xFF693C00)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFDDBE)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD4)

val md_theme_dark_background = MGA_Dark_Background
val md_theme_dark_onBackground = MGA_Light_Text_Dark_Theme
val md_theme_dark_surface = MGA_Dark_Surface
val md_theme_dark_onSurface = MGA_Light_Text_Dark_Theme
val md_theme_dark_surfaceVariant = Color(0xFF44474F)
val md_theme_dark_onSurfaceVariant = MGA_Medium_Text_Dark_Theme

val md_theme_dark_outline = Color(0xFF8E9099)
val md_theme_dark_inverseOnSurface = Color(0xFF1C1B1F)
val md_theme_dark_inverseSurface = Color(0xFFE6E1E5)
val md_theme_dark_inversePrimary = MGA_Blue_Primary
val md_theme_dark_surfaceTint = md_theme_dark_primary
val md_theme_dark_outlineVariant = Color(0xFF44474F)
val md_theme_dark_scrim = Color(0x99000000)