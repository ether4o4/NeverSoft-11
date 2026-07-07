package com.neversoft.launcher.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import com.neversoft.launcher.desktop.Desktop
import com.neversoft.launcher.startmenu.StartMenu
import com.neversoft.launcher.taskbar.Taskbar
import com.neversoft.launcher.taskview.TaskView
import com.neversoft.launcher.window.*

@Composable
fun ShellScreen() {
    val windowEngine = remember { WindowManagerEngine() }
    var startMenuVisible by remember { mutableStateOf(false) }
    var taskViewVisible by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(
        Brush.radialGradient(listOf(Color(0xFF0D1B3E), Color(0xFF050A1A))))) {

        Desktop(Modifier.fillMaxSize().padding(bottom = 60.dp))

        ShellWindowHost(engine = windowEngine,
            modifier = Modifier.fillMaxSize().padding(bottom = 60.dp))

        if (taskViewVisible) {
            TaskView(windows = windowEngine.windows,
                onWindowFocus = { id -> windowEngine.restoreWindow(id); windowEngine.focusWindow(id); taskViewVisible = false },
                onWindowClose = { id -> windowEngine.closeWindow(id) },
                onDismiss = { taskViewVisible = false })
        }

        if (startMenuVisible) {
            StartMenu(
                onDismiss = { startMenuVisible = false },
                onOpenFileExplorer = { windowEngine.openWindow(WindowContentType.FILE_EXPLORER, "File Explorer"); startMenuVisible = false },
                onOpenControlPanel = { windowEngine.openWindow(WindowContentType.CONTROL_PANEL, "Control Panel"); startMenuVisible = false },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp))
        }

        Taskbar(
            onOrbClick = { startMenuVisible = !startMenuVisible },
            onTaskViewClick = { taskViewVisible = !taskViewVisible },
            openWindows = windowEngine.windows,
            onWindowTaskbarClick = { id ->
                val w = windowEngine.windows.find { it.id == id }
                if (w?.state == com.neversoft.launcher.window.WindowState.MINIMIZED) windowEngine.restoreWindow(id)
                windowEngine.focusWindow(id)
                startMenuVisible = false
            },
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(60.dp))
    }
}
