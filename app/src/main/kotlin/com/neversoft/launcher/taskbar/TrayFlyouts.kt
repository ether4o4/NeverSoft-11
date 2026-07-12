package com.neversoft.launcher.taskbar

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.AudioManager
import android.os.BatteryManager
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.AirplanemodeActive
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neversoft.launcher.theme.LocalLauncherTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Context.open(intentAction: String) {
    runCatching { startActivity(Intent(intentAction).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
}

// ————— Quick settings (network / volume / battery flyout) —————
@Composable
fun QuickSettingsFlyout(modifier: Modifier = Modifier) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    val audio = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    var volume by remember {
        mutableFloatStateOf(
            audio.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() /
                audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1),
        )
    }
    var brightness by remember {
        val current = context.findActivity()?.window?.attributes?.screenBrightness ?: -1f
        mutableFloatStateOf(if (current in 0f..1f) current else 0.8f)
    }
    val batteryPct = remember {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)?.takeIf { it in 1..100 } ?: 100
    }

    Column(
        modifier
            .width(348.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(theme.menuSurface)
            .border(1.dp, theme.stroke, RoundedCornerShape(8.dp))
            .padding(20.dp),
    ) {
        // Toggle grid, 3 per row — each opens the matching system control
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickToggle("Wi-Fi", Icons.Outlined.Wifi, active = true, Modifier.weight(1f)) {
                runCatching {
                    context.startActivity(
                        Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                }
            }
            QuickToggle("Bluetooth", Icons.Outlined.Bluetooth, active = false, Modifier.weight(1f)) {
                context.open(Settings.ACTION_BLUETOOTH_SETTINGS)
            }
            QuickToggle("Airplane mode", Icons.Outlined.AirplanemodeActive, active = false, Modifier.weight(1f)) {
                context.open(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickToggle("Battery saver", Icons.Outlined.BatterySaver, active = false, Modifier.weight(1f)) {
                context.open(Settings.ACTION_BATTERY_SAVER_SETTINGS)
            }
            QuickToggle("Night light", Icons.Outlined.Bedtime, active = false, Modifier.weight(1f)) {
                context.open(Settings.ACTION_DISPLAY_SETTINGS)
            }
            QuickToggle("Accessibility", Icons.Outlined.Accessibility, active = false, Modifier.weight(1f)) {
                context.open(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            }
        }

        Spacer(Modifier.height(18.dp))

        // Brightness — adjusts the shell window's brightness
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.WbSunny, "Brightness", Modifier.size(18.dp), tint = theme.text)
            Slider(
                value = brightness,
                onValueChange = { value ->
                    brightness = value
                    context.findActivity()?.window?.let { window ->
                        val attrs = window.attributes
                        attrs.screenBrightness = value.coerceIn(0.05f, 1f)
                        window.attributes = attrs
                    }
                },
                colors = fluentSliderColors(),
                modifier = Modifier.weight(1f).padding(start = 12.dp),
            )
        }
        // Volume — live media volume
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Outlined.VolumeUp, "Volume", Modifier.size(18.dp), tint = theme.text)
            Slider(
                value = volume,
                onValueChange = { value ->
                    volume = value
                    val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    runCatching {
                        audio.setStreamVolume(AudioManager.STREAM_MUSIC, (value * max).toInt(), 0)
                    }
                },
                colors = fluentSliderColors(),
                modifier = Modifier.weight(1f).padding(start = 12.dp),
            )
        }

        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(theme.divider))
        Spacer(Modifier.height(10.dp))

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("$batteryPct%", color = theme.text, fontSize = 13.sp)
            Spacer(Modifier.weight(1f))
            Icon(
                Icons.Outlined.Edit, "Edit quick settings", Modifier.size(16.dp),
                tint = theme.textSecondary,
            )
            Spacer(Modifier.width(18.dp))
            Icon(
                Icons.Outlined.Settings, "All settings",
                Modifier.size(18.dp).clip(RoundedCornerShape(4.dp))
                    .clickable { context.open(Settings.ACTION_SETTINGS) },
                tint = theme.text,
            )
        }
    }
}

@Composable
private fun fluentSliderColors() = SliderDefaults.colors(
    thumbColor = LocalLauncherTheme.current.accent,
    activeTrackColor = LocalLauncherTheme.current.accent,
    inactiveTrackColor = LocalLauncherTheme.current.textDisabled.copy(alpha = 0.3f),
)

@Composable
private fun QuickToggle(
    label: String,
    icon: ImageVector,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (active) theme.accent else theme.card)
                .border(1.dp, theme.stroke, RoundedCornerShape(4.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, label, Modifier.size(20.dp), tint = if (active) theme.accentText else theme.text)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            label, color = theme.text, fontSize = 11.sp, lineHeight = 13.sp,
            textAlign = TextAlign.Center, maxLines = 2,
        )
    }
}

// ————— Notification center + calendar (clock flyout) —————
// Anchored bottom-right; its top-left corner drags to resize.
@Composable
fun CalendarFlyout(
    currentSize: androidx.compose.ui.unit.DpSize = androidx.compose.ui.unit.DpSize.Unspecified,
    onResize: (androidx.compose.ui.unit.DpSize) -> Unit = {},
    onResizeEnd: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current
    var monthOffset by remember { mutableStateOf(0) }
    val density = androidx.compose.ui.platform.LocalDensity.current

    Box(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(theme.menuSurface)
                .border(1.dp, theme.stroke, RoundedCornerShape(8.dp)),
        ) {
        // Notifications — grows with the panel
        Column(
            Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Notifications", color = theme.text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text("Clear all", color = theme.accent, fontSize = 12.sp)
            }
            Spacer(Modifier.weight(1f))
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Notifications, null, Modifier.size(26.dp), tint = theme.textDisabled)
                Spacer(Modifier.height(6.dp))
                Text("No new notifications", color = theme.textSecondary, fontSize = 13.sp)
            }
            Spacer(Modifier.weight(1f))
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(theme.divider))

        // Calendar month view
        val cal = remember(monthOffset) {
            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, monthOffset)
            }
        }
        val monthTitle = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
        val today = Calendar.getInstance()

        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(monthTitle, color = theme.text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Outlined.ChevronLeft, "Previous month",
                    Modifier.size(22.dp).clip(CircleShape).clickable { monthOffset-- },
                    tint = theme.text,
                )
                Spacer(Modifier.width(14.dp))
                Icon(
                    Icons.Outlined.ChevronRight, "Next month",
                    Modifier.size(22.dp).clip(CircleShape).clickable { monthOffset++ },
                    tint = theme.text,
                )
            }
            Spacer(Modifier.height(10.dp))

            val dayNames = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
            Row(Modifier.fillMaxWidth()) {
                dayNames.forEach { d ->
                    Text(
                        d, color = theme.textSecondary, fontSize = 11.sp,
                        textAlign = TextAlign.Center, modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.height(4.dp))

            val firstDow = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY // 0-based offset
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val isThisMonth = today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
            var day = 1 - firstDow
            repeat(6) {
                Row(Modifier.fillMaxWidth()) {
                    repeat(7) {
                        val inMonth = day in 1..daysInMonth
                        val isToday = inMonth && isThisMonth && day == today.get(Calendar.DAY_OF_MONTH)
                        Box(Modifier.weight(1f).height(34.dp), contentAlignment = Alignment.Center) {
                            if (inMonth) {
                                Box(
                                    Modifier.size(28.dp).clip(CircleShape)
                                        .background(if (isToday) theme.accent else androidx.compose.ui.graphics.Color.Transparent),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        "$day",
                                        color = if (isToday) theme.accentText else theme.text,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                        }
                        day++
                    }
                }
            }
        }
        }

        // Resize button: panel is anchored bottom-right, so the top-left
        // corner drags to resize. Baseline captured at drag start, deltas
        // accumulate locally — can't snap back.
        val latestSize by androidx.compose.runtime.rememberUpdatedState(currentSize)
        val latestOnResize by androidx.compose.runtime.rememberUpdatedState(onResize)
        val latestOnResizeEnd by androidx.compose.runtime.rememberUpdatedState(onResizeEnd)
        Box(
            Modifier
                .align(Alignment.TopStart)
                .size(40.dp)
                .pointerInput(Unit) {
                    var baseW = 0f
                    var baseH = 0f
                    var acc = androidx.compose.ui.geometry.Offset.Zero
                    detectDragGestures(
                        onDragStart = {
                            baseW = latestSize.width.toPx()
                            baseH = latestSize.height.toPx()
                            acc = androidx.compose.ui.geometry.Offset.Zero
                        },
                        onDrag = { change, drag ->
                            change.consume()
                            acc += drag
                            latestOnResize(
                                androidx.compose.ui.unit.DpSize(
                                    (baseW - acc.x).toDp(),
                                    (baseH - acc.y).toDp(),
                                ),
                            )
                        },
                        onDragEnd = { latestOnResizeEnd() },
                    )
                },
        ) {
            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 5.dp, start = 5.dp)
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(theme.card.copy(alpha = 0.9f))
                    .border(1.dp, theme.stroke, RoundedCornerShape(6.dp)),
            ) {
                androidx.compose.foundation.Canvas(
                    Modifier.align(Alignment.Center).size(11.dp),
                ) {
                    val stroke = 1.4.dp.toPx()
                    drawLine(
                        theme.textSecondary,
                        androidx.compose.ui.geometry.Offset(0f, 0f),
                        androidx.compose.ui.geometry.Offset(size.width, 0f),
                        stroke,
                    )
                    drawLine(
                        theme.textSecondary,
                        androidx.compose.ui.geometry.Offset(0f, 0f),
                        androidx.compose.ui.geometry.Offset(0f, size.height),
                        stroke,
                    )
                }
            }
        }
    }
}
