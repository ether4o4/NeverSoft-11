package com.neversoft.launcher.window

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import com.neversoft.launcher.controlpanel.ControlPanelWindow
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
                        engine = engine,
                        selectedPreset = selectedPreset,
                        onSelectPreset = onSelectPreset,
                    )
                }
            }
    }
}

@Composable
fun ShellWindowCard(
    window: ShellWindow,
    engine: WindowManagerEngine,
    selectedPreset: ThemePreset,
    onSelectPreset: (ThemePreset) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    val density = LocalDensity.current
    val isMaximized = window.state == WindowState.MAXIMIZED
    // Gesture callbacks below resolve the window by id INSIDE the engine at
    // event time. pointerInput coroutines outlive recompositions, so they
    // must never capture this composition's `window` snapshot for positions.
    val windowId = window.id
    val offsetMod = if (isMaximized) {
        Modifier.fillMaxSize()
    } else {
        Modifier.offset { IntOffset(window.position.x.roundToInt(), window.position.y.roundToInt()) }
            .size(window.size)
    }

    Card(
        modifier = offsetMod
            .zIndex(window.zIndex.toFloat())
            .pointerInput(windowId) { detectTapGestures { engine.focusWindow(windowId) } }
            .shadow(8.dp, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(Modifier.fillMaxSize().background(theme.windowBrush())) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .background(theme.accentColor.copy(alpha = 0.18f))
                        .pointerInput(windowId) {
                            detectDragGestures(
                                onDragStart = { engine.focusWindow(windowId) },
                                onDrag = { change, drag ->
                                    change.consume()
                                    engine.dragBy(windowId, drag)
                                },
                            )
                        }
                        .pointerInput(windowId) {
                            detectTapGestures(
                                onDoubleTap = {
                                    val w = engine.windows.find { it.id == windowId } ?: return@detectTapGestures
                                    if (w.state == WindowState.MAXIMIZED) engine.restoreWindow(windowId)
                                    else engine.maximizeWindow(windowId)
                                },
                            )
                        }
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        window.contentType.toIcon(),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = theme.onSurface,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        window.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = theme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        onClick = { engine.toggleMinimize(windowId) },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(28.dp),
                    ) {
                        Text("—", color = theme.onSurface, fontSize = 12.sp)
                    }
                    TextButton(
                        onClick = {
                            if (window.state == WindowState.MAXIMIZED) engine.restoreWindow(windowId)
                            else engine.maximizeWindow(windowId)
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(28.dp),
                    ) {
                        Text(if (isMaximized) "❐" else "□", color = theme.onSurface, fontSize = 12.sp)
                    }
                    TextButton(
                        onClick = { engine.closeWindow(windowId) },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(28.dp),
                    ) {
                        Text("✕", color = Color(0xFFFF4444), fontSize = 12.sp)
                    }
                }
                Box(Modifier.fillMaxSize()) {
                    WindowContent(
                        window = window,
                        selectedPreset = selectedPreset,
                        onSelectPreset = onSelectPreset,
                    )
                }
            }

            // Resize grip — bottom-right corner, hidden when maximized.
            if (!isMaximized) {
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(26.dp)
                        .pointerInput(windowId) {
                            detectDragGestures(
                                onDragStart = { engine.focusWindow(windowId) },
                                onDrag = { change, drag ->
                                    change.consume()
                                    with(density) {
                                        engine.resizeBy(windowId, drag.x.toDp(), drag.y.toDp())
                                    }
                                },
                            )
                        },
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    Text(
                        "◢",
                        color = theme.onSurface.copy(alpha = 0.35f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(end = 4.dp, bottom = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun WindowContent(
    window: ShellWindow,
    selectedPreset: ThemePreset,
    onSelectPreset: (ThemePreset) -> Unit,
) {
    when (window.contentType) {
        WindowContentType.FILE_EXPLORER -> FileExplorerWindow(initialPath = window.payload)
        WindowContentType.CONTROL_PANEL -> ControlPanelWindow(
            selectedPreset = selectedPreset,
            onSelectPreset = onSelectPreset,
        )

        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(window.title, color = LocalLauncherTheme.current.onSurface.copy(alpha = 0.5f))
        }
    }
}

fun WindowContentType.toIcon(): ImageVector = when (this) {
    WindowContentType.FILE_EXPLORER -> Icons.Default.Folder
    WindowContentType.CONTROL_PANEL -> Icons.Default.Settings
    WindowContentType.BROWSER -> Icons.Default.Language
    WindowContentType.TERMINAL -> Icons.Default.Terminal
    else -> Icons.Default.WebAsset
}
