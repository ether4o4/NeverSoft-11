package com.neversoft.launcher.shell

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neversoft.launcher.desktop.Desktop
import com.neversoft.launcher.startmenu.PowerAction
import com.neversoft.launcher.startmenu.StartMenu
import com.neversoft.launcher.taskbar.CalendarFlyout
import com.neversoft.launcher.taskbar.QuickSettingsFlyout
import com.neversoft.launcher.taskbar.Taskbar
import com.neversoft.launcher.taskview.TaskView
import com.neversoft.launcher.theme.BloomWallpaper
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.theme.ThemePreset
import com.neversoft.launcher.window.ShellWindowHost
import com.neversoft.launcher.window.WindowContentType
import com.neversoft.launcher.window.WindowManagerEngine
import com.neversoft.launcher.window.WindowState

private enum class Flyout { NONE, START, SEARCH, QUICK_SETTINGS, CALENDAR }

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private const val TASKBAR_HEIGHT = 48

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
        val context = androidx.compose.ui.platform.LocalContext.current
        val menuWidth = if (maxWidth < 620.dp) maxWidth - 24.dp else 596.dp

        BloomWallpaper(isDark = theme.isDark, modifier = Modifier.fillMaxSize())

        Desktop(
            onOpenWindow = { type, title, param -> windowEngine.openWindow(type, title, param) },
            modifier = Modifier.fillMaxSize().padding(bottom = TASKBAR_HEIGHT.dp),
        )

        ShellWindowHost(
            engine = windowEngine,
            selectedPreset = selectedPreset,
            onSelectPreset = onSelectPreset,
            modifier = Modifier.fillMaxSize().padding(bottom = TASKBAR_HEIGHT.dp),
        )

        if (taskViewVisible) {
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
                modifier = Modifier.fillMaxSize().padding(bottom = TASKBAR_HEIGHT.dp),
            )
        }

        // Click-away scrim for open flyouts
        if (flyout != Flyout.NONE) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = TASKBAR_HEIGHT.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { flyout = Flyout.NONE },
            )
        }

        if (flyout == Flyout.START || flyout == Flyout.SEARCH) {
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
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = (TASKBAR_HEIGHT + 8).dp)
                    .width(menuWidth),
            )
        }

        if (flyout == Flyout.QUICK_SETTINGS) {
            QuickSettingsFlyout(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = (TASKBAR_HEIGHT + 8).dp),
            )
        }

        if (flyout == Flyout.CALENDAR) {
            CalendarFlyout(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = (TASKBAR_HEIGHT + 8).dp),
            )
        }

        Taskbar(
            onStartClick = { flyout = if (flyout == Flyout.START) Flyout.NONE else Flyout.START },
            onSearchClick = { flyout = if (flyout == Flyout.SEARCH) Flyout.NONE else Flyout.SEARCH },
            onTaskViewClick = {
                taskViewVisible = !taskViewVisible
                flyout = Flyout.NONE
            },
            onExplorerClick = {
                windowEngine.openWindow(WindowContentType.FILE_EXPLORER, "File Explorer")
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
                .fillMaxWidth()
                .height(TASKBAR_HEIGHT.dp),
        )

        if (locked) {
            LockScreen(onUnlock = { locked = false }, modifier = Modifier.fillMaxSize())
        }
    }
}
