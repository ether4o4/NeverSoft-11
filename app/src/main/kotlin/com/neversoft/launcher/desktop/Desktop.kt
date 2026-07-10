package com.neversoft.launcher.desktop

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neversoft.launcher.data.AppSettings
import com.neversoft.launcher.fileexplorer.FileOps
import com.neversoft.launcher.window.WindowContentType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

private data class DesktopShortcut(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val windowType: WindowContentType,
    val windowTitle: String,
    val payload: String? = null,
)

@Composable
fun Desktop(
    onOpenWindow: (WindowContentType, String, String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val shortcuts = remember {
        listOf(
            DesktopShortcut("files", "File Explorer", Icons.Default.Folder, WindowContentType.FILE_EXPLORER, "File Explorer"),
            DesktopShortcut(
                "bin", "Recycle Bin", Icons.Default.Delete, WindowContentType.FILE_EXPLORER, "Recycle Bin",
                payload = FileOps.recycleBinDir().absolutePath,
            ),
            DesktopShortcut("browser", "Browser", Icons.Default.Language, WindowContentType.BROWSER, "Browser"),
            DesktopShortcut("terminal", "Terminal", Icons.Default.Terminal, WindowContentType.TERMINAL, "Terminal"),
        )
    }

    val positions = remember { mutableStateMapOf<String, Offset>() }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val stored = runCatching { JSONObject(AppSettings.desktopIconPositionsFlow(context).first()) }
            .getOrDefault(JSONObject())
        val cell = with(density) { 96.dp.toPx() }
        val margin = with(density) { 24.dp.toPx() }
        shortcuts.forEachIndexed { index, shortcut ->
            val arr = stored.optJSONArray(shortcut.id)
            positions[shortcut.id] = if (arr != null && arr.length() == 2) {
                Offset(arr.optDouble(0, 0.0).toFloat(), arr.optDouble(1, 0.0).toFloat())
            } else {
                Offset(margin, margin + index * cell)
            }
        }
        loaded = true
    }

    fun persistPositions() {
        val json = JSONObject()
        positions.forEach { (id, pos) ->
            json.put(id, JSONArray().put(pos.x.toDouble()).put(pos.y.toDouble()))
        }
        scope.launch { AppSettings.setDesktopIconPositions(context, json.toString()) }
    }

    Box(modifier) {
        if (loaded) {
            shortcuts.forEach { shortcut ->
                key(shortcut.id) {
                    val pos = positions[shortcut.id] ?: Offset.Zero
                    DesktopIcon(
                        icon = shortcut.icon,
                        label = shortcut.label,
                        modifier = Modifier
                            .offset { IntOffset(pos.x.roundToInt(), pos.y.roundToInt()) }
                            .pointerInput(shortcut.id) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        val current = positions[shortcut.id] ?: Offset.Zero
                                        positions[shortcut.id] = Offset(
                                            (current.x + dragAmount.x).coerceAtLeast(0f),
                                            (current.y + dragAmount.y).coerceAtLeast(0f),
                                        )
                                    },
                                    onDragEnd = { persistPositions() },
                                )
                            }
                            .pointerInput(shortcut.id) {
                                detectTapGestures(
                                    onTap = { onOpenWindow(shortcut.windowType, shortcut.windowTitle, shortcut.payload) },
                                )
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun DesktopIcon(icon: ImageVector, label: String, modifier: Modifier) {
    Column(modifier = modifier.width(76.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(36.dp))
        Text(label, color = Color.White, fontSize = 10.sp, maxLines = 2)
    }
}
