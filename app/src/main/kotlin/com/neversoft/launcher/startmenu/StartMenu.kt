package com.neversoft.launcher.startmenu

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import com.neversoft.launcher.theme.LocalLauncherTheme

@Composable
fun StartMenu(
    onDismiss: () -> Unit,
    onOpenFileExplorer: () -> Unit,
    onOpenControlPanel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }

    Card(
        modifier = modifier.width(360.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(Modifier.background(theme.surfaceBrush()).padding(16.dp)) {
            // Search bar
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                placeholder = { Text("Search apps, settings, documents…", fontSize = 13.sp, color = theme.onSurface.copy(0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = theme.onSurface, unfocusedTextColor = theme.onSurface,
                    focusedBorderColor = theme.accentColor, unfocusedBorderColor = theme.onSurface.copy(0.3f),
                    cursorColor = theme.accentColor,
                ),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = theme.onSurface.copy(0.6f)) },
                singleLine = true,
            )
            Spacer(Modifier.height(12.dp))
            Text("Most used", style = MaterialTheme.typography.labelSmall, color = theme.onSurface.copy(0.6f))
            Spacer(Modifier.height(8.dp))
            // 4×4 grid placeholder
            LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height(240.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(16) { i ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)) {
                        Box(Modifier.size(40.dp).background(theme.accentColor.copy(0.15f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center) {
                            Text("A", color = theme.onSurface, fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(2.dp))
                        Text("App ${i+1}", color = theme.onSurface, fontSize = 10.sp, maxLines = 1)
                    }
                }
            }
            TextButton(onClick = {}, modifier = Modifier.align(Alignment.End)) {
                Text("See all ›", color = theme.accentColor, fontSize = 12.sp)
            }
            Divider(color = theme.onSurface.copy(0.1f))
            Spacer(Modifier.height(8.dp))
            // Bottom cluster
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                BottomClusterBtn("Settings",  Icons.Default.Settings, theme.onSurface) {
                    context.startActivity(Intent(Settings.ACTION_SETTINGS)) }
                BottomClusterBtn("Control\nPanel", Icons.Default.DisplaySettings, theme.onSurface) {
                    onOpenControlPanel() }
                BottomClusterBtn("Files",    Icons.Default.Folder,   theme.onSurface) {
                    onOpenFileExplorer() }
                BottomClusterBtn("Terminal", Icons.Default.Terminal,  theme.onSurface) {}
            }
        }
    }
}

@Composable
private fun BottomClusterBtn(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
        Text(label, color = tint.copy(0.8f), fontSize = 9.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
