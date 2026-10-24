package com.neversoft.launcher.files

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Shared in-process drag session for the desktop and every Explorer window.
 * Coordinates are in the Shell root coordinate space, so window offsets and
 * scroll transforms are already reflected by the registered bounds.
 */
object DragDropCoordinator {
    sealed interface Payload {
        data class DesktopShortcut(
            val page: Int,
            val itemId: String,
            val label: String,
            val packageName: String? = null,
            val path: String? = null,
            val kind: String,
            val iconPath: String = "",
        ) : Payload

        data class ExplorerFile(
            val path: String,
            val isDirectory: Boolean,
        ) : Payload

        data class ExplorerShortcut(
            val directory: String,
            val entry: ShortcutStore.Entry,
        ) : Payload
    }

    private data class Target(
        val id: String,
        val bounds: Rect,
        val priority: Int,
        val onDrop: (Payload, Offset) -> Boolean,
    )

    private val targets = LinkedHashMap<String, Target>()
    private var activePayload: Payload? = null
    private val _changeVersion = MutableStateFlow(0L)
    val changeVersion: StateFlow<Long> = _changeVersion

    @Synchronized
    fun registerTarget(
        id: String,
        bounds: Rect,
        priority: Int = 0,
        onDrop: (Payload, Offset) -> Boolean,
    ) {
        targets[id] = Target(id, bounds, priority, onDrop)
    }

    @Synchronized
    fun unregisterTarget(id: String) {
        targets.remove(id)
    }

    @Synchronized
    fun unregisterTargets(prefix: String) {
        targets.keys.removeAll { it.startsWith(prefix) }
    }

    @Synchronized
    fun begin(payload: Payload) {
        activePayload = payload
    }

    @Synchronized
    fun cancel() {
        activePayload = null
    }

    @Synchronized
    fun finish(position: Offset): Boolean {
        val payload = activePayload ?: return false
        activePayload = null
        val target = targets.values
            .filter { it.bounds.contains(position) }
            .maxWithOrNull(compareBy<Target> { it.priority }.thenBy { it.bounds.width * it.bounds.height })
            ?: return false
        return runCatching { target.onDrop(payload, position) }.getOrDefault(false)
    }

    @Synchronized
    fun isDragging(): Boolean = activePayload != null

    @Synchronized
    fun notifyChanged() {
        _changeVersion.value += 1L
    }
}
