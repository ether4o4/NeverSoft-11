package com.neversoft.launcher.startmenu

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neversoft.launcher.apps.InstalledApp
import com.neversoft.launcher.apps.InstalledAppsRepository
import com.neversoft.launcher.search.AppSearchProvider
import com.neversoft.launcher.search.FileSearchProvider
import com.neversoft.launcher.search.LauncherSearchEngine
import com.neversoft.launcher.search.ResultType
import com.neversoft.launcher.search.SearchResult
import com.neversoft.launcher.search.SettingsSearchProvider
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

    var appsLoaded by remember { mutableStateOf(false) }
    var apps by remember { mutableStateOf(emptyList<InstalledApp>()) }
    LaunchedEffect(Unit) {
        apps = InstalledAppsRepository.loadApps(context)
        appsLoaded = true
    }

    val searchEngine = remember {
        LauncherSearchEngine(
            listOf(
                AppSearchProvider { apps },
                SettingsSearchProvider(),
                FileSearchProvider(),
            ),
        )
    }
    var results by remember { mutableStateOf(emptyList<SearchResult>()) }
    LaunchedEffect(query, apps) {
        results = searchEngine.search(query)
    }

    fun activate(result: SearchResult) {
        when (result.type) {
            ResultType.APP -> result.packageName?.let { InstalledAppsRepository.launch(context, it) }
            ResultType.SETTING -> runCatching { context.startActivity(Intent(result.id)) }
            ResultType.FILE -> onOpenFileExplorer()
            else -> Unit
        }
        onDismiss()
    }

    Card(
        modifier = modifier.width(360.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(Modifier.background(theme.surfaceBrush()).padding(16.dp)) {
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

            if (query.isBlank()) {
                Text("All apps", style = MaterialTheme.typography.labelSmall, color = theme.onSurface.copy(0.6f))
                Spacer(Modifier.height(8.dp))
                Box(Modifier.height(260.dp)) {
                    when {
                        !appsLoaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = theme.accentColor)
                        }
                        apps.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No apps found", color = theme.onSurface.copy(0.5f), fontSize = 12.sp)
                        }
                        else -> LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            items(apps, key = { it.packageName }) { app ->
                                AppGridItem(app = app, onSurface = theme.onSurface, accent = theme.accentColor) {
                                    InstalledAppsRepository.launch(context, app.packageName)
                                    onDismiss()
                                }
                            }
                        }
                    }
                }
            } else {
                Text("Results", style = MaterialTheme.typography.labelSmall, color = theme.onSurface.copy(0.6f))
                Spacer(Modifier.height(8.dp))
                Box(Modifier.height(260.dp)) {
                    if (results.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No results for \"$query\"", color = theme.onSurface.copy(0.5f), fontSize = 12.sp)
                        }
                    } else {
                        LazyColumn {
                            items(results, key = { "${it.type}_${it.id}" }) { result ->
                                SearchResultRow(
                                    result = result,
                                    apps = apps,
                                    onSurface = theme.onSurface,
                                    accent = theme.accentColor,
                                ) { activate(result) }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Divider(color = theme.onSurface.copy(0.1f))
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                BottomClusterBtn("Settings", Icons.Default.Settings, theme.onSurface) {
                    runCatching { context.startActivity(Intent(Settings.ACTION_SETTINGS)) }
                    onDismiss()
                }
                BottomClusterBtn("Control\nPanel", Icons.Default.DisplaySettings, theme.onSurface) {
                    onOpenControlPanel()
                }
                BottomClusterBtn("Files", Icons.Default.Folder, theme.onSurface) {
                    onOpenFileExplorer()
                }
                BottomClusterBtn("Terminal", Icons.Default.Terminal, theme.onSurface) { onDismiss() }
            }
        }
    }
}

@Composable
private fun AppGridItem(
    app: InstalledApp,
    onSurface: Color,
    accent: Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(4.dp),
    ) {
        if (app.icon != null) {
            Image(bitmap = app.icon, contentDescription = app.label, modifier = Modifier.size(40.dp))
        } else {
            Box(
                Modifier.size(40.dp).background(accent.copy(0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(app.label.take(1).uppercase(), color = onSurface, fontSize = 16.sp)
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(app.label, color = onSurface, fontSize = 10.sp, maxLines = 1,
            overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}

@Composable
private fun SearchResultRow(
    result: SearchResult,
    apps: List<InstalledApp>,
    onSurface: Color,
    accent: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val appIcon = if (result.type == ResultType.APP) {
            apps.firstOrNull { it.packageName == result.packageName }?.icon
        } else null
        if (appIcon != null) {
            Image(bitmap = appIcon, contentDescription = null, modifier = Modifier.size(28.dp))
        } else {
            Icon(
                when (result.type) {
                    ResultType.SETTING -> Icons.Default.Settings
                    ResultType.FILE -> Icons.Default.Description
                    else -> Icons.Default.Search
                },
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(result.label, color = onSurface, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (result.subtitle.isNotEmpty()) {
                Text(result.subtitle, color = onSurface.copy(0.5f), fontSize = 10.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun BottomClusterBtn(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
        Text(label, color = tint.copy(0.8f), fontSize = 9.sp, textAlign = TextAlign.Center)
    }
}
