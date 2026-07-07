package com.neversoft.launcher.taskbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.window.ShellWindow
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Taskbar(
    onOrbClick: () -> Unit,
    onTaskViewClick: () -> Unit,
    openWindows: List<ShellWindow>,
    onWindowTaskbarClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current
    var currentTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    Row(
        modifier = modifier.background(theme.surfaceBrush()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        // Start Orb
        Box(
            modifier = Modifier.size(44.dp).background(theme.accentColor, CircleShape)
                .clickable { onOrbClick() },
            contentAlignment = Alignment.Center
        ) { Text("NS", color = theme.onAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp) }

        Spacer(Modifier.width(12.dp))

        // Running windows
        openWindows.take(8).forEach { win ->
            Box(Modifier.size(36.dp).clickable { onWindowTaskbarClick(win.id) },
                contentAlignment = Alignment.Center) {
                Icon(win.contentType.let {
                    com.neversoft.launcher.window.toIcon(it)
                }, contentDescription = win.title, modifier = Modifier.size(20.dp), tint = theme.onSurface)
                Box(Modifier.size(4.dp, 2.dp).align(Alignment.BottomCenter)
                    .background(theme.accentColor, CircleShape))
            }
            Spacer(Modifier.width(4.dp))
        }

        Spacer(Modifier.weight(1f))

        // Task View
        IconButton(onClick = onTaskViewClick, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.GridView, contentDescription = "Task View",
                tint = theme.onSurface, modifier = Modifier.size(18.dp))
        }

        Spacer(Modifier.width(8.dp))

        // Clock
        Text(currentTime, color = theme.onSurface, fontSize = 12.sp)
        Spacer(Modifier.width(12.dp))
    }
}
