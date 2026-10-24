package com.neversoft.launcher.notifications

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/** Bound exclusively by Android after the user grants Notification Access. */
class LauncherNotificationListener : NotificationListenerService() {
    private var observingAccess = false
    private val accessObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            NotificationCenter.refresh(this@LauncherNotificationListener)
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            contentResolver.registerContentObserver(
                Settings.Secure.getUriFor("enabled_notification_listeners"),
                false,
                accessObserver,
            )
            observingAccess = true
        } catch (_: RuntimeException) {
            // ON_RESUME and every callback also check access on devices that
            // do not permit observing this setting.
            Log.w("NotificationCenter", "Access setting observer unavailable")
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        NotificationCenter.connected(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let { NotificationCenter.posted(this, it) }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.let { NotificationCenter.removed(this, it.key) }
    }

    override fun onListenerDisconnected() {
        NotificationCenter.disconnected(this)
        NotificationCenter.requestReconnect(this)
        super.onListenerDisconnected()
    }

    override fun onDestroy() {
        if (observingAccess) contentResolver.unregisterContentObserver(accessObserver)
        NotificationCenter.disconnected(this)
        super.onDestroy()
    }
}
