package com.neversoft.launcher.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// Shared shell theme: the taskbar, Start menu, flyouts, and every window
// read the same token set, so a preset recolors the whole OS at once.
enum class ThemePreset { DARK, LIGHT, MACBOOK, GLASS, METALLIC }

data class LauncherTheme(
    val preset: ThemePreset,
    val displayName: String,
    val isDark: Boolean, // drives wallpaper variant + light/dark treatment
    val accent: Color,
    val accentText: Color,
    // Surfaces
    val taskbar: Color,        // bar over wallpaper
    val menuSurface: Color,    // Start menu / flyout acrylic
    val windowSurface: Color,  // app window body
    val card: Color,           // layer cards inside windows
    val cardPressed: Color,
    val inputField: Color,     // text boxes / search field
    // Strokes and text
    val stroke: Color,
    val divider: Color,
    val text: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    val hover: Color,
)

object LauncherThemes {
    val Dark = LauncherTheme(
        preset = ThemePreset.DARK,
        displayName = "Dark",
        isDark = true,
        accent = Color(0xFF4CC2FF),
        accentText = Color(0xFF003048),
        taskbar = Color(0xE8202020),
        menuSurface = Color(0xF5242424),
        windowSurface = Color(0xFF202020),
        card = Color(0xFF2D2D2D),
        cardPressed = Color(0xFF383838),
        inputField = Color(0xFF2D2D2D),
        stroke = Color(0x17FFFFFF),
        divider = Color(0x15FFFFFF),
        text = Color(0xFFFFFFFF),
        textSecondary = Color(0xC9FFFFFF),
        textDisabled = Color(0x5EFFFFFF),
        hover = Color(0x12FFFFFF),
    )

    val Light = LauncherTheme(
        preset = ThemePreset.LIGHT,
        displayName = "Light",
        isDark = false,
        accent = Color(0xFF005FB8),
        accentText = Color(0xFFFFFFFF),
        taskbar = Color(0xE8F3F3F3),
        menuSurface = Color(0xF5F3F3F3),
        windowSurface = Color(0xFFF3F3F3),
        card = Color(0xFFFBFBFB),
        cardPressed = Color(0xFFF0F0F0),
        inputField = Color(0xFFFFFFFF),
        stroke = Color(0x12000000),
        divider = Color(0x14000000),
        text = Color(0xE4000000),
        textSecondary = Color(0x9B000000),
        textDisabled = Color(0x5C000000),
        hover = Color(0x0D000000),
    )

    // Warm silver surfaces with the Apple-blue accent
    val MacBook = LauncherTheme(
        preset = ThemePreset.MACBOOK,
        displayName = "MacBook",
        isDark = false,
        accent = Color(0xFF0071E3),
        accentText = Color(0xFFFFFFFF),
        taskbar = Color(0xF0ECECF1),
        menuSurface = Color(0xF5F0F0F5),
        windowSurface = Color(0xFFECECF1),
        card = Color(0xFFF8F8FB),
        cardPressed = Color(0xFFE2E2E9),
        inputField = Color(0xFFFFFFFF),
        stroke = Color(0x14000000),
        divider = Color(0x12000000),
        text = Color(0xE6000000),
        textSecondary = Color(0x99000000),
        textDisabled = Color(0x59000000),
        hover = Color(0x0D000000),
    )

    // Frosted dark glass: translucent surfaces over the bloom
    val Glass = LauncherTheme(
        preset = ThemePreset.GLASS,
        displayName = "Glass",
        isDark = true,
        accent = Color(0xFF6FD1FF),
        accentText = Color(0xFF06283D),
        taskbar = Color(0x66101826),
        menuSurface = Color(0x8C101E30),
        windowSurface = Color(0xE6141F2E),
        card = Color(0x33FFFFFF),
        cardPressed = Color(0x4DFFFFFF),
        inputField = Color(0x2EFFFFFF),
        stroke = Color(0x40FFFFFF),
        divider = Color(0x2EFFFFFF),
        text = Color(0xFFFFFFFF),
        textSecondary = Color(0xC9FFFFFF),
        textDisabled = Color(0x66FFFFFF),
        hover = Color(0x1FFFFFFF),
    )

    // Brushed-steel silvers with a gunmetal accent
    val Metallic = LauncherTheme(
        preset = ThemePreset.METALLIC,
        displayName = "Metallic",
        isDark = false,
        accent = Color(0xFF46596B),
        accentText = Color(0xFFFFFFFF),
        taskbar = Color(0xF0C6CAD1),
        menuSurface = Color(0xF5CDD1D8),
        windowSurface = Color(0xFFC9CDD4),
        card = Color(0xFFDADDE3),
        cardPressed = Color(0xFFBEC3CB),
        inputField = Color(0xFFE8EAEE),
        stroke = Color(0x26000000),
        divider = Color(0x21000000),
        text = Color(0xF014161A),
        textSecondary = Color(0xA6000000),
        textDisabled = Color(0x59000000),
        hover = Color(0x14000000),
    )

    val all = listOf(Dark, Light, MacBook, Glass, Metallic)

    fun fromPreset(preset: ThemePreset): LauncherTheme = all.first { it.preset == preset }
}

val LocalLauncherTheme = compositionLocalOf { LauncherThemes.Dark }
