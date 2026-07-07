package com.neversoft.launcher.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*

@Composable
fun Desktop(modifier: Modifier = Modifier) {
    Box(modifier) {
        // Recycle Bin — bottom left
        DesktopIcon(Icons.Default.Delete, "Recycle Bin",
            Modifier.align(Alignment.BottomStart).padding(24.dp))
        // File Explorer — top left
        DesktopIcon(Icons.Default.Folder, "File Explorer",
            Modifier.align(Alignment.TopStart).padding(24.dp))
        // Placeholder shortcuts
        DesktopIcon(Icons.Default.Language, "Browser",
            Modifier.align(Alignment.TopStart).padding(start = 100.dp, top = 24.dp))
        DesktopIcon(Icons.Default.Terminal, "Terminal",
            Modifier.align(Alignment.TopStart).padding(start = 176.dp, top = 24.dp))
    }
}

@Composable
private fun DesktopIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(36.dp))
        Text(label, color = Color.White, fontSize = 10.sp)
    }
}
