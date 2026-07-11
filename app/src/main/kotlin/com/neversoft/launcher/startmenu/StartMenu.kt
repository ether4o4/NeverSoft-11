package com.neversoft.launcher.startmenu

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neversoft.launcher.apps.InstalledApp
import com.neversoft.launcher.apps.InstalledAppsRepository
import com.neversoft.launcher.files.FileOpener
import com.neversoft.launcher.search.AppSearchProvider
import com.neversoft.launcher.search.FileSearchProvider
import com.neversoft.launcher.search.LauncherSearchEngine
import com.neversoft.launcher.search.ResultType
import com.neversoft.launcher.search.SearchResult
import com.neversoft.launcher.search.SettingsSearchProvider
import com.neversoft.launcher.theme.LocalLauncherTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PowerAction { LOCK, SLEEP, SHUT_DOWN, RESTART, SIGN_OUT }

private enum class StartView { PINNED, ALL_APPS, SEARCH }

// Windows 11 Start menu: search box, Pinned grid with "All apps",
// Recommended recent files, user + power footer.
@Composable
fun StartMenu(
    onDismiss: () -> Unit,
    onOpenFileExplorer: () -> Unit,
    onOpenSettings: () -> Unit,
    onPowerAction: (PowerAction) -> Unit,
    searchFocused: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current

    var query by remember { mutableStateOf("") }
    var view by remember { mutableStateOf(if (searchFocused) StartView.SEARCH else StartView.PINNED) }
    var powerMenuOpen by remember { mutableStateOf(false) }

    var apps by remember { mutableStateOf(emptyList<InstalledApp>()) }
    var appsLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        apps = InstalledAppsRepository.loadApps(context)
        appsLoaded = true
    }
    val recentFiles = remember { loadRecentFiles() }

    val searchEngine = remember {
        LauncherSearchEngine(
            listOf(AppSearchProvider { apps }, SettingsSearchProvider(), FileSearchProvider()),
        )
    }
    var results by remember { mutableStateOf(emptyList<SearchResult>()) }
    LaunchedEffect(query, apps) { results = searchEngine.search(query) }

    fun activate(result: SearchResult) {
        when (result.type) {
            ResultType.APP -> result.packageName?.let { InstalledAppsRepository.launch(context, it) }
            ResultType.SETTING -> runCatching {
                context.startActivity(Intent(result.id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            ResultType.FILE -> result.filePath?.let { FileOpener.open(context, File(it)) }
            else -> Unit
        }
        onDismiss()
    }

    BoxWithConstraints(modifier) {
        val columns = if (maxWidth >= 480.dp) 6 else 4
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(theme.menuSurface)
                .border(1.dp, theme.stroke, RoundedCornerShape(8.dp)),
        ) {
            // Search box
            Box(Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 16.dp)) {
                val focusRequester = remember { FocusRequester() }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(theme.inputField)
                        .border(1.dp, theme.stroke, RoundedCornerShape(17.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Search, null, Modifier.size(15.dp), tint = theme.textSecondary)
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            view = when {
                                it.isNotBlank() -> StartView.SEARCH
                                view == StartView.SEARCH -> StartView.PINNED
                                else -> view
                            }
                        },
                        singleLine = true,
                        textStyle = TextStyle(color = theme.text, fontSize = 13.sp),
                        cursorBrush = SolidColor(theme.accent),
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                        decorationBox = { inner ->
                            if (query.isEmpty()) {
                                Text(
                                    "Search for apps, settings, and documents",
                                    color = theme.textSecondary, fontSize = 13.sp,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                                )
                            }
                            inner()
                        },
                    )
                }
                if (searchFocused) {
                    LaunchedEffect(Unit) { focusRequester.requestFocus() }
                }
            }

            Box(Modifier.fillMaxWidth().weight(1f, fill = false)) {
                when (view) {
                    StartView.PINNED -> PinnedView(
                        apps = apps, appsLoaded = appsLoaded, columns = columns,
                        recentFiles = recentFiles,
                        onAllApps = { view = StartView.ALL_APPS },
                        onLaunch = { app ->
                            InstalledAppsRepository.launch(context, app.packageName)
                            onDismiss()
                        },
                        onOpenFile = { file ->
                            FileOpener.open(context, file)
                            onDismiss()
                        },
                    )
                    StartView.ALL_APPS -> AllAppsView(
                        apps = apps,
                        onBack = { view = StartView.PINNED },
                        onLaunch = { app ->
                            InstalledAppsRepository.launch(context, app.packageName)
                            onDismiss()
                        },
                    )
                    StartView.SEARCH -> SearchResultsView(
                        query = query, results = results, apps = apps,
                        onActivate = { activate(it) },
                    )
                }
            }

            // Footer: user + power
            Box(
                Modifier.fillMaxWidth().height(56.dp)
                    .background(if (theme.isDark) Color(0x33000000) else Color(0x14000000)),
            ) {
                Row(
                    Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onPowerAction(PowerAction.LOCK) }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val userName = remember { deviceUserName(context) }
                    Box(
                        Modifier.size(30.dp).clip(CircleShape).background(theme.accent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            userName.take(1).uppercase(), color = theme.accentText,
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(userName, color = theme.text, fontSize = 12.sp)
                }
                Row(
                    Modifier.align(Alignment.CenterEnd).padding(end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Folder, "File Explorer",
                        Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onOpenFileExplorer() }
                            .padding(10.dp),
                        tint = Color(0xFFFFCA28),
                    )
                    Icon(
                        Icons.Outlined.Settings, "Settings",
                        Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onOpenSettings() }
                            .padding(10.dp),
                        tint = theme.text,
                    )
                    Icon(
                        Icons.Outlined.PowerSettingsNew, "Power",
                        Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { powerMenuOpen = !powerMenuOpen }
                            .padding(9.dp),
                        tint = theme.text,
                    )
                }
            }
        }

        // Power flyout
        if (powerMenuOpen) {
            Column(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 60.dp)
                    .width(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.menuSurface)
                    .border(1.dp, theme.stroke, RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp),
            ) {
                PowerMenuItem(Icons.Outlined.Lock, "Lock") { onPowerAction(PowerAction.LOCK) }
                PowerMenuItem(Icons.Outlined.Bedtime, "Sleep") { onPowerAction(PowerAction.SLEEP) }
                PowerMenuItem(Icons.Outlined.PowerSettingsNew, "Shut down") { onPowerAction(PowerAction.SHUT_DOWN) }
                PowerMenuItem(Icons.Outlined.RestartAlt, "Restart") { onPowerAction(PowerAction.RESTART) }
                Box(Modifier.fillMaxWidth().height(1.dp).background(theme.divider))
                PowerMenuItem(Icons.AutoMirrored.Outlined.Logout, "Sign out") { onPowerAction(PowerAction.SIGN_OUT) }
            }
        }
    }
}

@Composable
private fun PowerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, Modifier.size(16.dp), tint = theme.text)
        Spacer(Modifier.width(12.dp))
        Text(label, color = theme.text, fontSize = 13.sp)
    }
}

@Composable
private fun SectionHeader(title: String, actionLabel: String?, onAction: (() -> Unit)?) {
    val theme = LocalLauncherTheme.current
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, color = theme.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.weight(1f))
        if (actionLabel != null && onAction != null) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(theme.card)
                    .border(1.dp, theme.stroke, RoundedCornerShape(4.dp))
                    .clickable { onAction() }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(actionLabel, color = theme.text, fontSize = 11.sp)
                Icon(Icons.Outlined.ChevronRight, null, Modifier.size(13.dp), tint = theme.textSecondary)
            }
        }
    }
}

@Composable
private fun PinnedView(
    apps: List<InstalledApp>,
    appsLoaded: Boolean,
    columns: Int,
    recentFiles: List<File>,
    onAllApps: () -> Unit,
    onLaunch: (InstalledApp) -> Unit,
    onOpenFile: (File) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Column(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        SectionHeader("Pinned", "All apps", onAllApps)
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(252.dp).padding(horizontal = 20.dp)) {
            when {
                !appsLoaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading…", color = theme.textSecondary, fontSize = 12.sp)
                }
                apps.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No apps installed", color = theme.textSecondary, fontSize = 12.sp)
                }
                else -> LazyVerticalGrid(columns = GridCells.Fixed(columns)) {
                    items(apps.take(columns * 3), key = { it.packageName }) { app ->
                        PinnedAppTile(app) { onLaunch(app) }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        SectionHeader("Recommended", null, null)
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().height(128.dp).padding(horizontal = 20.dp)) {
            if (recentFiles.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Your recent files will show here",
                        color = theme.textSecondary, fontSize = 12.sp,
                    )
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(recentFiles.take(6), key = { it.absolutePath }) { file ->
                        RecommendedTile(file) { onOpenFile(file) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PinnedAppTile(app: InstalledApp, onClick: () -> Unit) {
    val theme = LocalLauncherTheme.current
    Column(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (app.icon != null) {
            Image(bitmap = app.icon, contentDescription = app.label, modifier = Modifier.size(32.dp))
        } else {
            Box(
                Modifier.size(32.dp).background(theme.card, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(app.label.take(1).uppercase(), color = theme.text, fontSize = 14.sp)
            }
        }
        Spacer(Modifier.height(5.dp))
        Text(
            app.label, color = theme.text, fontSize = 11.sp, maxLines = 1,
            overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
    }
}

@Composable
private fun RecommendedTile(file: File, onClick: () -> Unit) {
    val theme = LocalLauncherTheme.current
    Row(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Outlined.Description, null, Modifier.size(26.dp),
            tint = theme.accent,
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                file.name, color = theme.text, fontSize = 12.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
            Text(
                SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(file.lastModified())),
                color = theme.textSecondary, fontSize = 10.sp,
            )
        }
    }
}

@Composable
private fun AllAppsView(
    apps: List<InstalledApp>,
    onBack: () -> Unit,
    onLaunch: (InstalledApp) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Column(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        SectionHeader("All apps", "Back", onBack)
        Spacer(Modifier.height(8.dp))
        val grouped = remember(apps) {
            apps.groupBy { app ->
                val c = app.label.firstOrNull()?.uppercaseChar() ?: '#'
                if (c.isLetter()) c.toString() else "#"
            }.toSortedMap()
        }
        LazyColumn(Modifier.fillMaxWidth().height(430.dp).padding(horizontal = 20.dp)) {
            grouped.forEach { (letter, letterApps) ->
                item(key = "header_$letter") {
                    Text(
                        letter, color = theme.accent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
                items(letterApps, key = { it.packageName }) { app ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onLaunch(app) }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (app.icon != null) {
                            Image(bitmap = app.icon, contentDescription = null, modifier = Modifier.size(24.dp))
                        } else {
                            Box(
                                Modifier.size(24.dp).background(theme.card, RoundedCornerShape(5.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(app.label.take(1).uppercase(), color = theme.text, fontSize = 11.sp)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            app.label, color = theme.text, fontSize = 13.sp,
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultsView(
    query: String,
    results: List<SearchResult>,
    apps: List<InstalledApp>,
    onActivate: (SearchResult) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Column(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        SectionHeader(if (query.isBlank()) "Search" else "Best match", null, null)
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().height(430.dp).padding(horizontal = 20.dp)) {
            if (results.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (query.isBlank()) "Start typing to search"
                        else "No results for “$query”",
                        color = theme.textSecondary, fontSize = 13.sp,
                    )
                }
            } else {
                LazyColumn {
                    items(results, key = { "${it.type}_${it.id}" }) { result ->
                        val appIcon = if (result.type == ResultType.APP) {
                            apps.firstOrNull { it.packageName == result.packageName }?.icon
                        } else null
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onActivate(result) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (appIcon != null) {
                                Image(bitmap = appIcon, contentDescription = null, modifier = Modifier.size(28.dp))
                            } else {
                                Icon(
                                    when (result.type) {
                                        ResultType.SETTING -> Icons.Outlined.Settings
                                        ResultType.FILE -> Icons.AutoMirrored.Outlined.InsertDriveFile
                                        else -> Icons.Outlined.Search
                                    },
                                    null, Modifier.size(24.dp), tint = theme.accent,
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    result.label, color = theme.text, fontSize = 13.sp,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                                )
                                if (result.subtitle.isNotEmpty()) {
                                    Text(result.subtitle, color = theme.textSecondary, fontSize = 11.sp, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun deviceUserName(context: Context): String =
    runCatching { Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME) }
        .getOrNull()
        ?.takeIf { it.isNotBlank() }
        ?: Build.MODEL

private fun loadRecentFiles(): List<File> = runCatching {
    listOf(
        Environment.DIRECTORY_DOWNLOADS, Environment.DIRECTORY_DOCUMENTS,
        Environment.DIRECTORY_PICTURES, Environment.DIRECTORY_DCIM,
    )
        .map { Environment.getExternalStoragePublicDirectory(it) }
        .flatMap { it.listFiles()?.toList() ?: emptyList() }
        .filter { it.isFile && !it.name.startsWith(".") }
        .sortedByDescending { it.lastModified() }
        .take(6)
}.getOrDefault(emptyList())
