package com.neversoft.launcher.window

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.*
import com.neversoft.launcher.fileexplorer.FileExplorerWindow
import com.neversoft.launcher.theme.LocalLauncherTheme
import kotlin.math.roundToInt

@Composable
fun ShellWindowHost(engine: WindowManagerEngine, modifier: Modifier = Modifier) {
    Box(modifier) {
        engine.windows.filter { it.state != WindowState.MINIMIZED }
            .sortedBy { it.zIndex }
            .forEach { window ->
                key(window.id) {
                    ShellWindowCard(
                        window = window,
                        onFocus = { engine.reorderToFront(window.id) },
                        onMove = { delta -> engine.moveWindow(window.id,
                            Offset(window.position.x + delta.x, window.position.y + delta.y)) },
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

@Composable
fun ShellWindowCard(
    window: ShellWindow,
    onFocus: () -> Unit, onMove: (Offset) -> Unit,
    onMinimize: () -> Unit, onMaximizeRestore: () -> Unit, onClose: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    val isMaximized = window.state == WindowState.MAXIMIZED
    val offsetMod = if (isMaximized) Modifier.fillMaxSize() else
        Modifier.offset { IntOffset(window.position.x.roundToInt(), window.position.y.roundToInt()) }
            .size(window.size)

    Card(
        modifier = offsetMod.zIndex(window.zIndex.toFloat())
            .pointerInput(Unit) { detectTapGestures { onFocus() } }
            .shadow(8.dp, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(Modifier.fillMaxSize().background(theme.surfaceBrush())) {
            Row(
                modifier = Modifier.fillMaxWidth().height(36.dp)
                    .background(theme.accentColor.copy(alpha = 0.18f))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { onFocus() },
                            onDrag = { _, drag -> if (!isMaximized) onMove(drag) }
                        )
                    }.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(window.contentType.toIcon(), contentDescription = null,
                    modifier = Modifier.size(14.dp), tint = theme.onSurface)
                Spacer(Modifier.width(6.dp))
                Text(window.title, style = MaterialTheme.typography.labelMedium,
                    color = theme.onSurface, modifier = Modifier.weight(1f))
                TextButton(onClick = onMinimize, contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(28.dp)) {
                    Text("—", color = theme.onSurface, fontSize = 12.sp) }
                TextButton(onClick = onMaximizeRestore, contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(28.dp)) {
                    Text(if (isMaximized) "❐" else "□", color = theme.onSurface, fontSize = 12.sp) }
                TextButton(onClick = onClose, contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(28.dp)) {
                    Text("✕", color = Color(0xFFFF4444), fontSize = 12.sp) }
            }
            Box(Modifier.fillMaxSize()) { WindowContent(window) }
        }
    }
}

@Composable
fun WindowContent(window: ShellWindow) {
    when (window.contentType) {
        WindowContentType.FILE_EXPLORER -> FileExplorerWindow()
        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(window.title, color = LocalLauncherTheme.current.onSurface.copy(alpha = 0.5f))
        }
    }
}

fun WindowContentType.toIcon(): ImageVector = when (this) {
    WindowContentType.FILE_EXPLORER  -> Icons.Default.Folder
    WindowContentType.CONTROL_PANEL -> Icons.Default.Settings
    WindowContentType.BROWSER       -> Icons.Default.Language
    WindowContentType.TERMINAL      -> Icons.Default.Terminal
    else                             -> Icons.Default.WebAsset
}
