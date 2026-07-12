package com.neversoft.launcher.startmenu

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    currentSize: androidx.compose.ui.unit.DpSize = androidx.compose.ui.unit.DpSize.Unspecified,
    onResize: (androidx.compose.ui.unit.DpSize) -> Unit = {},
    onResizeEnd: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current

    val scope = androidx.compose.runtime.rememberCoroutineScope()
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

    // User-chosen pinned apps (package names, in pin order)
    var pins by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(Unit) {
        com.neversoft.launcher.data.AppSettings.startPinsFlow(context).collect { json ->
            pins = runCatching {
                val arr = org.json.JSONArray(json)
                List(arr.length()) { arr.getString(it) }
            }.getOrDefault(emptyList())
        }
    }

    fun setPins(newPins: List<String>) {
        pins = newPins
        scope.launch {
            com.neversoft.launcher.data.AppSettings.setStartPins(
                context, org.json.JSONArray(newPins).toString(),
            )
        }
    }

    // Taskbar pins, shared with the taskbar via DataStore
    var dockPins by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(Unit) {
        com.neversoft.launcher.data.AppSettings.dockPinsFlow(context).collect { json ->
            dockPins = runCatching {
                val arr = org.json.JSONArray(json)
                List(arr.length()) { arr.getString(it) }
            }.getOrDefault(emptyList())
        }
    }

    fun toggleDockPin(pkg: String) {
        val newPins = if (dockPins.contains(pkg)) dockPins - pkg else dockPins + pkg
        dockPins = newPins
        scope.launch {
            com.neversoft.launcher.data.AppSettings.setDockPins(
                context, org.json.JSONArray(newPins).toString(),
            )
        }
    }

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

        // One-time migration: materialize the default app set as real pins so
        // every tile is individually removable from then on
        LaunchedEffect(appsLoaded) {
            if (!appsLoaded || apps.isEmpty()) return@LaunchedEffect
            val seeded = com.neversoft.launcher.data.AppSettings.startPinsSeededFlow(context).first()
            if (!seeded) {
                val stored = runCatching {
                    val arr = org.json.JSONArray(
                        com.neversoft.launcher.data.AppSettings.startPinsFlow(context).first(),
                    )
                    List(arr.length()) { arr.getString(it) }
                }.getOrDefault(emptyList())
                if (stored.isEmpty()) {
                    setPins(apps.take(columns * 3).map { it.packageName })
                }
                com.neversoft.launcher.data.AppSettings.setStartPinsSeeded(context)
            }
        }
        Column(
            Modifier
                .fillMaxSize()
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

            Box(Modifier.fillMaxWidth().weight(1f)) {
                when (view) {
                    StartView.PINNED -> PinnedView(
                        apps = apps, appsLoaded = appsLoaded, columns = columns,
                        pins = pins,
                        dockPins = dockPins,
                        recentFiles = recentFiles,
                        onAllApps = { view = StartView.ALL_APPS },
                        onLaunch = { app ->
                            InstalledAppsRepository.launch(context, app.packageName)
                            onDismiss()
                        },
                        onUnpin = { pkg -> setPins(pins - pkg) },
                        onToggleDockPin = { pkg -> toggleDockPin(pkg) },
                        onOpenFile = { file ->
                            FileOpener.open(context, file)
                            onDismiss()
                        },
                    )
                    StartView.ALL_APPS -> AllAppsView(
                        apps = apps,
                        pins = pins,
                        dockPins = dockPins,
                        onBack = { view = StartView.PINNED },
                        onLaunch = { app ->
                            InstalledAppsRepository.launch(context, app.packageName)
                            onDismiss()
                        },
                        onTogglePin = { pkg ->
                            setPins(if (pins.contains(pkg)) pins - pkg else pins + pkg)
                        },
                        onToggleDockPin = { pkg -> toggleDockPin(pkg) },
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

        // Resize button: the menu is anchored bottom-left, so its top-right
        // corner drags to resize width and height. A baseline is captured at
        // drag start and raw deltas accumulate locally, so it can't snap back.
        val latestSize by androidx.compose.runtime.rememberUpdatedState(currentSize)
        val latestOnResize by androidx.compose.runtime.rememberUpdatedState(onResize)
        val latestOnResizeEnd by androidx.compose.runtime.rememberUpdatedState(onResizeEnd)
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .size(40.dp)
                .pointerInput(Unit) {
                    var baseW = 0f
                    var baseH = 0f
                    var acc = androidx.compose.ui.geometry.Offset.Zero
                    detectDragGestures(
                        onDragStart = {
                            baseW = latestSize.width.toPx()
                            baseH = latestSize.height.toPx()
                            acc = androidx.compose.ui.geometry.Offset.Zero
                        },
                        onDrag = { change, drag ->
                            change.consume()
                            acc += drag
                            latestOnResize(
                                androidx.compose.ui.unit.DpSize(
                                    (baseW + acc.x).toDp(),
                                    (baseH - acc.y).toDp(),
                                ),
                            )
                        },
                        onDragEnd = { latestOnResizeEnd() },
                    )
                },
        ) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 5.dp, end = 5.dp)
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(theme.card.copy(alpha = 0.9f))
                    .border(1.dp, theme.stroke, RoundedCornerShape(6.dp)),
            ) {
            androidx.compose.foundation.Canvas(
                Modifier.align(Alignment.Center).size(11.dp),
            ) {
                val stroke = 1.4.dp.toPx()
                drawLine(
                    theme.textSecondary,
                    androidx.compose.ui.geometry.Offset(0f, 0f),
                    androidx.compose.ui.geometry.Offset(size.width, 0f),
                    stroke,
                )
                drawLine(
                    theme.textSecondary,
                    androidx.compose.ui.geometry.Offset(size.width, 0f),
                    androidx.compose.ui.geometry.Offset(size.width, size.height),
                    stroke,
                )
            }
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
    pins: List<String>,
    dockPins: List<String>,
    recentFiles: List<File>,
    onAllApps: () -> Unit,
    onLaunch: (InstalledApp) -> Unit,
    onUnpin: (String) -> Unit,
    onToggleDockPin: (String) -> Unit,
    onOpenFile: (File) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    // The pinned grid IS the pins list — every tile is removable, and any
    // app can be added from All apps
    val pinnedApps = pins.mapNotNull { pkg -> apps.firstOrNull { it.packageName == pkg } }

    Column(Modifier.fillMaxSize().padding(bottom = 10.dp)) {
        SectionHeader("Pinned", "All apps", onAllApps)
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 20.dp)) {
            when {
                !appsLoaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading…", color = theme.textSecondary, fontSize = 12.sp)
                }
                pinnedApps.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Long-press an app in All apps to pin it here",
                        color = theme.textSecondary, fontSize = 12.sp,
                    )
                }
                else -> LazyVerticalGrid(columns = GridCells.Fixed(columns)) {
                    items(pinnedApps, key = { it.packageName }) { app ->
                        PinnedAppTile(
                            app = app,
                            isDockPinned = dockPins.contains(app.packageName),
                            onClick = { onLaunch(app) },
                            onUnpin = { onUnpin(app.packageName) },
                            onToggleDockPin = { onToggleDockPin(app.packageName) },
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        SectionHeader("Recommended", null, null)
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().height(120.dp).padding(horizontal = 20.dp)) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PinnedAppTile(
    app: InstalledApp,
    isDockPinned: Boolean,
    onClick: () -> Unit,
    onUnpin: () -> Unit,
    onToggleDockPin: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    var menuOpen by remember { mutableStateOf(false) }
    Box {
        Column(
            Modifier
                .clip(RoundedCornerShape(4.dp))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { menuOpen = true },
                )
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
        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { menuOpen = false },
            shape = RoundedCornerShape(8.dp),
            containerColor = theme.menuSurface,
        ) {
            ContextMenuItem(Icons.Outlined.PushPin, "Unpin from Start") {
                menuOpen = false
                onUnpin()
            }
            ContextMenuItem(
                Icons.Outlined.PushPin,
                if (isDockPinned) "Unpin from taskbar" else "Pin to taskbar",
            ) {
                menuOpen = false
                onToggleDockPin()
            }
        }
    }
}

@Composable
private fun ContextMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Row(
        Modifier
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, Modifier.size(15.dp), tint = theme.text)
        Spacer(Modifier.width(10.dp))
        Text(label, color = theme.text, fontSize = 13.sp)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AllAppsView(
    apps: List<InstalledApp>,
    pins: List<String>,
    dockPins: List<String>,
    onBack: () -> Unit,
    onLaunch: (InstalledApp) -> Unit,
    onTogglePin: (String) -> Unit,
    onToggleDockPin: (String) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Column(Modifier.fillMaxSize().padding(bottom = 10.dp)) {
        SectionHeader("All apps", "Back", onBack)
        Spacer(Modifier.height(8.dp))
        val grouped = remember(apps) {
            apps.groupBy { app ->
                val c = app.label.firstOrNull()?.uppercaseChar() ?: '#'
                if (c.isLetter()) c.toString() else "#"
            }.toSortedMap()
        }
        LazyColumn(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 20.dp)) {
            grouped.forEach { (letter, letterApps) ->
                item(key = "header_$letter") {
                    Text(
                        letter, color = theme.accent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
                items(letterApps, key = { it.packageName }) { app ->
                    var menuOpen by remember(app.packageName) { mutableStateOf(false) }
                    val isPinned = pins.contains(app.packageName)
                    Box {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .combinedClickable(
                                    onClick = { onLaunch(app) },
                                    onLongClick = { menuOpen = true },
                                )
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
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false },
                            shape = RoundedCornerShape(8.dp),
                            containerColor = theme.menuSurface,
                        ) {
                            ContextMenuItem(
                                Icons.Outlined.PushPin,
                                if (isPinned) "Unpin from Start" else "Pin to Start",
                            ) {
                                menuOpen = false
                                onTogglePin(app.packageName)
                            }
                            ContextMenuItem(
                                Icons.Outlined.PushPin,
                                if (dockPins.contains(app.packageName)) "Unpin from taskbar"
                                else "Pin to taskbar",
                            ) {
                                menuOpen = false
                                onToggleDockPin(app.packageName)
                            }
                        }
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
    Column(Modifier.fillMaxSize().padding(bottom = 10.dp)) {
        SectionHeader(if (query.isBlank()) "Search" else "Best match", null, null)
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 20.dp)) {
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
