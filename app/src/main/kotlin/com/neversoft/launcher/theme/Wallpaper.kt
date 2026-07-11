package com.neversoft.launcher.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.min

// Procedural recreation of the Windows 11 "Bloom" wallpaper: a ribbon
// flower of soft blue petals over a deep navy backdrop. Dark and light
// variants follow the OS wallpaper pair.
object DesktopWallpaper {
    val DarkBase = Color(0xFF04101F)
    val LightBase = Color(0xFF7EB3E4)
}

@Composable
fun BloomWallpaper(isDark: Boolean, modifier: Modifier = Modifier) {
    val base = if (isDark) DesktopWallpaper.DarkBase else DesktopWallpaper.LightBase
    Canvas(modifier.background(base)) {
        if (isDark) drawDarkBloom() else drawLightBloom()
    }
}

private fun DrawScope.drawDarkBloom() {
    // Backdrop: deep navy with a faint blue glow low-center
    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF030B18), Color(0xFF071A33), Color(0xFF0A2547)),
        ),
    )
    val cx = size.width * 0.5f
    val cy = size.height * 0.52f
    val r = min(size.width, size.height) * 0.62f
    val center = Offset(cx, cy)

    // Ambient glow behind the bloom
    drawCircle(
        Brush.radialGradient(
            listOf(Color(0x59306ACC), Color(0x2E1E4E9E), Color.Transparent),
            center = center, radius = r * 1.5f,
        ),
        radius = r * 1.5f, center = center,
    )

    // Petals: rotated translucent ribbons around the center
    val petalColors = listOf(
        Color(0xB3245BC2), Color(0x8C3574F0), Color(0xA61D4FB0),
        Color(0x8C4A8AF5), Color(0xB32E66D6), Color(0x8C6EA8FE),
        Color(0xA6234FA8), Color(0x8C58C4F6),
    )
    petalColors.forEachIndexed { i, color ->
        rotate(degrees = i * 45f + 12f, pivot = center) {
            val petalW = r * 0.62f
            val petalH = r * 1.28f
            drawOval(
                Brush.radialGradient(
                    listOf(color, color.copy(alpha = color.alpha * 0.35f), Color.Transparent),
                    center = Offset(cx, cy - petalH * 0.42f),
                    radius = petalH * 0.72f,
                ),
                topLeft = Offset(cx - petalW / 2f, cy - petalH),
                size = Size(petalW, petalH),
            )
        }
    }

    // Bright core
    drawCircle(
        Brush.radialGradient(
            listOf(Color(0xE68FC1FF), Color(0x804A8AF5), Color.Transparent),
            center = center, radius = r * 0.34f,
        ),
        radius = r * 0.34f, center = center,
    )

    // Vignette so the taskbar/menus read against the edges
    drawRect(
        Brush.verticalGradient(
            0f to Color(0x66000000), 0.25f to Color.Transparent,
            0.75f to Color.Transparent, 1f to Color(0x73000000),
        ),
    )
}

private fun DrawScope.drawLightBloom() {
    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF9CC7EE), Color(0xFF6FA8DF), Color(0xFF4E88C7)),
        ),
    )
    val cx = size.width * 0.5f
    val cy = size.height * 0.52f
    val r = min(size.width, size.height) * 0.62f
    val center = Offset(cx, cy)

    drawCircle(
        Brush.radialGradient(
            listOf(Color(0x66FFFFFF), Color(0x33DCEBFA), Color.Transparent),
            center = center, radius = r * 1.5f,
        ),
        radius = r * 1.5f, center = center,
    )

    val petalColors = listOf(
        Color(0xB3E8F2FC), Color(0x8CBBD9F4), Color(0xA6FFFFFF),
        Color(0x8CA5CDF0), Color(0xB3D3E7F9), Color(0x8CEFF6FD),
        Color(0xA6C3DDF5), Color(0x8CFFFFFF),
    )
    petalColors.forEachIndexed { i, color ->
        rotate(degrees = i * 45f + 12f, pivot = center) {
            val petalW = r * 0.62f
            val petalH = r * 1.28f
            drawOval(
                Brush.radialGradient(
                    listOf(color, color.copy(alpha = color.alpha * 0.35f), Color.Transparent),
                    center = Offset(cx, cy - petalH * 0.42f),
                    radius = petalH * 0.72f,
                ),
                topLeft = Offset(cx - petalW / 2f, cy - petalH),
                size = Size(petalW, petalH),
            )
        }
    }

    drawCircle(
        Brush.radialGradient(
            listOf(Color(0xF2FFFFFF), Color(0x80D8EAFB), Color.Transparent),
            center = center, radius = r * 0.34f,
        ),
        radius = r * 0.34f, center = center,
    )
}
