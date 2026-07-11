package com.neversoft.launcher.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// Windows 11 Fluent design tokens. Two modes, exactly like Windows 11:
// Dark (default) and Light. Accent is the Windows-blue default.
enum class ThemePreset { DARK, LIGHT }

data class LauncherTheme(
    val preset: ThemePreset,
    val isDark: Boolean,
    // Interactive accent (dark mode uses the lightened accent, light the deep one)
    val accent: Color,
    val accentText: Color,
    // Surfaces
    val taskbar: Color,        // Mica Alt — translucent bar over wallpaper
    val menuSurface: Color,    // Start menu / flyout acrylic
    val windowSurface: Color,  // app window body (Mica)
    val card: Color,           // layer cards inside windows
    val cardPressed: Color,
    val inputField: Color,     // text boxes / search field
    // Strokes and text
    val stroke: Color,         // 1px surface borders
    val divider: Color,
    val text: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    val hover: Color,          // subtle fill for pressed/hovered items
)

object LauncherThemes {
    val Dark = LauncherTheme(
        preset = ThemePreset.DARK,
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

    val all = listOf(Dark, Light)

    fun fromPreset(preset: ThemePreset): LauncherTheme = all.first { it.preset == preset }
}

val LocalLauncherTheme = compositionLocalOf { LauncherThemes.Dark }
