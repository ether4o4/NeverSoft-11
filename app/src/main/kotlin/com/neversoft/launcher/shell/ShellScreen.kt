package com.neversoft.launcher.shell

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.neversoft.launcher.apps.InstalledAppsRepository
import com.neversoft.launcher.data.AppSettings
import com.neversoft.launcher.desktop.Desktop
import com.neversoft.launcher.startmenu.PowerAction
import com.neversoft.launcher.startmenu.StartMenu
import com.neversoft.launcher.taskbar.CalendarFlyout
import com.neversoft.launcher.taskbar.QuickSettingsFlyout
import com.neversoft.launcher.taskbar.TASKBAR_HEIGHT_DP
import com.neversoft.launcher.taskbar.Taskbar
import com.neversoft.launcher.taskview.TaskView
import com.neversoft.launcher.theme.BloomWallpaper
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.theme.ThemePreset
import com.neversoft.launcher.window.ShellWindowHost
import com.neversoft.launcher.window.WindowContentType
import com.neversoft.launcher.window.WindowManagerEngine
import com.neversoft.launcher.window.WindowState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private enum class Flyout { NONE, START, SEARCH, QUICK_SETTINGS, CALENDAR }

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

// Crash-proof clamp: when the available [max] is smaller than the desired
// [min] (e.g. the launcher is measured at a tiny overview/recents thumbnail
// during a swipe-up), coerceIn(min, max) would throw because min > max. Fall
// back to the available space instead of crashing the whole shell.
private fun coerceDp(value: Dp, min: Dp, max: Dp): Dp {
    val safeMax = max.coerceAtLeast(0.dp)
    return if (safeMax <= min) safeMax else value.coerceIn(min, safeMax)
}

private fun parseSize(raw: String): DpSize? = raw.split(",")
    .takeIf { it.size == 2 }
    ?.let { parts ->
        val w = parts[0].toFloatOrNull() ?: return null
        val h = parts[1].toFloatOrNull() ?: return null
        DpSize(w.dp, h.dp)
    }

@Composable
fun ShellScreen(
    selectedPreset: ThemePreset,
    onSelectPreset: (ThemePreset) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    val windowEngine = remember { WindowManagerEngine() }
    var flyout by remember { mutableStateOf(Flyout.NONE) }
    var taskViewVisible by remember { mutableStateOf(false) }
    var locked by remember { mutableStateOf(false) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        // Warm the app cache so the Start menu opens instantly
        LaunchedEffect(Unit) { InstalledAppsRepository.loadApps(context) }

        val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val bottomBar = TASKBAR_HEIGHT_DP.dp + 1.dp + navBottom
        val contentPadding = Modifier.fillMaxSize().padding(top = statusTop, bottom = bottomBar)

        // Resizable panel sizes (persisted)
        val menuMaxW = maxWidth - 24.dp
        val menuMaxH = maxHeight - bottomBar - statusTop - 16.dp
        val defaultMenu = DpSize(
            if (maxWidth < 620.dp) menuMaxW else 596.dp,
            if (menuMaxH < 620.dp) menuMaxH else 620.dp,
        )
        var startMenuSize by remember { mutableStateOf<DpSize?>(null) }
        var calendarSize by remember { mutableStateOf<DpSize?>(null) }
        LaunchedEffect(Unit) {
            startMenuSize = parseSize(AppSettings.startMenuSizeFlow(context).first())
            calendarSize = parseSize(AppSettings.calendarSizeFlow(context).first())
        }
        fun clampMenu(size: DpSize) = DpSize(
            coerceDp(size.width, 320.dp, menuMaxW),
            coerceDp(size.height, 420.dp, menuMaxH),
        )
        val effectiveMenu = clampMenu(startMenuSize ?: defaultMenu)
        val defaultCalendar = DpSize(348.dp, if (menuMaxH < 540.dp) menuMaxH else 540.dp)
        fun clampCalendar(size: DpSize) = DpSize(
            coerceDp(size.width, 300.dp, menuMaxW),
            coerceDp(size.height, 420.dp, menuMaxH),
        )
        val effectiveCalendar = clampCalendar(calendarSize ?: defaultCalendar)

        BloomWallpaper(isDark = theme.isDark, modifier = Modifier.fillMaxSize())

        Desktop(
            onOpenWindow = { type, title, param -> windowEngine.openWindow(type, title, param) },
            modifier = contentPadding,
        )

        ShellWindowHost(
            engine = windowEngine,
            selectedPreset = selectedPreset,
            onSelectPreset = onSelectPreset,
            modifier = contentPadding,
        )

        AnimatedVisibility(
            visible = taskViewVisible,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(120)),
            modifier = Modifier.fillMaxSize(),
        ) {
            TaskView(
                windows = windowEngine.windows,
                focusedWindowId = windowEngine.focusedWindowId,
                onWindowFocus = { id ->
                    windowEngine.restoreWindow(id)
                    windowEngine.focusWindow(id)
                    taskViewVisible = false
                },
                onWindowClose = { id -> windowEngine.closeWindow(id) },
                onDismiss = { taskViewVisible = false },
                modifier = contentPadding,
            )
        }

        // Click-away scrim for open flyouts
        if (flyout != Flyout.NONE) {
            Box(
                contentPadding.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { flyout = Flyout.NONE },
            )
        }

        // Start menu: anchored bottom-left, resized from its top-right corner
        AnimatedVisibility(
            visible = flyout == Flyout.START || flyout == Flyout.SEARCH,
            enter = fadeIn(tween(160)) + slideInVertically(tween(160)) { it / 10 },
            exit = fadeOut(tween(110)) + slideOutVertically(tween(110)) { it / 10 },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = bottomBar + 8.dp),
        ) {
            StartMenu(
                onDismiss = { flyout = Flyout.NONE },
                onOpenFileExplorer = {
                    windowEngine.openWindow(WindowContentType.FILE_EXPLORER, "File Explorer")
                    flyout = Flyout.NONE
                },
                onOpenSettings = {
                    windowEngine.openWindow(WindowContentType.SETTINGS, "Settings")
                    flyout = Flyout.NONE
                },
                onPowerAction = { action ->
                    flyout = Flyout.NONE
                    when (action) {
                        PowerAction.LOCK, PowerAction.SLEEP, PowerAction.SHUT_DOWN -> locked = true
                        PowerAction.RESTART -> context.findActivity()?.recreate()
                        PowerAction.SIGN_OUT -> runCatching {
                            context.startActivity(
                                Intent(Settings.ACTION_HOME_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            )
                        }
                    }
                },
                searchFocused = flyout == Flyout.SEARCH,
                currentSize = effectiveMenu,
                onResize = { newSize -> startMenuSize = clampMenu(newSize) },
                onResizeEnd = {
                    startMenuSize?.let { size ->
                        scope.launch {
                            AppSettings.setStartMenuSize(
                                context, "${size.width.value},${size.height.value}",
                            )
                        }
                    }
                },
                modifier = Modifier.size(effectiveMenu),
            )
        }

        AnimatedVisibility(
            visible = flyout == Flyout.QUICK_SETTINGS,
            enter = fadeIn(tween(160)) + slideInVertically(tween(160)) { it / 10 },
            exit = fadeOut(tween(110)) + slideOutVertically(tween(110)) { it / 10 },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = bottomBar + 8.dp),
        ) {
            QuickSettingsFlyout()
        }

        // Calendar: anchored bottom-right, resized from its top-left corner
        AnimatedVisibility(
            visible = flyout == Flyout.CALENDAR,
            enter = fadeIn(tween(160)) + slideInVertically(tween(160)) { it / 10 },
            exit = fadeOut(tween(110)) + slideOutVertically(tween(110)) { it / 10 },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = bottomBar + 8.dp),
        ) {
            CalendarFlyout(
                currentSize = effectiveCalendar,
                onResize = { newSize -> calendarSize = clampCalendar(newSize) },
                onResizeEnd = {
                    calendarSize?.let { size ->
                        scope.launch {
                            AppSettings.setCalendarSize(
                                context, "${size.width.value},${size.height.value}",
                            )
                        }
                    }
                },
                modifier = Modifier.size(effectiveCalendar),
            )
        }

        Taskbar(
            onStartClick = { flyout = if (flyout == Flyout.START) Flyout.NONE else Flyout.START },
            onSearchClick = { flyout = if (flyout == Flyout.SEARCH) Flyout.NONE else Flyout.SEARCH },
            onTaskViewClick = {
                taskViewVisible = !taskViewVisible
                flyout = Flyout.NONE
            },
            openWindows = windowEngine.windows,
            focusedWindowId = windowEngine.focusedWindowId,
            onWindowTaskbarClick = { id ->
                val win = windowEngine.windows.find { it.id == id }
                if (win?.state == WindowState.MINIMIZED) {
                    windowEngine.restoreWindow(id)
                    windowEngine.focusWindow(id)
                } else if (win?.id == windowEngine.focusedWindowId) {
                    windowEngine.toggleMinimize(id)
                } else {
                    windowEngine.focusWindow(id)
                }
                flyout = Flyout.NONE
            },
            onTrayClick = {
                flyout = if (flyout == Flyout.QUICK_SETTINGS) Flyout.NONE else Flyout.QUICK_SETTINGS
            },
            onClockClick = {
                flyout = if (flyout == Flyout.CALENDAR) Flyout.NONE else Flyout.CALENDAR
            },
            onShowDesktop = {
                windowEngine.minimizeAll()
                flyout = Flyout.NONE
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
        )

        if (locked) {
            LockScreen(onUnlock = { locked = false }, modifier = Modifier.fillMaxSize())
        }
    }
}
