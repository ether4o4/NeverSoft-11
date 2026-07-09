package com.neversoft.launcher.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import com.neversoft.launcher.desktop.Desktop
import com.neversoft.launcher.startmenu.StartMenu
import com.neversoft.launcher.taskbar.Taskbar
import com.neversoft.launcher.taskview.TaskView
import com.neversoft.launcher.theme.DesktopWallpaper
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.theme.ThemePreset
import com.neversoft.launcher.window.*

@Composable
fun ShellScreen(
    selectedPreset: ThemePreset,
    onSelectPreset: (ThemePreset) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    val windowEngine = remember { WindowManagerEngine() }
    var startMenuVisible by remember { mutableStateOf(false) }
    var taskViewVisible by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .background(DesktopWallpaper.brush)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        theme.accentColor.copy(alpha = 0.20f),
                        Color.Transparent,
                    ),
                ),
            ),
    ) {
        Desktop(
            onOpenWindow = { type, title -> windowEngine.openWindow(type, title) },
            modifier = Modifier.fillMaxSize().padding(bottom = 60.dp),
        )

        ShellWindowHost(
            engine = windowEngine,
            selectedPreset = selectedPreset,
            onSelectPreset = onSelectPreset,
            modifier = Modifier.fillMaxSize().padding(bottom = 60.dp),
        )

        if (taskViewVisible) {
            TaskView(
                windows = windowEngine.windows,
                onWindowFocus = { id ->
                    windowEngine.restoreWindow(id)
                    windowEngine.focusWindow(id)
                    taskViewVisible = false
                },
                onWindowClose = { id -> windowEngine.closeWindow(id) },
                onDismiss = { taskViewVisible = false },
            )
        }

        if (startMenuVisible) {
            StartMenu(
                onDismiss = { startMenuVisible = false },
                onOpenFileExplorer = {
                    windowEngine.openWindow(WindowContentType.FILE_EXPLORER, "File Explorer")
                    startMenuVisible = false
                },
                onOpenControlPanel = {
                    windowEngine.openWindow(WindowContentType.CONTROL_PANEL, "Control Panel")
                    startMenuVisible = false
                },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp),
            )
        }

        Taskbar(
            onOrbClick = { startMenuVisible = !startMenuVisible },
            onTaskViewClick = { taskViewVisible = !taskViewVisible },
            openWindows = windowEngine.windows,
            onWindowTaskbarClick = { id ->
                val w = windowEngine.windows.find { it.id == id }
                if (w?.state == WindowState.MINIMIZED) windowEngine.restoreWindow(id)
                windowEngine.focusWindow(id)
                startMenuVisible = false
            },
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(60.dp),
        )
    }
}
