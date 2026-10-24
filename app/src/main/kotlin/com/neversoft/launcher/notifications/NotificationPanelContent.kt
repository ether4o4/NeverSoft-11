package com.neversoft.launcher.notifications

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.neversoft.launcher.theme.LocalLauncherTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Date

/** Only fills the existing upper section; its parent owns all panel geometry. */
@Composable
internal fun NotificationPanelContent() {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by NotificationCenter.state.collectAsState()
    val listState = rememberLazyListState()
    LaunchedEffect(state.items.firstOrNull()?.key) {
        // A new newest notification should be visible immediately, rather than
        // LazyColumn preserving the previous first row's scroll anchor.
        if (state.items.isNotEmpty()) listState.scrollToItem(0)
    }
    fun unavailable(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    DisposableEffect(lifecycleOwner, context) {
        NotificationCenter.refresh(context)
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) NotificationCenter.refresh(context)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Notifications", color = theme.text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Text(
                "Clear all", color = theme.accent, fontSize = 12.sp,
                modifier = Modifier.clickable(enabled = state.items.any { it.clearable }) {
                    if (!NotificationCenter.clearAll()) unavailable("Some notifications cannot be dismissed")
                },
            )
        }
        if (state.items.isEmpty()) {
            Spacer(Modifier.weight(1f))
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Notifications, null, Modifier.size(26.dp), tint = theme.textDisabled)
                Spacer(Modifier.height(6.dp))
                val message = when {
                    !state.accessGranted -> "Enable notification access"
                    !state.connected || state.unavailable -> "Reconnect notifications"
                    else -> "No new notifications"
                }
                Text(
                    message, color = theme.textSecondary, fontSize = 13.sp,
                    modifier = Modifier.clickable(enabled = !state.connected || !state.accessGranted) {
                        NotificationCenter.refresh(context)
                        if (!NotificationCenter.openAccessSettings(context)) {
                            unavailable("Notification access settings are unavailable")
                        }
                    },
                )
            }
            Spacer(Modifier.weight(1f))
        } else {
            LazyColumn(
                Modifier.fillMaxWidth().weight(1f).padding(top = 6.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.items, key = { it.key }) { item ->
                    NotificationRow(
                        item,
                        onOpen = {
                            if (!NotificationCenter.open(context, item.key)) {
                                unavailable("This notification can no longer be opened")
                            }
                        },
                        onDismiss = {
                            if (!NotificationCenter.dismiss(item.key)) {
                                unavailable("This notification cannot be dismissed")
                            }
                        },
                    )
                }
            }
        }
    }
}

private data class NotificationApp(val name: String, val icon: ImageBitmap?)

@Composable
private fun NotificationRow(item: NotificationItem, onOpen: () -> Unit, onDismiss: () -> Unit) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    var app by remember(item.packageName) {
        mutableStateOf(NotificationApp(item.packageName, null))
    }
    LaunchedEffect(item.packageName, item.smallIcon, context) {
        app = withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val info = runCatching { pm.getApplicationInfo(item.packageName, 0) }.getOrNull()
            val name = info?.let { runCatching { pm.getApplicationLabel(it).toString() }.getOrNull() }
                ?: item.packageName
            val icon = runCatching {
                (info?.let { pm.getApplicationIcon(it) } ?: item.smallIcon?.loadDrawable(context))
                    ?.toBitmap(48, 48)?.asImageBitmap()
            }.getOrNull()
            NotificationApp(name, icon)
        }
    }
    Row(
        Modifier.fillMaxWidth().clickable(enabled = item.contentIntent != null, onClick = onOpen)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        app.icon?.let { Image(it, app.name, Modifier.size(22.dp)) }
            ?: Icon(Icons.Outlined.Notifications, app.name, Modifier.size(22.dp), tint = theme.textSecondary)
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(app.name, color = theme.textSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (item.title.isNotEmpty()) {
                Text(item.title, color = theme.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            if (item.text.isNotEmpty()) {
                Text(item.text, color = theme.textSecondary, fontSize = 12.sp,
                    maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
            Text(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(item.timestamp)),
                color = theme.textSecondary, fontSize = 10.sp)
        }
        if (item.clearable) {
            Icon(
                Icons.Outlined.Close, "Dismiss notification",
                Modifier.size(24.dp).clickable(onClick = onDismiss).padding(3.dp),
                tint = theme.textSecondary,
            )
        }
    }
}
