package com.neversoft.launcher.taskbar

import android.content.Context
import android.os.BatteryManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.neversoft.launcher.apps.InstalledAppsRepository
import com.neversoft.launcher.data.AppSettings
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.ui.StartLogo
import com.neversoft.launcher.window.ShellWindow
import com.neversoft.launcher.window.WindowContentType
import com.neversoft.launcher.window.WindowState
import com.neversoft.launcher.window.toIcon
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val TASKBAR_HEIGHT_DP = 48

private data class TaskbarApp(val packageName: String, val label: String, val icon: ImageBitmap?)

// Windows 11 taskbar. The app cluster (Start, Search, Task View, pinned
// apps, running windows) centers itself in the space left of the tray;
// the tray is battery + clock. Pins live in DataStore and are managed by
// long-press here and in the Start menu.
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Taskbar(
    onStartClick: () -> Unit,
    onSearchClick: () -> Unit,
    onTaskViewClick: () -> Unit,
    openWindows: List<ShellWindow>,
    focusedWindowId: String?,
    onWindowTaskbarClick: (String) -> Unit,
    onTrayClick: () -> Unit,
    onClockClick: () -> Unit,
    onShowDesktop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var timeText by remember { mutableStateOf(formatTime()) }
    var dateText by remember { mutableStateOf(formatDate()) }
    var batteryPct by remember { mutableIntStateOf(readBattery(context)) }
    LaunchedEffect(Unit) {
        while (true) {
            timeText = formatTime()
            dateText = formatDate()
            batteryPct = readBattery(context)
            kotlinx.coroutines.delay(5_000)
        }
    }

    // Custom Start button image (falls back to the four-pane logo)
    val orbPath by AppSettings.orbImageFlow(context).collectAsState(initial = "")
    val orbBitmap = remember(orbPath) {
        orbPath.takeIf { it.isNotEmpty() && java.io.File(it).exists() }
            ?.let { com.neversoft.launcher.files.ImageStore.decodeSampled(it, 128)?.asImageBitmap() }
    }

    // Taskbar pins, shared with the Start menu via DataStore
    val iconPack by AppSettings.iconPackFlow(context).collectAsState(initial = "")
    val dockPinsJson by AppSettings.dockPinsFlow(context).collectAsState(initial = "[]")
    val pinnedPkgs = remember(dockPinsJson) {
        runCatching {
            val arr = JSONArray(dockPinsJson)
            List(arr.length()) { arr.getString(it) }
        }.getOrDefault(emptyList())
    }
    val pinnedApps = remember(pinnedPkgs, iconPack) {
        val pm = context.packageManager
        pinnedPkgs.mapNotNull { pkg ->
            runCatching {
                val info = pm.getApplicationInfo(pkg, 0)
                TaskbarApp(
                    packageName = pkg,
                    label = pm.getApplicationLabel(info).toString(),
                    icon = InstalledAppsRepository.loadIcon(context, iconPack, pkg)?.asImageBitmap(),
                )
            }.getOrNull()
        }
    }

    fun unpin(pkg: String) {
        scope.launch {
            AppSettings.setDockPins(context, JSONArray(pinnedPkgs - pkg).toString())
        }
    }

    Column(modifier.background(theme.taskbar)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(theme.stroke))
        Row(
            Modifier.fillMaxWidth().height(TASKBAR_HEIGHT_DP.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // App cluster, centered in the space before the tray, capped so
            // it can never spill under the tray
            BoxWithConstraints(Modifier.weight(1f).fillMaxHeight()) {
                val budget = ((maxWidth / 46.dp).toInt() - 3).coerceAtLeast(1)
                val windowsShown = openWindows.take(minOf(3, budget))
                val pinsShown = pinnedApps.take((budget - windowsShown.size).coerceAtLeast(0))
                Row(
                    Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    TaskbarButton(onClick = onStartClick) {
                        if (orbBitmap != null) {
                            // Custom orb fills the button, larger than the stock logo
                            Image(
                                bitmap = orbBitmap,
                                contentDescription = "Start",
                                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(7.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            )
                        } else {
                            StartLogo(22.dp)
                        }
                    }
                    TaskbarButton(onClick = onSearchClick) {
                        Icon(Icons.Outlined.Search, "Search", Modifier.size(22.dp), tint = theme.text)
                    }
                    TaskbarButton(onClick = onTaskViewClick) {
                        TaskViewGlyph(theme.text)
                    }
                    pinsShown.forEach { app ->
                        PinnedTaskbarButton(
                            app = app,
                            onClick = { InstalledAppsRepository.launch(context, app.packageName) },
                            onUnpin = { unpin(app.packageName) },
                        )
                    }
                    windowsShown.forEach { win ->
                        TaskbarButton(
                            onClick = { onWindowTaskbarClick(win.id) },
                            indicator = if (win.id == focusedWindowId && win.state != WindowState.MINIMIZED)
                                TaskbarIndicator.FOCUSED else TaskbarIndicator.RUNNING,
                        ) {
                            Icon(
                                toIcon(win.contentType), win.title, Modifier.size(21.dp),
                                tint = if (win.contentType == WindowContentType.FILE_EXPLORER)
                                    Color(0xFFFFCA28) else theme.text,
                            )
                        }
                    }
                }
            }

            // System tray: battery (opens quick settings) + clock
            Row(
                Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onTrayClick() }
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BatteryGlyph(batteryPct, theme.text)
            }
            Column(
                Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onClockClick() }
                    .padding(horizontal = 7.dp, vertical = 5.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Text(timeText, color = theme.text, fontSize = 12.sp, lineHeight = 15.sp, textAlign = TextAlign.End)
                Text(dateText, color = theme.text, fontSize = 12.sp, lineHeight = 15.sp, textAlign = TextAlign.End)
            }
            // Show desktop sliver
            Box(Modifier.fillMaxHeight().width(10.dp).clickable { onShowDesktop() }) {
                Box(
                    Modifier.align(Alignment.CenterStart).fillMaxHeight()
                        .padding(vertical = 12.dp).width(1.dp).background(theme.divider),
                )
            }
        }
        // Extend the bar's background behind the gesture-nav area
        Box(Modifier.fillMaxWidth().windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

enum class TaskbarIndicator { NONE, RUNNING, FOCUSED }

@Composable
private fun TaskbarButton(
    onClick: () -> Unit,
    indicator: TaskbarIndicator = TaskbarIndicator.NONE,
    content: @Composable () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Box(
        Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        content()
        TaskbarIndicatorBar(indicator, theme.accent, theme.textSecondary)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PinnedTaskbarButton(
    app: TaskbarApp,
    onClick: () -> Unit,
    onUnpin: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    var menuOpen by remember { mutableStateOf(false) }
    Box {
        Box(
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(4.dp))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { menuOpen = true },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (app.icon != null) {
                Image(bitmap = app.icon, contentDescription = app.label, modifier = Modifier.size(24.dp))
            } else {
                Text(app.label.take(1).uppercase(), color = theme.text, fontSize = 14.sp)
            }
        }
        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { menuOpen = false },
            shape = RoundedCornerShape(8.dp),
            containerColor = theme.menuSurface,
        ) {
            Row(
                Modifier
                    .clickable {
                        menuOpen = false
                        onUnpin()
                    }
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.PushPin, null, Modifier.size(15.dp), tint = theme.text)
                Spacer(Modifier.width(10.dp))
                Text("Unpin from taskbar", color = theme.text, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.TaskbarIndicatorBar(
    indicator: TaskbarIndicator,
    accent: Color,
    secondary: Color,
) {
    when (indicator) {
        TaskbarIndicator.FOCUSED -> Box(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 3.dp)
                .size(width = 16.dp, height = 3.dp)
                .background(accent, RoundedCornerShape(2.dp)),
        )
        TaskbarIndicator.RUNNING -> Box(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 3.dp)
                .size(width = 6.dp, height = 3.dp)
                .background(secondary, RoundedCornerShape(2.dp)),
        )
        TaskbarIndicator.NONE -> Unit
    }
}

// Win11 task-view glyph: two overlapping rounded squares
@Composable
private fun TaskViewGlyph(tint: Color) {
    Canvas(Modifier.size(20.dp)) {
        val stroke = Stroke(width = size.width * 0.09f)
        val corner = CornerRadius(size.width * 0.14f)
        drawRoundRect(
            color = tint,
            topLeft = Offset(size.width * 0.22f, 0f),
            size = Size(size.width * 0.78f, size.height * 0.78f),
            cornerRadius = corner,
            style = stroke,
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(0f, size.height * 0.22f),
            size = Size(size.width * 0.78f, size.height * 0.78f),
            cornerRadius = corner,
            style = stroke,
        )
    }
}

// Horizontal Win11-style battery glyph with fill level
@Composable
private fun BatteryGlyph(percent: Int, tint: Color) {
    Canvas(Modifier.size(width = 22.dp, height = 12.dp)) {
        val bodyW = size.width * 0.88f
        val capW = size.width * 0.08f
        val corner = CornerRadius(size.height * 0.25f)
        drawRoundRect(
            color = tint,
            size = Size(bodyW, size.height),
            cornerRadius = corner,
            style = Stroke(width = size.height * 0.12f),
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(bodyW + capW * 0.4f, size.height * 0.3f),
            size = Size(capW, size.height * 0.4f),
            cornerRadius = CornerRadius(capW * 0.5f),
        )
        val pad = size.height * 0.22f
        drawRoundRect(
            color = tint,
            topLeft = Offset(pad, pad),
            size = Size((bodyW - pad * 2f) * (percent.coerceIn(0, 100) / 100f), size.height - pad * 2f),
            cornerRadius = CornerRadius(size.height * 0.12f),
        )
    }
}

private fun formatTime(): String =
    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

private fun formatDate(): String =
    SimpleDateFormat("M/d/yyyy", Locale.getDefault()).format(Date())

private fun readBattery(context: Context): Int {
    val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager ?: return 100
    val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    return if (level in 1..100) level else 100
}
