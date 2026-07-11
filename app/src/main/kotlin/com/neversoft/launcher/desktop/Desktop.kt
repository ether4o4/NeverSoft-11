package com.neversoft.launcher.desktop

import android.content.Intent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neversoft.launcher.data.AppSettings
import com.neversoft.launcher.files.Trash
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
    val iconTint: Color?,
    val action: DesktopAction,
)

private sealed interface DesktopAction {
    data class OpenWindow(val type: WindowContentType, val title: String, val param: String? = null) : DesktopAction
    data object OpenBrowser : DesktopAction
}

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
            DesktopShortcut(
                "bin", "Recycle Bin", Icons.Outlined.Delete, null,
                DesktopAction.OpenWindow(WindowContentType.FILE_EXPLORER, "Recycle Bin", Trash.dir().absolutePath),
            ),
            DesktopShortcut(
                "files", "File Explorer", Icons.Filled.Folder, Color(0xFFFFCA28),
                DesktopAction.OpenWindow(WindowContentType.FILE_EXPLORER, "File Explorer"),
            ),
            DesktopShortcut(
                "browser", "Browser", Icons.Outlined.Language, Color(0xFF6EC6F5),
                DesktopAction.OpenBrowser,
            ),
            DesktopShortcut(
                "thispc", "This PC", Icons.Outlined.Computer, null,
                DesktopAction.OpenWindow(WindowContentType.FILE_EXPLORER, "This PC", "/storage"),
            ),
        )
    }

    val cellPx = with(density) { 92.dp.toPx() }
    val marginPx = with(density) { 12.dp.toPx() }
    val positions = remember { mutableStateMapOf<String, Offset>() }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val stored = runCatching { JSONObject(AppSettings.desktopIconPositionsFlow(context).first()) }
            .getOrDefault(JSONObject())
        shortcuts.forEachIndexed { index, shortcut ->
            val arr = stored.optJSONArray(shortcut.id)
            positions[shortcut.id] = if (arr != null && arr.length() == 2) {
                Offset(arr.optDouble(0, 0.0).toFloat(), arr.optDouble(1, 0.0).toFloat())
            } else {
                Offset(marginPx, marginPx + index * cellPx)
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

    fun activate(shortcut: DesktopShortcut) {
        when (val action = shortcut.action) {
            is DesktopAction.OpenWindow -> onOpenWindow(action.type, action.title, action.param)
            DesktopAction.OpenBrowser -> {
                val browser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                runCatching { context.startActivity(browser) }
            }
        }
    }

    Box(modifier) {
        if (loaded) {
            shortcuts.forEach { shortcut ->
                key(shortcut.id) {
                    val pos = positions[shortcut.id] ?: Offset.Zero
                    DesktopIcon(
                        icon = shortcut.icon,
                        iconTint = shortcut.iconTint,
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
                                    onDragEnd = {
                                        // Snap to the desktop grid like Windows auto-align
                                        val current = positions[shortcut.id] ?: Offset.Zero
                                        positions[shortcut.id] = Offset(
                                            marginPx + ((current.x - marginPx) / cellPx).roundToInt()
                                                .coerceAtLeast(0) * cellPx,
                                            marginPx + ((current.y - marginPx) / cellPx).roundToInt()
                                                .coerceAtLeast(0) * cellPx,
                                        )
                                        persistPositions()
                                    },
                                )
                            }
                            .pointerInput(shortcut.id) {
                                detectTapGestures(onTap = { activate(shortcut) })
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun DesktopIcon(icon: ImageVector, iconTint: Color?, label: String, modifier: Modifier) {
    Column(modifier = modifier.width(80.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = iconTint ?: Color.White, modifier = Modifier.size(38.dp))
        Spacer(Modifier.height(3.dp))
        Text(
            label,
            style = TextStyle(
                color = Color.White,
                fontSize = 11.sp,
                shadow = Shadow(Color(0xB3000000), blurRadius = 4f),
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}
