package com.neversoft.launcher.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.service.notification.StatusBarNotification
import com.neversoft.launcher.MainActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ServiceController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class NotificationCenterTest {
    private val context: Context get() = RuntimeEnvironment.getApplication()
    private lateinit var controller: ServiceController<LauncherNotificationListener>
    private lateinit var listener: LauncherNotificationListener

    @Before
    fun setup() {
        controller = Robolectric.buildService(LauncherNotificationListener::class.java).create()
        listener = controller.get()
        grantAccess(true)
    }

    @After
    fun cleanup() {
        grantAccess(false)
        NotificationCenter.refresh(context)
        controller.destroy()
    }

    private fun grantAccess(granted: Boolean) {
        shadowOf(context.getSystemService(NotificationManager::class.java))
            .setNotificationListenerAccessGranted(NotificationCenter.component(context), granted)
    }

    private fun sbn(
        id: Int = 1,
        text: String = "Hello",
        pkg: String = "example.messages",
        flags: Int = 0,
        intent: PendingIntent? = null,
        time: Long = 1000L,
    ): StatusBarNotification {
        val notification = Notification.Builder(context, "messages")
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("Messages")
            .setContentText(text)
            .setContentIntent(intent)
            .setWhen(time)
            .build().apply { this.flags = flags }
        return StatusBarNotification(pkg, pkg, id, null, 1000, 0, 0, notification, android.os.Process.myUserHandle(), time)
    }

    @Test
    fun manifestRegistersSystemProtectedListener() {
        val info = context.packageManager.getServiceInfo(NotificationCenter.component(context), 0)
        assertTrue(info.exported)
        assertEquals("android.permission.BIND_NOTIFICATION_LISTENER_SERVICE", info.permission)
        val matches = context.packageManager.queryIntentServices(
            Intent("android.service.notification.NotificationListenerService").setPackage(context.packageName),
            PackageManager.GET_SERVICES,
        )
        assertTrue(matches.any { it.serviceInfo.name == LauncherNotificationListener::class.java.name })
    }

    @Test
    fun connectionLoadsExistingNotificationsExcludingLauncherAndOrdersNewestFirst() {
        shadowOf(listener).addActiveNotification(sbn(id = 1))
        shadowOf(listener).addActiveNotification(sbn(id = 2, time = 2000))
        shadowOf(listener).addActiveNotification(sbn(id = 3, pkg = context.packageName))
        listener.onListenerConnected()
        val state = NotificationCenter.state.value
        assertTrue(state.accessGranted)
        assertTrue(state.connected)
        assertEquals(listOf(2000L, 1000L), state.items.map { it.timestamp })
        assertEquals("Messages", state.items.first().title)
        assertEquals("Hello", state.items.first().text)
        assertNotNull(state.items.first().smallIcon)
    }

    @Test
    fun postUpdateAndRemovalAreLiveAndKeyedWithoutDuplicates() {
        listener.onListenerConnected()
        val first = sbn()
        listener.onNotificationPosted(first)
        listener.onNotificationPosted(sbn(text = "Updated"))
        assertEquals(1, NotificationCenter.state.value.items.size)
        assertEquals("Updated", NotificationCenter.state.value.items.single().text)
        listener.onNotificationPosted(sbn(id = 2, time = 2000))
        assertEquals(2, NotificationCenter.state.value.items.size)
        listener.onNotificationRemoved(first)
        assertEquals(sbn(id = 2).key, NotificationCenter.state.value.items.single().key)
    }

    @Test
    fun disconnectClearsStaleItemsAndReconnectReplacesWithActiveSnapshot() {
        val old = sbn()
        val new = sbn(id = 2)
        shadowOf(listener).addActiveNotification(old)
        listener.onListenerConnected()
        listener.onListenerDisconnected()
        assertFalse(NotificationCenter.state.value.connected)
        assertTrue(NotificationCenter.state.value.items.isEmpty())
        listener.cancelNotification(old.key)
        shadowOf(listener).addActiveNotification(new)
        listener.onListenerConnected()
        assertEquals(listOf(new.key), NotificationCenter.state.value.items.map { it.key })
        assertTrue(org.robolectric.shadows.ShadowNotificationListenerService.getRebindRequestCount() > 0)
    }

    @Test
    fun recreatedServiceRepopulatesAndOldDestroyCannotClearNewConnection() {
        shadowOf(listener).addActiveNotification(sbn())
        listener.onListenerConnected()
        val replacement = Robolectric.buildService(LauncherNotificationListener::class.java).create()
        try {
            shadowOf(replacement.get()).addActiveNotification(sbn(id = 2))
            replacement.get().onListenerConnected()
            NotificationCenter.disconnected(listener)
            assertEquals(listOf(sbn(id = 2).key), NotificationCenter.state.value.items.map { it.key })
        } finally {
            replacement.destroy()
        }
    }

    @Test
    fun revokedAccessClearsDataAndRejectsSubsequentCallbacksAndActions() {
        val notification = sbn()
        shadowOf(listener).addActiveNotification(notification)
        listener.onListenerConnected()
        grantAccess(false)
        NotificationCenter.refresh(context)
        listener.onNotificationPosted(sbn(id = 2))
        assertFalse(NotificationCenter.state.value.accessGranted)
        assertFalse(NotificationCenter.state.value.connected)
        assertTrue(NotificationCenter.state.value.items.isEmpty())
        assertFalse(NotificationCenter.dismiss(notification.key))
        assertFalse(NotificationCenter.open(context, notification.key))
    }

    @Test
    fun revocationObserverClearsWhilePanelWouldBeOpen() {
        shadowOf(listener).addActiveNotification(sbn())
        listener.onListenerConnected()
        grantAccess(false)
        context.contentResolver.notifyChange(Settings.Secure.getUriFor("enabled_notification_listeners"), null)
        shadowOf(android.os.Looper.getMainLooper()).idle()
        assertFalse(NotificationCenter.state.value.accessGranted)
        assertTrue(NotificationCenter.state.value.items.isEmpty())
    }

    @Test
    fun dismissalWaitsForAndroidRemovalAndClearAllSkipsOngoingNotifications() {
        val clearable = sbn()
        val ongoing = sbn(id = 2, flags = Notification.FLAG_ONGOING_EVENT)
        shadowOf(listener).addActiveNotification(clearable)
        shadowOf(listener).addActiveNotification(ongoing)
        listener.onListenerConnected()
        assertFalse(NotificationCenter.dismiss(ongoing.key))
        assertTrue(NotificationCenter.clearAll())
        assertEquals(listOf(ongoing.key), listener.activeNotifications.map { it.key })
        assertEquals(2, NotificationCenter.state.value.items.size)
        listener.onNotificationRemoved(clearable)
        assertEquals(listOf(ongoing.key), NotificationCenter.state.value.items.map { it.key })
    }

    @Test
    fun notificationTapSendsOriginalPendingIntentAndHandlesCancellation() {
        val pending = PendingIntent.getActivity(
            context, 10, Intent(context, MainActivity::class.java).setAction("original-notification"),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = sbn(intent = pending)
        shadowOf(listener).addActiveNotification(notification)
        listener.onListenerConnected()
        assertTrue(NotificationCenter.open(context, notification.key))
        assertEquals("original-notification", shadowOf(RuntimeEnvironment.getApplication()).nextStartedActivity.action)
        pending.cancel()
        assertFalse(NotificationCenter.open(context, notification.key))
        listener.onNotificationPosted(sbn(id = 2))
        assertFalse(NotificationCenter.open(context, sbn(id = 2).key))
    }

    @Test
    @Config(sdk = [35])
    fun pendingIntentOpensOnTargetAndroidVersion() {
        notificationTapSendsOriginalPendingIntentAndHandlesCancellation()
    }

    @Test
    fun accessSettingsUsesListenerSettingsNotPostNotifications() {
        grantAccess(false)
        assertFalse(NotificationCenter.hasAccess(context))
        assertTrue(NotificationCenter.openAccessSettings(context))
        assertEquals(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS,
            shadowOf(RuntimeEnvironment.getApplication()).nextStartedActivity.action)
    }

    @Test
    fun bigTextAndTimestampFallbackAreExtractedWithoutInventingContent() {
        val notification = sbn(time = 1234)
        notification.notification.`when` = 0
        notification.notification.extras.putCharSequence(Notification.EXTRA_BIG_TEXT, "Expanded message")
        val item = NotificationCenter.toItem(notification, context.packageName)!!
        assertEquals("Expanded message", item.text)
        assertEquals(1234L, item.timestamp)
        notification.notification.extras.clear()
        val empty = NotificationCenter.toItem(notification, context.packageName)!!
        assertEquals("", empty.title)
        assertEquals("", empty.text)
    }
}
