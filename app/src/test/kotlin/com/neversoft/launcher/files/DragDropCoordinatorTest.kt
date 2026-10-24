package com.neversoft.launcher.files

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DragDropCoordinatorTest {
    @After
    fun tearDown() {
        DragDropCoordinator.cancel()
        DragDropCoordinator.unregisterTargets("test-")
    }

    @Test
    fun highestPriorityTargetReceivesAnInBoundsDrop() {
        val payload = DragDropCoordinator.Payload.ExplorerFile("/tmp/item", false)
        var selected = ""
        DragDropCoordinator.registerTarget("test-low", Rect(0f, 0f, 100f, 100f), 0) { _, _ ->
            selected = "low"
            true
        }
        DragDropCoordinator.registerTarget("test-high", Rect(20f, 20f, 80f, 80f), 10) { _, _ ->
            selected = "high"
            true
        }

        DragDropCoordinator.begin(payload)
        assertTrue(DragDropCoordinator.finish(Offset(40f, 40f)))
        assertEquals("high", selected)
    }

    @Test
    fun outsideDropAndCancelLeaveNoActiveDrag() {
        DragDropCoordinator.registerTarget("test-target", Rect(0f, 0f, 20f, 20f), 1) { _, _ -> true }
        DragDropCoordinator.begin(DragDropCoordinator.Payload.ExplorerFile("/tmp/item", false))
        assertFalse(DragDropCoordinator.finish(Offset(100f, 100f)))
        assertFalse(DragDropCoordinator.isDragging())

        DragDropCoordinator.begin(DragDropCoordinator.Payload.ExplorerFile("/tmp/item", false))
        DragDropCoordinator.cancel()
        assertFalse(DragDropCoordinator.isDragging())
    }
}
