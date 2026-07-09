package com.neversoft.launcher.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Opaque desktop backdrop drawn beneath every screen. Translucent theme
// surfaces (Glass etc.) assume this dark base — never render them on the
// bare window background.
object DesktopWallpaper {
    val Top = Color(0xFF0D1B2A)
    val Mid = Color(0xFF16324F)
    val Bottom = Color(0xFF1B4965)

    val brush: Brush = Brush.verticalGradient(listOf(Top, Mid, Bottom))
}
