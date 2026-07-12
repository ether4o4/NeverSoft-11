package com.neversoft.launcher.window

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

enum class WindowState { NORMAL, MINIMIZED, MAXIMIZED }

enum class WindowContentType { FILE_EXPLORER, SETTINGS, BROWSER, TERMINAL, PHOTOS, MUSIC }

data class ShellWindow(
    val id: String,
    val title: String,
    val contentType: WindowContentType,
    val state: WindowState = WindowState.NORMAL,
    val position: Offset = Offset(40f, 60f),
    val size: DpSize = DpSize(380.dp, 540.dp),
    val zIndex: Int = 0,
    val param: String? = null, // e.g. initial path for File Explorer
    val previousPosition: Offset? = null,
    val previousSize: DpSize? = null,
)

class WindowManagerEngine {
    private val _windows = mutableStateListOf<ShellWindow>()
    val windows: List<ShellWindow> get() = _windows
    private var _nextZ = 1

    val focusedWindowId: String?
        get() = _windows.filter { it.state != WindowState.MINIMIZED }.maxByOrNull { it.zIndex }?.id

    fun openWindow(contentType: WindowContentType, title: String, param: String? = null): ShellWindow {
        // Re-focus an existing window of the same title instead of stacking duplicates
        _windows.firstOrNull { it.title == title && it.contentType == contentType }?.let { existing ->
            if (existing.state == WindowState.MINIMIZED) restoreWindow(existing.id)
            focusWindow(existing.id)
            return _windows.first { it.id == existing.id }
        }
        val offset = Offset(40f + _windows.size * 48f, 60f + _windows.size * 48f)
        val win = ShellWindow(
            id = "${contentType.name}_${System.currentTimeMillis()}",
            title = title, contentType = contentType, position = offset,
            zIndex = _nextZ++, param = param,
        )
        _windows.add(win)
        return win
    }

    fun focusWindow(id: String) {
        val idx = _windows.indexOfFirst { it.id == id }
        if (idx >= 0) _windows[idx] = _windows[idx].copy(zIndex = _nextZ++)
    }

    fun closeWindow(id: String) { _windows.removeIf { it.id == id } }

    fun toggleMinimize(id: String) = updateWindow(id) { w ->
        w.copy(state = if (w.state == WindowState.MINIMIZED) WindowState.NORMAL else WindowState.MINIMIZED)
    }

    fun minimizeAll() {
        for (i in _windows.indices) {
            _windows[i] = _windows[i].copy(state = WindowState.MINIMIZED)
        }
    }

    fun maximizeWindow(id: String) = updateWindow(id) { w ->
        w.copy(state = WindowState.MAXIMIZED, previousPosition = w.position, previousSize = w.size)
    }

    fun restoreWindow(id: String) = updateWindow(id) { w ->
        w.copy(
            state = WindowState.NORMAL,
            position = w.previousPosition ?: w.position,
            size = w.previousSize ?: w.size,
        )
    }

    fun moveWindow(id: String, newPosition: Offset) = updateWindow(id) { it.copy(position = newPosition) }

    // Relative move/resize: the transform reads the window's CURRENT state,
    // so rapid incremental drag deltas accumulate correctly even though the
    // gesture handler holds a stale snapshot of the window.
    fun moveWindowBy(id: String, delta: Offset) = updateWindow(id) { w ->
        w.copy(
            position = Offset(
                (w.position.x + delta.x).coerceAtLeast(0f),
                (w.position.y + delta.y).coerceAtLeast(0f),
            ),
        )
    }

    fun resizeWindow(id: String, newSize: DpSize) = updateWindow(id) { w ->
        w.copy(
            size = DpSize(
                newSize.width.coerceAtLeast(280.dp),
                newSize.height.coerceAtLeast(320.dp),
            ),
        )
    }

    fun resizeWindowBy(id: String, deltaWidth: androidx.compose.ui.unit.Dp, deltaHeight: androidx.compose.ui.unit.Dp) =
        updateWindow(id) { w ->
            w.copy(
                size = DpSize(
                    (w.size.width + deltaWidth).coerceIn(280.dp, 1400.dp),
                    (w.size.height + deltaHeight).coerceIn(320.dp, 2400.dp),
                ),
            )
        }

    fun reorderToFront(id: String) = focusWindow(id)

    private fun updateWindow(id: String, transform: (ShellWindow) -> ShellWindow) {
        val idx = _windows.indexOfFirst { it.id == id }
        if (idx >= 0) _windows[idx] = transform(_windows[idx])
    }
}
