package com.neversoft.launcher.taskbar

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SignalCellularAlt
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.ui.StartLogo
import com.neversoft.launcher.window.ShellWindow
import com.neversoft.launcher.window.WindowState
import com.neversoft.launcher.window.toIcon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Windows 11 taskbar: centered Start/Search/Task View + running apps,
// system tray with network/volume/battery, two-line clock, show-desktop sliver.
@Composable
fun Taskbar(
    onStartClick: () -> Unit,
    onSearchClick: () -> Unit,
    onTaskViewClick: () -> Unit,
    onExplorerClick: () -> Unit,
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

    var timeText by remember { mutableStateOf(formatTime()) }
    var dateText by remember { mutableStateOf(formatDate()) }
    var batteryPct by remember { mutableIntStateOf(readBattery(context)) }
    var onWifi by remember { mutableStateOf(isOnWifi(context)) }
    LaunchedEffect(Unit) {
        while (true) {
            timeText = formatTime()
            dateText = formatDate()
            batteryPct = readBattery(context)
            onWifi = isOnWifi(context)
            kotlinx.coroutines.delay(5_000)
        }
    }

    Column(modifier) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(theme.stroke))
        Box(Modifier.fillMaxWidth().weight(1f).background(theme.taskbar)) {
            // Centered cluster
            Row(
                Modifier.align(Alignment.Center).fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                TaskbarButton(onClick = onStartClick, contentDescription = "Start") {
                    StartLogo(22.dp)
                }
                TaskbarButton(onClick = onSearchClick, contentDescription = "Search") {
                    Icon(Icons.Outlined.Search, "Search", Modifier.size(22.dp), tint = theme.text)
                }
                TaskbarButton(onClick = onTaskViewClick, contentDescription = "Task view") {
                    TaskViewGlyph(theme.text)
                }
                TaskbarButton(
                    onClick = onExplorerClick,
                    contentDescription = "File Explorer",
                    indicator = if (openWindows.any { it.title == "File Explorer" }) {
                        if (openWindows.firstOrNull { it.id == focusedWindowId }?.title == "File Explorer")
                            TaskbarIndicator.FOCUSED else TaskbarIndicator.RUNNING
                    } else TaskbarIndicator.NONE,
                ) {
                    Icon(Icons.Filled.Folder, "File Explorer", Modifier.size(22.dp), tint = Color(0xFFFFCA28))
                }
                openWindows.filter { it.title != "File Explorer" }.take(6).forEach { win ->
                    TaskbarButton(
                        onClick = { onWindowTaskbarClick(win.id) },
                        contentDescription = win.title,
                        indicator = if (win.id == focusedWindowId && win.state != WindowState.MINIMIZED)
                            TaskbarIndicator.FOCUSED else TaskbarIndicator.RUNNING,
                    ) {
                        Icon(toIcon(win.contentType), win.title, Modifier.size(21.dp), tint = theme.text)
                    }
                }
            }

            // System tray, right-aligned
            Row(
                Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Network / volume / battery cluster -> quick settings
                Row(
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onTrayClick() }
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(
                        if (onWifi) Icons.Outlined.Wifi else Icons.Outlined.SignalCellularAlt,
                        "Network", Modifier.size(16.dp), tint = theme.text,
                    )
                    Icon(Icons.AutoMirrored.Outlined.VolumeUp, "Volume", Modifier.size(16.dp), tint = theme.text)
                    BatteryGlyph(batteryPct, theme.text)
                }
                // Clock -> calendar & notifications
                Column(
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onClockClick() }
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(timeText, color = theme.text, fontSize = 12.sp, lineHeight = 15.sp, textAlign = TextAlign.End)
                    Text(dateText, color = theme.text, fontSize = 12.sp, lineHeight = 15.sp, textAlign = TextAlign.End)
                }
                // Show desktop sliver
                Box(
                    Modifier.fillMaxHeight().width(10.dp).clickable { onShowDesktop() },
                ) {
                    Box(
                        Modifier.align(Alignment.CenterStart).fillMaxHeight()
                            .padding(vertical = 12.dp).width(1.dp).background(theme.divider),
                    )
                }
            }
        }
    }
}

enum class TaskbarIndicator { NONE, RUNNING, FOCUSED }

@Composable
private fun TaskbarButton(
    onClick: () -> Unit,
    contentDescription: String,
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
        when (indicator) {
            TaskbarIndicator.FOCUSED -> Box(
                Modifier.align(Alignment.BottomCenter).padding(bottom = 3.dp)
                    .size(width = 16.dp, height = 3.dp)
                    .background(theme.accent, RoundedCornerShape(2.dp)),
            )
            TaskbarIndicator.RUNNING -> Box(
                Modifier.align(Alignment.BottomCenter).padding(bottom = 3.dp)
                    .size(width = 6.dp, height = 3.dp)
                    .background(theme.textSecondary, RoundedCornerShape(2.dp)),
            )
            TaskbarIndicator.NONE -> Unit
        }
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
        // Cap
        drawRoundRect(
            color = tint,
            topLeft = Offset(bodyW + capW * 0.4f, size.height * 0.3f),
            size = Size(capW, size.height * 0.4f),
            cornerRadius = CornerRadius(capW * 0.5f),
        )
        // Fill
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

private fun isOnWifi(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
    return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
}
