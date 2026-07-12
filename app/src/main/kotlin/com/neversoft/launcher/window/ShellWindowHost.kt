package com.neversoft.launcher.window

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.neversoft.launcher.controlpanel.SettingsWindow
import com.neversoft.launcher.fileexplorer.FileExplorerWindow
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.theme.ThemePreset
import kotlin.math.roundToInt

@Composable
fun ShellWindowHost(
    engine: WindowManagerEngine,
    selectedPreset: ThemePreset,
    onSelectPreset: (ThemePreset) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        engine.windows.filter { it.state != WindowState.MINIMIZED }
            .sortedBy { it.zIndex }
            .forEach { window ->
                key(window.id) {
                    ShellWindowCard(
                        window = window,
                        selectedPreset = selectedPreset,
                        onSelectPreset = onSelectPreset,
                        onFocus = { engine.reorderToFront(window.id) },
                        onMove = { delta ->
                            engine.moveWindow(
                                window.id,
                                Offset(
                                    (window.position.x + delta.x).coerceAtLeast(0f),
                                    (window.position.y + delta.y).coerceAtLeast(0f),
                                ),
                            )
                        },
                        onResize = { newSize -> engine.resizeWindow(window.id, newSize) },
                        onMinimize = { engine.toggleMinimize(window.id) },
                        onMaximizeRestore = {
                            if (window.state == WindowState.MAXIMIZED) engine.restoreWindow(window.id)
                            else engine.maximizeWindow(window.id)
                        },
                        onClose = { engine.closeWindow(window.id) },
                    )
                }
            }
    }
}

// Windows 11 window: Mica surface, 8dp corners, 1px stroke,
// title bar with left icon+title and flat caption buttons.
@Composable
fun ShellWindowCard(
    window: ShellWindow,
    selectedPreset: ThemePreset,
    onSelectPreset: (ThemePreset) -> Unit,
    onFocus: () -> Unit,
    onMove: (Offset) -> Unit,
    onResize: (DpSize) -> Unit,
    onMinimize: () -> Unit,
    onMaximizeRestore: () -> Unit,
    onClose: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    val density = LocalDensity.current
    val isMaximized = window.state == WindowState.MAXIMIZED
    val corner = if (isMaximized) 0.dp else 8.dp
    val shape = RoundedCornerShape(corner)

    // Win11-style open animation: quick scale + fade in
    val spawn = androidx.compose.runtime.remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        spawn.animateTo(
            1f,
            androidx.compose.animation.core.tween(150, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        )
    }

    val frameModifier = if (isMaximized) {
        Modifier.fillMaxSize()
    } else {
        Modifier
            .offset { IntOffset(window.position.x.roundToInt(), window.position.y.roundToInt()) }
            .size(window.size)
    }

    Box(
        frameModifier
            .zIndex(window.zIndex.toFloat())
            .graphicsLayer {
                val s = 0.94f + 0.06f * spawn.value
                scaleX = s
                scaleY = s
                alpha = spawn.value
            }
            .shadow(if (isMaximized) 0.dp else 18.dp, shape)
            .clip(shape)
            .background(theme.windowSurface)
            .border(1.dp, theme.stroke, shape)
            .pointerInput(Unit) { detectTapGestures { onFocus() } },
    ) {
        Column(Modifier.fillMaxSize()) {
            // Title bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .pointerInput(isMaximized) {
                        detectDragGestures(
                            onDragStart = { onFocus() },
                            onDrag = { change, drag ->
                                change.consume()
                                if (!isMaximized) onMove(drag)
                            },
                        )
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.width(14.dp))
                Icon(
                    toIcon(window.contentType),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = if (window.contentType == WindowContentType.FILE_EXPLORER)
                        Color(0xFFFFCA28) else theme.text,
                )
                Spacer(Modifier.width(9.dp))
                Text(
                    window.title,
                    fontSize = 12.sp,
                    color = theme.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                CaptionButton(onClick = onMinimize, contentDescription = "Minimize") { color ->
                    drawLine(
                        color,
                        Offset(size.width * 0.5f - 5.dp.toPx(), size.height * 0.5f),
                        Offset(size.width * 0.5f + 5.dp.toPx(), size.height * 0.5f),
                        strokeWidth = 1.2.dp.toPx(),
                    )
                }
                CaptionButton(
                    onClick = onMaximizeRestore,
                    contentDescription = if (isMaximized) "Restore" else "Maximize",
                ) { color ->
                    val s = 9.dp.toPx()
                    val stroke = Stroke(1.2.dp.toPx())
                    val r = androidx.compose.ui.geometry.CornerRadius(1.6.dp.toPx())
                    if (isMaximized) {
                        // Restore: two overlapping panes
                        val off = 2.4.dp.toPx()
                        drawRoundRect(
                            color,
                            topLeft = Offset((size.width - s) / 2f + off, (size.height - s) / 2f - off),
                            size = androidx.compose.ui.geometry.Size(s - off, s - off),
                            cornerRadius = r, style = stroke,
                        )
                        drawRoundRect(
                            color,
                            topLeft = Offset((size.width - s) / 2f, (size.height - s) / 2f + off * 0.4f),
                            size = androidx.compose.ui.geometry.Size(s - off, s - off),
                            cornerRadius = r, style = stroke,
                        )
                    } else {
                        drawRoundRect(
                            color,
                            topLeft = Offset((size.width - s) / 2f, (size.height - s) / 2f),
                            size = androidx.compose.ui.geometry.Size(s, s),
                            cornerRadius = r, style = stroke,
                        )
                    }
                }
                CaptionButton(onClick = onClose, contentDescription = "Close", isClose = true) { color ->
                    val s = 4.6.dp.toPx()
                    val cx = size.width * 0.5f
                    val cy = size.height * 0.5f
                    drawLine(color, Offset(cx - s, cy - s), Offset(cx + s, cy + s), 1.2.dp.toPx())
                    drawLine(color, Offset(cx - s, cy + s), Offset(cx + s, cy - s), 1.2.dp.toPx())
                }
            }

            Box(Modifier.fillMaxSize().weight(1f)) {
                WindowContent(
                    window = window,
                    selectedPreset = selectedPreset,
                    onSelectPreset = onSelectPreset,
                )
            }
        }

        // Resize grip, bottom-right, with a visible Win11-style hatch
        if (!isMaximized) {
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .pointerInput(window.id) {
                        detectDragGestures(
                            onDragStart = { onFocus() },
                            onDrag = { change, drag ->
                                change.consume()
                                with(density) {
                                    onResize(
                                        DpSize(
                                            window.size.width + drag.x.toDp(),
                                            window.size.height + drag.y.toDp(),
                                        ),
                                    )
                                }
                            },
                        )
                    },
            ) {
                Canvas(
                    Modifier.align(Alignment.BottomEnd).padding(bottom = 5.dp, end = 5.dp).size(10.dp),
                ) {
                    val stroke = 1.2.dp.toPx()
                    val c = theme.textSecondary.copy(alpha = 0.55f)
                    drawLine(c, Offset(size.width, 0f), Offset(0f, size.height), stroke)
                    drawLine(c, Offset(size.width, size.height * 0.5f), Offset(size.width * 0.5f, size.height), stroke)
                }
            }
        }
    }
}

@Composable
private fun CaptionButton(
    onClick: () -> Unit,
    contentDescription: String,
    isClose: Boolean = false,
    glyph: androidx.compose.ui.graphics.drawscope.DrawScope.(Color) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Box(
        Modifier
            .width(44.dp)
            .fillMaxHeight()
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        val color = if (isClose) theme.text else theme.text
        Canvas(Modifier.fillMaxSize()) { glyph(color) }
    }
}

@Composable
fun WindowContent(
    window: ShellWindow,
    selectedPreset: ThemePreset,
    onSelectPreset: (ThemePreset) -> Unit,
) {
    when (window.contentType) {
        WindowContentType.FILE_EXPLORER -> FileExplorerWindow(
            initialPath = window.param,
            windowTitle = window.title,
        )
        WindowContentType.SETTINGS -> SettingsWindow(
            selectedPreset = selectedPreset,
            onSelectPreset = onSelectPreset,
        )
        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                window.title,
                color = LocalLauncherTheme.current.textSecondary,
                fontSize = 13.sp,
            )
        }
    }
}
