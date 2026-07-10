package com.neversoft.launcher.window

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

enum class WindowState { NORMAL, MINIMIZED, MAXIMIZED }

enum class WindowContentType { FILE_EXPLORER, CONTROL_PANEL, BROWSER, TERMINAL, PHOTOS, MUSIC }

data class ShellWindow(
    val id: String,
    val title: String,
    val contentType: WindowContentType,
    /** Optional content argument, e.g. the initial path for the file explorer. */
    val payload: String? = null,
    val state: WindowState = WindowState.NORMAL,
    val position: Offset = Offset(80f, 80f),
    val size: DpSize = DpSize(360.dp, 480.dp),
    val zIndex: Int = 0,
    val previousPosition: Offset? = null,
    val previousSize: DpSize? = null,
)

class WindowManagerEngine {
    private val _windows = mutableStateListOf<ShellWindow>()
    val windows: List<ShellWindow> get() = _windows
    private var _nextZ = 1

    fun openWindow(contentType: WindowContentType, title: String, payload: String? = null): ShellWindow {
        val offset = Offset(80f + _windows.size * 24f, 80f + _windows.size * 24f)
        val win = ShellWindow(id = "${contentType.name}_${System.currentTimeMillis()}",
            title = title, contentType = contentType, payload = payload,
            position = offset, zIndex = _nextZ++)
        _windows.add(win); return win
    }

    fun focusWindow(id: String) {
        val idx = _windows.indexOfFirst { it.id == id }
        if (idx >= 0 && _windows[idx].zIndex != _nextZ - 1) {
            _windows[idx] = _windows[idx].copy(zIndex = _nextZ++)
        }
    }

    fun closeWindow(id: String)    { _windows.removeIf { it.id == id } }
    fun toggleMinimize(id: String) = updateWindow(id) { w ->
        w.copy(state = if (w.state == WindowState.MINIMIZED) WindowState.NORMAL else WindowState.MINIMIZED)
    }
    fun maximizeWindow(id: String) = updateWindow(id) { w ->
        w.copy(state = WindowState.MAXIMIZED, previousPosition = w.position, previousSize = w.size)
    }
    fun restoreWindow(id: String) = updateWindow(id) { w ->
        w.copy(state = WindowState.NORMAL,
            position = w.previousPosition ?: w.position,
            size = w.previousSize ?: w.size)
    }
    fun moveWindow(id: String, newPosition: Offset) = updateWindow(id) { it.copy(position = newPosition) }

    /** Drag by a delta, resolving the window's CURRENT position at call time. */
    fun dragBy(id: String, delta: Offset) = updateWindow(id) { w ->
        w.copy(
            position = Offset(
                (w.position.x + delta.x).coerceAtLeast(0f),
                (w.position.y + delta.y).coerceAtLeast(0f),
            ),
        )
    }

    /** Resize by a delta, clamped to a usable minimum. */
    fun resizeBy(id: String, deltaWidth: Dp, deltaHeight: Dp) = updateWindow(id) { w ->
        w.copy(
            size = DpSize(
                (w.size.width + deltaWidth).coerceAtLeast(MIN_WIDTH),
                (w.size.height + deltaHeight).coerceAtLeast(MIN_HEIGHT),
            ),
        )
    }

    fun reorderToFront(id: String) = focusWindow(id)

    private fun updateWindow(id: String, transform: (ShellWindow) -> ShellWindow) {
        val idx = _windows.indexOfFirst { it.id == id }
        if (idx >= 0) _windows[idx] = transform(_windows[idx])
    }

    private companion object {
        val MIN_WIDTH = 260.dp
        val MIN_HEIGHT = 220.dp
    }
}
