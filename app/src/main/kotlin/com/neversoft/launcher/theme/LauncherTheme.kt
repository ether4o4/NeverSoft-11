package com.neversoft.launcher.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

enum class ThemePreset { GLASS, MACBOOK, RED, PURPLE, BLUE, GREEN, BLACK, DARK_GREY, LIGHT_GREY, WHITE }

data class LauncherTheme(
    val preset: ThemePreset,
    val displayName: String,
    val surfaceColor: Color,
    val surfaceAlpha: Float,
    val accentColor: Color,
    val onSurface: Color,
    val onAccent: Color,
    val blurRadius: Float,
) {
    fun surfaceBrush(): Brush = Brush.linearGradient(
        colors = listOf(
            surfaceColor.copy(alpha = surfaceAlpha),
            surfaceColor.copy(alpha = surfaceAlpha * 0.85f)
        )
    )

    /**
     * Fully opaque surface for windows and the start menu: the translucent
     * theme tint composited over a dark base so nothing behind bleeds
     * through, finished with a subtle top-lit vertical gradient so it still
     * reads as glass.
     */
    fun windowBrush(): Brush {
        val base = surfaceColor.copy(alpha = surfaceAlpha).compositeOver(WindowBase)
        return Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.10f).compositeOver(base),
                base,
                Color.Black.copy(alpha = 0.12f).compositeOver(base),
            ),
        )
    }

    /** Opaque flat color matching the middle of [windowBrush], for sub-panels. */
    fun windowSolid(): Color = surfaceColor.copy(alpha = surfaceAlpha).compositeOver(WindowBase)

    private companion object {
        val WindowBase = Color(0xFF151C28)
    }
}

object LauncherThemes {
    val Glass     = LauncherTheme(ThemePreset.GLASS,      "Glass",      Color.White,          0.12f, Color.White,          Color.White,          Color.Black, 20f)
    val MacBook   = LauncherTheme(ThemePreset.MACBOOK,    "MacBook",    Color(0xFFECECF1),    0.92f, Color(0xFF0071E3),    Color(0xFF1D1D1F),    Color.White, 16f)
    val Red       = LauncherTheme(ThemePreset.RED,        "Red",        Color(0xFFB41414),    0.85f, Color(0xFFFF4444),    Color.White,          Color.White, 18f)
    val Purple    = LauncherTheme(ThemePreset.PURPLE,     "Purple",     Color(0xFF5014A0),    0.85f, Color(0xFFA855F7),    Color.White,          Color.White, 18f)
    val Blue      = LauncherTheme(ThemePreset.BLUE,       "Blue",       Color(0xFF143CB4),    0.85f, Color(0xFF3B82F6),    Color.White,          Color.White, 18f)
    val Green     = LauncherTheme(ThemePreset.GREEN,      "Green",      Color(0xFF14783C),    0.85f, Color(0xFF22C55E),    Color.White,          Color.White, 18f)
    val Black     = LauncherTheme(ThemePreset.BLACK,      "Black",      Color(0xFF0A0A0A),    0.95f, Color.White,          Color.White,          Color.Black, 20f)
    val DarkGrey  = LauncherTheme(ThemePreset.DARK_GREY,  "Dark Grey",  Color(0xFF282828),    0.92f, Color(0xFF888888),    Color.White,          Color.White, 18f)
    val LightGrey = LauncherTheme(ThemePreset.LIGHT_GREY, "Light Grey", Color(0xFFDCDCDC),    0.92f, Color(0xFF555555),    Color(0xFF111111),    Color.White, 16f)
    val White     = LauncherTheme(ThemePreset.WHITE,      "White",      Color.White,          0.95f, Color(0xFF0071E3),    Color(0xFF111111),    Color.White, 16f)

    val all = listOf(Glass, MacBook, Red, Purple, Blue, Green, Black, DarkGrey, LightGrey, White)

    fun fromPreset(preset: ThemePreset): LauncherTheme = all.first { it.preset == preset }
}

val LocalLauncherTheme = compositionLocalOf { LauncherThemes.Glass }
