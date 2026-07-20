package com.neversoft.launcher.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.neversoft.launcher.data.AppSettings
import com.neversoft.launcher.files.ImageStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.min

// The desktop backdrop. Defaults to a procedural recreation of the
// Windows 11 "Bloom" wallpaper (dark and light variants); a user-picked
// photo from Settings > Personalization replaces it everywhere the
// wallpaper appears (desktop, lock screen, setup).
object DesktopWallpaper {
    val DarkBase = Color(0xFF04101F)
    val LightBase = Color(0xFF7EB3E4)
}

@Composable
fun BloomWallpaper(
    isDark: Boolean,
    accent: Color = Color(0xFF4CC2FF),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val customPath by AppSettings.wallpaperImageFlow(context).collectAsState(initial = "")
    val fit by AppSettings.wallpaperFitFlow(context).collectAsState(initial = "crop")
    val base = if (isDark) blendColor(accent, Color.Black, 0.86f)
    else blendColor(accent, Color.White, 0.52f)

    // Decode the custom wallpaper OFF the main thread — decoding a large
    // bitmap during the home/overview transition on the UI thread was a
    // major source of jank and freezes.
    var customBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(customPath) {
        customBitmap = withContext(Dispatchers.IO) {
            customPath.takeIf { it.isNotEmpty() && File(it).exists() }
                ?.let { ImageStore.decodeSampled(it, 1600)?.asImageBitmap() }
        }
    }

    when {
        customBitmap != null -> Image(
            bitmap = customBitmap!!,
            contentDescription = null,
            modifier = modifier.background(base),
            // "exact" maps the image to exactly the screen size — one static
            // page, no cropping and no scroll-parallax oversizing
            contentScale = if (fit == "exact") ContentScale.FillBounds else ContentScale.Crop,
            filterQuality = androidx.compose.ui.graphics.FilterQuality.High,
        )
        // A custom wallpaper is set but still decoding: hold the base color
        // (no procedural-bloom flash before the photo appears)
        customPath.isNotEmpty() -> Box(modifier.fillMaxSize().background(base))
        // Default: procedural, vector-drawn bloom (no bitmap, always crisp),
        // tinted by the active theme's accent so each theme has its own desktop
        else -> Canvas(modifier.background(base)) {
            drawBloom(isDark, accent)
        }
    }
}

// Linear RGB blend toward [other] by fraction [t] (0 = this, 1 = other).
internal fun blendColor(base: Color, other: Color, t: Float): Color = Color(
    red = base.red + (other.red - base.red) * t,
    green = base.green + (other.green - base.green) * t,
    blue = base.blue + (other.blue - base.blue) * t,
    alpha = 1f,
)

// Procedural Windows-11-style "Bloom", tinted by the theme accent. A dark
// theme gets a deep accent backdrop with a bright accent bloom; a light theme
// gets a soft accent-washed sky. Every theme's accent yields a distinct
// desktop (blue Dark, cyan Glass, steel Metallic, ...).
private fun DrawScope.drawBloom(isDark: Boolean, accent: Color) {
    val bgTop = if (isDark) blendColor(accent, Color.Black, 0.90f) else blendColor(accent, Color.White, 0.62f)
    val bgMid = if (isDark) blendColor(accent, Color.Black, 0.82f) else blendColor(accent, Color.White, 0.46f)
    val bgBot = if (isDark) blendColor(accent, Color.Black, 0.70f) else blendColor(accent, Color.White, 0.30f)
    drawRect(Brush.verticalGradient(listOf(bgTop, bgMid, bgBot)))

    val cx = size.width * 0.5f
    val cy = size.height * 0.52f
    val r = min(size.width, size.height) * 0.62f
    val center = Offset(cx, cy)

    // Ambient glow behind the bloom
    val glow = if (isDark) blendColor(accent, Color.White, 0.15f) else Color.White
    drawCircle(
        Brush.radialGradient(
            listOf(glow.copy(alpha = if (isDark) 0.35f else 0.40f), glow.copy(alpha = 0.18f), Color.Transparent),
            center = center, radius = r * 1.5f,
        ),
        radius = r * 1.5f, center = center,
    )

    // Petals: rotated translucent ribbons around the center, tinted per theme
    val bright = blendColor(accent, Color.White, if (isDark) 0.40f else 0.72f)
    val deep = blendColor(accent, if (isDark) Color.Black else Color.White, if (isDark) 0.25f else 0.30f)
    val petalColors = listOf(
        accent.copy(alpha = 0.70f), bright.copy(alpha = 0.55f), deep.copy(alpha = 0.65f),
        bright.copy(alpha = 0.55f), accent.copy(alpha = 0.72f), bright.copy(alpha = 0.55f),
        deep.copy(alpha = 0.65f), bright.copy(alpha = 0.55f),
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
    val core = if (isDark) blendColor(accent, Color.White, 0.65f) else Color.White
    drawCircle(
        Brush.radialGradient(
            listOf(core.copy(alpha = 0.90f), accent.copy(alpha = 0.50f), Color.Transparent),
            center = center, radius = r * 0.34f,
        ),
        radius = r * 0.34f, center = center,
    )

    // Vignette (dark themes) so the taskbar/menus read against the edges
    if (isDark) {
        drawRect(
            Brush.verticalGradient(
                0f to Color(0x66000000), 0.25f to Color.Transparent,
                0.75f to Color.Transparent, 1f to Color(0x73000000),
            ),
        )
    }
}
