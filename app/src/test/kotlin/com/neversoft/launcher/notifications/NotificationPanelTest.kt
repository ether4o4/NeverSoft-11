package com.neversoft.launcher.notifications

import android.app.Notification
import android.app.NotificationManager
import android.service.notification.StatusBarNotification
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.neversoft.launcher.taskbar.CalendarFlyout
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ServiceController
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29], qualifiers = "w420dp-h800dp")
@LooperMode(LooperMode.Mode.PAUSED)
class NotificationPanelTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private lateinit var controller: ServiceController<LauncherNotificationListener>

    @Before
    fun setup() {
        compose.runOnIdle {
            controller = Robolectric.buildService(LauncherNotificationListener::class.java).create()
            shadowOf(compose.activity.getSystemService(NotificationManager::class.java))
                .setNotificationListenerAccessGranted(NotificationCenter.component(compose.activity), true)
            controller.get().onListenerConnected()
        }
    }

    @After
    fun cleanup() {
        compose.runOnIdle {
            shadowOf(compose.activity.getSystemService(NotificationManager::class.java))
                .setNotificationListenerAccessGranted(NotificationCenter.component(compose.activity), false)
            NotificationCenter.refresh(compose.activity)
            controller.destroy()
        }
    }

    private fun notification(id: Int, text: String): StatusBarNotification {
        val notification = Notification.Builder(compose.activity, "messages")
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("Messages $id").setContentText(text).setWhen(id.toLong()).build()
        return StatusBarNotification("example.messages", "example.messages", id, null, 1000, 0, 0,
            notification, android.os.Process.myUserHandle(), id.toLong())
    }

    @Test
    fun liveNotificationsAppearAboveUnchangedCalendarAndDismiss() {
        compose.setContent { CalendarFlyout(modifier = Modifier.size(348.dp, 540.dp)) }
        compose.onNodeWithText("No new notifications").assertIsDisplayed()
        val first = notification(1, "First message")
        compose.runOnIdle { controller.get().onNotificationPosted(first) }
        compose.onNodeWithText("First message").assertIsDisplayed()
        val messageBounds = compose.onNodeWithText("First message").fetchSemanticsNode().boundsInRoot
        val calendarBounds = compose.onNodeWithContentDescription("Next month").fetchSemanticsNode().boundsInRoot
        assertTrue(messageBounds.bottom < calendarBounds.top)

        val second = notification(2, "Second message")
        compose.runOnIdle { controller.get().onNotificationPosted(second) }
        compose.onNodeWithText("Second message").assertIsDisplayed()
        compose.runOnIdle { controller.get().onNotificationRemoved(first) }
        compose.onNodeWithText("First message").assertDoesNotExist()
        compose.onNodeWithContentDescription("Dismiss notification").performClick()
        compose.runOnIdle { controller.get().onNotificationRemoved(second) }
        compose.onNodeWithText("No new notifications").assertIsDisplayed()
        compose.onNodeWithContentDescription("Next month").assertIsDisplayed()
    }

    @Test
    fun deniedAccessOffersSettingsAndGrantUpdatesWithoutRecreatingPanel() {
        compose.runOnIdle {
            shadowOf(compose.activity.getSystemService(NotificationManager::class.java))
                .setNotificationListenerAccessGranted(NotificationCenter.component(compose.activity), false)
            NotificationCenter.refresh(compose.activity)
        }
        compose.setContent { CalendarFlyout(modifier = Modifier.size(348.dp, 540.dp)) }
        compose.onNodeWithText("Enable notification access").assertIsDisplayed().performClick()
        compose.runOnIdle {
            shadowOf(compose.activity.getSystemService(NotificationManager::class.java))
                .setNotificationListenerAccessGranted(NotificationCenter.component(compose.activity), true)
            shadowOf(controller.get()).addActiveNotification(notification(1, "Already active"))
            controller.get().onListenerConnected()
        }
        compose.onNodeWithText("Already active").assertIsDisplayed()
        compose.runOnIdle {
            shadowOf(compose.activity.getSystemService(NotificationManager::class.java))
                .setNotificationListenerAccessGranted(NotificationCenter.component(compose.activity), false)
            NotificationCenter.refresh(compose.activity)
        }
        compose.onNodeWithText("Already active").assertDoesNotExist()
        compose.onNodeWithText("Enable notification access").assertIsDisplayed()
    }
}
