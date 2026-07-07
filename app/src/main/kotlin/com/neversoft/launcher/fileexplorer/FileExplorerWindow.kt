package com.neversoft.launcher.fileexplorer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.*
import com.neversoft.launcher.theme.LocalLauncherTheme

@Composable
fun FileExplorerWindow() {
    val theme = LocalLauncherTheme.current
    var selected by remember { mutableStateOf("Home") }
    val navItems = listOf("Home","Desktop","Documents","Downloads","Pictures","Music","Videos","This PC")
    Row(Modifier.fillMaxSize()) {
        Column(Modifier.width(160.dp).fillMaxHeight().verticalScroll(rememberScrollState())
            .background(theme.surfaceColor.copy(alpha = 0.3f)).padding(8.dp)) {
            Text("NeverSoft 11", fontSize = 10.sp, color = theme.onSurface.copy(0.5f), modifier = Modifier.padding(4.dp))
            navItems.forEach { item ->
                Row(Modifier.fillMaxWidth().clickable { selected = item }
                    .background(if (selected == item) theme.accentColor.copy(0.15f) else androidx.compose.ui.graphics.Color.Transparent,
                        androidx.compose.foundation.shape.RoundedCornerShape(6.dp)).padding(8.dp, 6.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(navIcon(item), null, Modifier.size(16.dp), tint = theme.onSurface.copy(0.7f))
                    Spacer(Modifier.width(6.dp))
                    Text(item, fontSize = 12.sp, color = theme.onSurface)
                }
            }
        }
        Divider(Modifier.width(1.dp).fillMaxHeight(), color = theme.accentColor.copy(0.2f))
        Column(Modifier.weight(1f).fillMaxHeight().padding(12.dp)) {
            Text(selected, style = MaterialTheme.typography.titleMedium, color = theme.onSurface)
            Spacer(Modifier.height(8.dp))
            Text("Phase 4 wires real file listing here", color = theme.onSurface.copy(0.4f), fontSize = 12.sp)
        }
    }
}

fun navIcon(label: String): ImageVector = when(label) {
    "Home"      -> Icons.Default.Home
    "Desktop"   -> Icons.Default.DesktopWindows
    "Documents" -> Icons.Default.Description
    "Downloads" -> Icons.Default.Download
    "Pictures"  -> Icons.Default.Image
    "Music"     -> Icons.Default.MusicNote
    "Videos"    -> Icons.Default.VideoLibrary
    "This PC"   -> Icons.Default.Computer
    else        -> Icons.Default.Folder
}
