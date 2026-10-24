package com.neversoft.launcher.notifications

import android.app.ActivityOptions
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal data class NotificationItem(
    val key: String,
    val packageName: String,
    val title: String,
    val text: String,
    val timestamp: Long,
    val smallIcon: Icon?,
    val contentIntent: PendingIntent?,
    val clearable: Boolean,
    val autoCancel: Boolean,
)

internal data class NotificationCenterState(
    val accessGranted: Boolean = false,
    val connected: Boolean = false,
    val items: List<NotificationItem> = emptyList(),
    val unavailable: Boolean = false,
)

/**
 * A process-local projection of Android's active notifications, not a history
 * database. No message text or PendingIntent is written to disk or logs.
 * Listener callbacks, permission observers and UI actions run on the main thread.
 */
internal object NotificationCenter {
    private const val TAG = "NotificationCenter"
    private val mutableState = MutableStateFlow(NotificationCenterState())
    val state = mutableState.asStateFlow()
    private var listener: LauncherNotificationListener? = null

    fun component(context: Context) = ComponentName(context, LauncherNotificationListener::class.java)

    fun hasAccess(context: Context): Boolean = try {
        context.getSystemService(NotificationManager::class.java)
            ?.isNotificationListenerAccessGranted(component(context)) == true
    } catch (_: RuntimeException) {
        false
    }

    fun refresh(context: Context) {
        val granted = hasAccess(context)
        if (!granted) {
            listener = null
            mutableState.value = NotificationCenterState()
            return
        }
        mutableState.value = state.value.copy(accessGranted = true)
        val current = listener
        if (current != null) {
            reload(current)
        } else {
            requestReconnect(context)
        }
    }

    fun requestReconnect(context: Context) {
        if (!hasAccess(context)) return
        try {
            NotificationListenerService.requestRebind(component(context))
        } catch (_: RuntimeException) {
            mutableState.value = state.value.copy(unavailable = true)
            Log.w(TAG, "Notification listener rebind unavailable")
        }
    }

    fun connected(service: LauncherNotificationListener) {
        if (!hasAccess(service)) {
            refresh(service)
            return
        }
        listener = service
        mutableState.value = NotificationCenterState(accessGranted = true, connected = true)
        reload(service)
    }

    fun disconnected(service: LauncherNotificationListener) {
        // A destroyed older service must not clear a newly connected instance.
        if (listener !== service) return
        listener = null
        mutableState.value = NotificationCenterState(accessGranted = hasAccess(service))
    }

    private fun reload(service: LauncherNotificationListener) {
        try {
            val active = service.activeNotifications
            if (active == null) {
                disconnected(service)
                mutableState.value = state.value.copy(unavailable = true)
                return
            }
            mutableState.value = state.value.copy(
                items = active.mapNotNull { toItem(it, service.packageName) }
                    .distinctBy { it.key }
                    .sortedByDescending { it.timestamp },
                unavailable = false,
            )
        } catch (_: RuntimeException) {
            disconnected(service)
            mutableState.value = state.value.copy(unavailable = true)
            Log.w(TAG, "Unable to read active notifications")
        }
    }

    fun posted(service: LauncherNotificationListener, notification: StatusBarNotification) {
        if (!validateConnection(service)) return
        val item = toItem(notification, service.packageName)
        val retained = state.value.items.filterNot { it.key == notification.key }
        mutableState.value = state.value.copy(
            items = (retained + listOfNotNull(item)).sortedByDescending { it.timestamp },
        )
    }

    fun removed(service: LauncherNotificationListener, key: String) {
        if (!validateConnection(service)) return
        mutableState.value = state.value.copy(items = state.value.items.filterNot { it.key == key })
    }

    private fun validateConnection(service: LauncherNotificationListener): Boolean {
        if (!hasAccess(service)) {
            refresh(service)
            return false
        }
        return listener === service
    }

    fun dismiss(key: String): Boolean {
        val current = listener ?: return false
        if (!validateConnection(current)) return false
        if (state.value.items.none { it.key == key && it.clearable }) return false
        return try {
            current.cancelNotification(key)
            // Android confirms removal via onNotificationRemoved. Never
            // optimistically hide an ongoing/restricted notification.
            true
        } catch (_: RuntimeException) {
            refresh(current)
            Log.w(TAG, "Notification dismissal unavailable")
            false
        }
    }

    fun clearAll(): Boolean {
        val keys = state.value.items.filter { it.clearable }.map { it.key }
        var accepted = true
        keys.forEach { if (!dismiss(it)) accepted = false }
        return accepted
    }

    fun open(context: Context, key: String): Boolean {
        if (!hasAccess(context)) {
            refresh(context)
            return false
        }
        val item = state.value.items.firstOrNull { it.key == key } ?: return false
        val intent = item.contentIntent ?: return false
        return try {
            val options = if (Build.VERSION.SDK_INT >= 34) {
                ActivityOptions.makeBasic()
                    .setPendingIntentBackgroundActivityStartMode(
                        ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED,
                    ).toBundle()
            } else null
            intent.send(context, 0, null, null, null, null, options)
            if (item.autoCancel && item.clearable) dismiss(key)
            true
        } catch (_: PendingIntent.CanceledException) {
            refresh(context)
            false
        } catch (_: RuntimeException) {
            Log.w(TAG, "Notification action unavailable")
            false
        }
    }

    fun openAccessSettings(context: Context): Boolean = try {
        context.startActivity(
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
        true
    } catch (_: RuntimeException) {
        Log.w(TAG, "Notification access settings unavailable")
        false
    }

    internal fun toItem(sbn: StatusBarNotification, ownPackage: String): NotificationItem? {
        if (sbn.packageName == ownPackage) return null
        return try {
            val notification = sbn.notification
            val extras = notification.extras
            val text = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
                ?.takeIf { it.isNotBlank() }
                ?: extras.getCharSequence(Notification.EXTRA_TEXT)?.takeIf { it.isNotBlank() }
                ?: extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.joinToString("\n")
                ?: notification.tickerText
            NotificationItem(
                key = sbn.key,
                packageName = sbn.packageName,
                title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty(),
                text = text?.toString().orEmpty(),
                timestamp = notification.`when`.takeIf { it > 0 } ?: sbn.postTime,
                smallIcon = notification.smallIcon,
                contentIntent = notification.contentIntent,
                clearable = sbn.isClearable,
                autoCancel = notification.flags and Notification.FLAG_AUTO_CANCEL != 0,
            )
        } catch (_: RuntimeException) {
            Log.w(TAG, "Unsupported notification payload")
            null
        }
    }
}
