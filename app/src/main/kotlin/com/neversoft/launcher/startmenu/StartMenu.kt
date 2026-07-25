package com.neversoft.launcher.startmenu

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.AddToHomeScreen
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.PhotoSizeSelectLarge
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import com.neversoft.launcher.files.ImageStore
import com.neversoft.launcher.search.AppSearchProvider
import com.neversoft.launcher.search.FileSearchProvider
import com.neversoft.launcher.search.LauncherSearchEngine
import com.neversoft.launcher.search.ResultType
import com.neversoft.launcher.search.SearchResult
import com.neversoft.launcher.search.SettingsSearchProvider
import com.neversoft.launcher.theme.LocalLauncherTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File

enum class PowerAction { LOCK, SLEEP, SHUT_DOWN, RESTART, SIGN_OUT }

private enum class StartView { PINNED, ALL_APPS, SEARCH }

// Five pinned-tile sizes as a fraction of the Start menu's own width (its
// "page"), stepping at an equal rate from 1/16 (level 1) to 1/2 (level 5).
private val START_ICON_SIZE_FRACTIONS = listOf(0.0625f, 0.171875f, 0.28125f, 0.390625f, 0.5f)
private const val START_ICON_SIZE_COUNT = 5

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

    // Start-menu folders (4 folders, up to 4 apps each)
    var folders by remember { mutableStateOf(StartFolders.parse("")) }
    LaunchedEffect(Unit) {
        com.neversoft.launcher.data.AppSettings.startFoldersFlow(context).collect { json ->
            folders = StartFolders.parse(json)
        }
    }

    fun addToFolder(pkg: String, folderIndex: Int) {
        val updated = folders.mapIndexed { i, f ->
            if (i == folderIndex && !f.apps.contains(pkg) && f.apps.size < StartFolders.CAPACITY) {
                f.copy(apps = f.apps + pkg)
            } else f
        }
        folders = updated
        scope.launch {
            com.neversoft.launcher.data.AppSettings.setStartFolders(context, StartFolders.serialize(updated))
        }
    }

    fun addToDesktop(app: InstalledApp) {
        scope.launch {
            runCatching {
                val raw = com.neversoft.launcher.data.AppSettings.desktopItemsFlow(context).first()
                val arr = if (raw.isBlank()) org.json.JSONArray() else org.json.JSONArray(raw)
                val id = "app_${app.packageName}"
                var exists = false
                for (i in 0 until arr.length()) {
                    if (arr.getJSONObject(i).optString("id") == id) { exists = true; break }
                }
                if (!exists) {
                    arr.put(
                        org.json.JSONObject()
                            .put("id", id).put("kind", "app")
                            .put("label", app.label).put("pkg", app.packageName),
                    )
                    com.neversoft.launcher.data.AppSettings.setDesktopItems(context, arr.toString())
                }
            }
        }
    }

    fun uninstall(pkg: String) {
        runCatching {
            context.startActivity(
                Intent(Intent.ACTION_DELETE, android.net.Uri.parse("package:$pkg"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }

    fun renameFolder(index: Int, rawName: String) {
        val name = rawName.trim().ifEmpty { return }
        val updated = folders.mapIndexed { i, f -> if (i == index) f.copy(name = name) else f }
        folders = updated
        scope.launch {
            com.neversoft.launcher.data.AppSettings.setStartFolders(context, StartFolders.serialize(updated))
        }
    }

    fun setFolderImage(index: Int, path: String) {
        val updated = folders.mapIndexed { i, f -> if (i == index) f.copy(image = path) else f }
        folders = updated
        scope.launch {
            com.neversoft.launcher.data.AppSettings.setStartFolders(context, StartFolders.serialize(updated))
        }
    }

    // Photo picker for a Start-menu folder; the target folder is captured just
    // before launching so the callback knows which one to update.
    var photoTargetFolder by remember { mutableStateOf<Int?>(null) }
    val folderPhotoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri ->
        val idx = photoTargetFolder
        photoTargetFolder = null
        if (uri != null && idx != null) {
            scope.launch {
                val path = withContext(Dispatchers.IO) {
                    ImageStore.importImage(context, uri, "startfolder$idx")
                }
                if (path != null) setFolderImage(idx, path)
            }
        }
    }

    fun addToQuickApps(pkg: String) {
        scope.launch {
            val current = runCatching {
                val arr = JSONArray(com.neversoft.launcher.data.AppSettings.quickAppsFlow(context).first())
                List(arr.length()) { arr.getString(it) }
            }.getOrDefault(emptyList())
            if (!current.contains(pkg)) {
                val updated = (current + pkg).take(6)
                com.neversoft.launcher.data.AppSettings.setQuickApps(context, JSONArray(updated).toString())
            }
        }
    }

    // Per-app custom icon: pick a photo for one app, or reset to default.
    val iconOverridesJson by com.neversoft.launcher.data.AppSettings
        .appIconOverridesFlow(context).collectAsState(initial = "{}")
    val overriddenPkgs = remember(iconOverridesJson) {
        runCatching {
            val o = org.json.JSONObject(iconOverridesJson)
            o.keys().asSequence().toSet()
        }.getOrDefault(emptySet())
    }
    var iconTargetPkg by remember { mutableStateOf<String?>(null) }
    val appIconPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri ->
        val pkg = iconTargetPkg
        iconTargetPkg = null
        if (uri != null && pkg != null) {
            scope.launch {
                val path = withContext(Dispatchers.IO) {
                    ImageStore.importImage(context, uri, "appicon-$pkg")
                }
                if (path != null) {
                    com.neversoft.launcher.data.AppSettings.setAppIcon(context, pkg, path)
                    apps = InstalledAppsRepository.loadApps(context)
                }
            }
        }
    }
    fun resetAppIcon(pkg: String) {
        scope.launch {
            com.neversoft.launcher.data.AppSettings.clearAppIcon(context, pkg)
            apps = InstalledAppsRepository.loadApps(context)
        }
    }

    var addAppsOpen by remember { mutableStateOf(false) }
    var helpOpen by remember { mutableStateOf(false) }

    // Bulk actions for multi-select in All apps
    fun bulkAddToFolder(idx: Int, pkgs: List<String>) {
        var updated = folders
        pkgs.forEach { pkg ->
            updated = updated.mapIndexed { i, f ->
                if (i == idx && !f.apps.contains(pkg) && f.apps.size < StartFolders.CAPACITY) {
                    f.copy(apps = f.apps + pkg)
                } else f
            }
        }
        folders = updated
        scope.launch {
            com.neversoft.launcher.data.AppSettings.setStartFolders(context, StartFolders.serialize(updated))
        }
        android.widget.Toast.makeText(context, "Added to ${updated[idx].name}", android.widget.Toast.LENGTH_SHORT).show()
    }

    fun bulkAddToDesktop(pkgs: List<String>) {
        scope.launch {
            runCatching {
                val raw = com.neversoft.launcher.data.AppSettings.desktopItemsFlow(context).first()
                val arr = if (raw.isBlank()) JSONArray() else JSONArray(raw)
                val existing = HashSet<String>()
                for (i in 0 until arr.length()) existing.add(arr.getJSONObject(i).optString("id"))
                val byPkg = apps.associateBy { it.packageName }
                var added = 0
                pkgs.forEach { pkg ->
                    val id = "app_$pkg"
                    if (!existing.contains(id)) {
                        byPkg[pkg]?.let { app ->
                            arr.put(
                                org.json.JSONObject()
                                    .put("id", id).put("kind", "app")
                                    .put("label", app.label).put("pkg", pkg),
                            )
                            added++
                        }
                    }
                }
                if (added > 0) com.neversoft.launcher.data.AppSettings.setDesktopItems(context, arr.toString())
                android.widget.Toast.makeText(
                    context,
                    if (added > 0) "Added $added to desktop" else "Already on desktop",
                    android.widget.Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    // Bulk change icon: pick ONE photo, apply it to every selected app
    var bulkIconPkgs by remember { mutableStateOf<List<String>>(emptyList()) }
    val bulkIconPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri ->
        val pkgs = bulkIconPkgs
        bulkIconPkgs = emptyList()
        if (uri != null && pkgs.isNotEmpty()) {
            scope.launch {
                val path = withContext(Dispatchers.IO) {
                    // Timestamped prefix so later bulk imports never delete a
                    // file that earlier-selected apps still point to
                    ImageStore.importImage(context, uri, "appicon-bulk-${System.currentTimeMillis()}")
                }
                if (path != null) {
                    com.neversoft.launcher.data.AppSettings.setAppIcons(context, pkgs.associateWith { path })
                    apps = InstalledAppsRepository.loadApps(context)
                    android.widget.Toast.makeText(
                        context, "Icon changed for ${pkgs.size} app${if (pkgs.size == 1) "" else "s"}",
                        android.widget.Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    // Bulk icon size: Start tiles for the selected apps, plus any copies of
    // them on the desktops
    fun bulkSetSize(level: Int, pkgs: List<String>) {
        val pkgSet = pkgs.toSet()
        scope.launch {
            com.neversoft.launcher.data.AppSettings.setStartIconSizes(context, pkgs.associateWith { level })
            for (page in 1..4) {
                runCatching {
                    val raw = com.neversoft.launcher.data.AppSettings.desktopItemsFlow(context, page).first()
                    if (raw.isBlank()) return@runCatching
                    val arr = JSONArray(raw)
                    var changed = false
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        if (o.optString("pkg") in pkgSet) { o.put("size", level); changed = true }
                    }
                    if (changed) {
                        com.neversoft.launcher.data.AppSettings.setDesktopItems(context, arr.toString(), page)
                    }
                }
            }
            android.widget.Toast.makeText(
                context, "Size $level set for ${pkgs.size} app${if (pkgs.size == 1) "" else "s"}",
                android.widget.Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun bulkAddToQuick(pkgs: List<String>) {
        scope.launch {
            val current = runCatching {
                val arr = JSONArray(com.neversoft.launcher.data.AppSettings.quickAppsFlow(context).first())
                List(arr.length()) { arr.getString(it) }
            }.getOrDefault(emptyList())
            val updated = (current + pkgs).distinct().take(6)
            com.neversoft.launcher.data.AppSettings.setQuickApps(context, JSONArray(updated).toString())
            android.widget.Toast.makeText(
                context, "Quick apps: ${updated.size}/6", android.widget.Toast.LENGTH_SHORT,
            ).show()
        }
    }

    // Per-app Start-menu pinned-tile sizes (1..5), on the Start menu's own scale
    val startIconSizesJson by com.neversoft.launcher.data.AppSettings
        .startIconSizesFlow(context).collectAsState(initial = "{}")
    val startIconSizes = remember(startIconSizesJson) {
        runCatching {
            val o = org.json.JSONObject(startIconSizesJson)
            buildMap {
                val keys = o.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    put(k, o.optInt(k, 1).coerceIn(1, START_ICON_SIZE_COUNT))
                }
            }
        }.getOrDefault(emptyMap())
    }
    fun setStartIconSize(pkg: String, level: Int) {
        scope.launch {
            com.neversoft.launcher.data.AppSettings.setStartIconSize(
                context, pkg, level.coerceIn(1, START_ICON_SIZE_COUNT),
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
                        folders = folders,
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
                        onOpenFileExplorer = onOpenFileExplorer,
                        onRenameFolder = { idx, name -> renameFolder(idx, name) },
                        onPickFolderPhoto = { idx ->
                            photoTargetFolder = idx
                            folderPhotoPicker.launch("image/*")
                        },
                        onRemoveFolderPhoto = { idx -> setFolderImage(idx, "") },
                        startIconSizes = startIconSizes,
                        onSetStartIconSize = { pkg, level -> setStartIconSize(pkg, level) },
                        onAddApps = { addAppsOpen = true },
                    )
                    StartView.ALL_APPS -> AllAppsView(
                        apps = apps,
                        pins = pins,
                        dockPins = dockPins,
                        folders = folders,
                        onBack = { view = StartView.PINNED },
                        onLaunch = { app ->
                            InstalledAppsRepository.launch(context, app.packageName)
                            onDismiss()
                        },
                        onTogglePin = { pkg ->
                            setPins(if (pins.contains(pkg)) pins - pkg else pins + pkg)
                        },
                        onToggleDockPin = { pkg -> toggleDockPin(pkg) },
                        onAddToDesktop = { app -> addToDesktop(app) },
                        onAddToFolder = { pkg, idx -> addToFolder(pkg, idx) },
                        onUninstall = { pkg -> uninstall(pkg) },
                        onAddToQuickApps = { pkg -> addToQuickApps(pkg) },
                        onChangeIcon = { pkg ->
                            iconTargetPkg = pkg
                            appIconPicker.launch("image/*")
                        },
                        onResetIcon = { pkg -> resetAppIcon(pkg) },
                        hasCustomIcon = { pkg -> overriddenPkgs.contains(pkg) },
                        onBulkToFolder = { idx, pkgs -> bulkAddToFolder(idx, pkgs) },
                        onBulkToDesktop = { pkgs -> bulkAddToDesktop(pkgs) },
                        onBulkToQuick = { pkgs -> bulkAddToQuick(pkgs) },
                        onBulkPin = { pkgs -> setPins((pins + pkgs).distinct()) },
                        onBulkChangeIcon = { pkgs ->
                            bulkIconPkgs = pkgs
                            bulkIconPicker.launch("image/*")
                        },
                        onBulkSize = { level, pkgs -> bulkSetSize(level, pkgs) },
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
                        Icons.AutoMirrored.Outlined.HelpOutline, "Help",
                        Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { helpOpen = true }
                            .padding(10.dp),
                        tint = theme.text,
                    )
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

        // Multi-select "Add apps" -> pin to Start
        if (addAppsOpen) {
            com.neversoft.launcher.apps.AppPickerDialog(
                title = "Add apps to Start",
                apps = apps,
                onConfirm = { pkgs -> setPins((pins + pkgs).distinct()) },
                onDismiss = { addAppsOpen = false },
            )
        }

        // Guided help book
        if (helpOpen) {
            HelpDialog(onDismiss = { helpOpen = false })
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
private fun HeaderChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Row(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(theme.card)
            .border(1.dp, theme.stroke, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = theme.text, fontSize = 11.sp)
        Icon(icon, null, Modifier.size(13.dp), tint = theme.textSecondary)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PinnedView(
    apps: List<InstalledApp>,
    appsLoaded: Boolean,
    columns: Int,
    pins: List<String>,
    dockPins: List<String>,
    folders: List<StartFolder>,
    onAllApps: () -> Unit,
    onLaunch: (InstalledApp) -> Unit,
    onUnpin: (String) -> Unit,
    onToggleDockPin: (String) -> Unit,
    onOpenFile: (File) -> Unit,
    onOpenFileExplorer: () -> Unit,
    onRenameFolder: (Int, String) -> Unit,
    onPickFolderPhoto: (Int) -> Unit,
    onRemoveFolderPhoto: (Int) -> Unit,
    startIconSizes: Map<String, Int>,
    onSetStartIconSize: (String, Int) -> Unit,
    onAddApps: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    val perRow = columns
    val pinnedApps = pins.mapNotNull { pkg -> apps.firstOrNull { it.packageName == pkg } }

    // Recent rows, resolved off the main thread
    var recentApps by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    var recentlyInstalled by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    var recentlyOpened by remember { mutableStateOf<List<RecentFile>>(emptyList()) }
    var recentlyAdded by remember { mutableStateOf<List<RecentFile>>(emptyList()) }
    var recentFiles by remember { mutableStateOf<List<RecentFile>>(emptyList()) }
    var recentlyUsed by remember { mutableStateOf<List<RecentFile>>(emptyList()) }
    LaunchedEffect(apps) {
        if (apps.isNotEmpty()) {
            recentApps = StartData.recentApps(context, apps, perRow)
            recentlyInstalled = StartData.recentlyInstalledApps(context, apps, perRow)
        }
        recentlyOpened = StartData.recentlyOpenedFiles(context, perRow)
        recentlyAdded = StartData.recentlyAddedFiles(perRow)
        recentFiles = StartData.recentFilesByDateAdded(context, perRow)
        recentlyUsed = StartData.recentlyUsedFiles(context, perRow)
    }

    var openFolder by remember { mutableStateOf<Int?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 12.dp),
    ) {
        // ——— Pinned: resizable apps (wrap to fit) + a row of 4 folders ———
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Pinned", color = theme.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            HeaderChip("Add apps", Icons.Outlined.Add, onAddApps)
            Spacer(Modifier.width(8.dp))
            HeaderChip("All apps", Icons.Outlined.ChevronRight, onAllApps)
        }
        Spacer(Modifier.height(10.dp))
        if (pinnedApps.isEmpty()) {
            Text(
                if (!appsLoaded) "Loading…" else "Long-press an app in All apps to pin it here",
                color = theme.textSecondary, fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            )
        } else {
            BoxWithConstraints(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                // The Start menu's own "page" for the size scale is this panel's width.
                val pageWidth = maxWidth
                FlowRow(Modifier.fillMaxWidth()) {
                    pinnedApps.forEach { app ->
                        val level = (startIconSizes[app.packageName] ?: 1).coerceIn(1, START_ICON_SIZE_COUNT)
                        PinnedAppTile(
                            app = app,
                            isDockPinned = dockPins.contains(app.packageName),
                            onClick = { onLaunch(app) },
                            onUnpin = { onUnpin(app.packageName) },
                            onToggleDockPin = { onToggleDockPin(app.packageName) },
                            iconSizeDp = pageWidth * START_ICON_SIZE_FRACTIONS[level - 1],
                            sizeLevel = level,
                            onSetSize = { lvl -> onSetStartIconSize(app.packageName, lvl) },
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            folders.take(StartFolders.COUNT).forEachIndexed { index, folder ->
                Box(Modifier.weight(1f)) {
                    FolderTile(folder = folder, apps = apps) { openFolder = index }
                }
            }
        }

        // ——— Recent apps ———
        RecentAppSection("Recent apps", recentApps, onSeeMore = onAllApps, perRow = perRow, onLaunch = onLaunch)
        // ——— Recently opened files ———
        RecentFileSection("Recently opened", recentlyOpened, onSeeMore = onOpenFileExplorer, perRow = perRow, onOpen = onOpenFile)
        // ——— Recently added files ———
        RecentFileSection("Recently added", recentlyAdded, onSeeMore = onOpenFileExplorer, perRow = perRow, onOpen = onOpenFile)
        // ——— Recent files (device-wide, by date added) ———
        RecentFileSection("Recent files", recentFiles, onSeeMore = onOpenFileExplorer, perRow = perRow, onOpen = onOpenFile)
        // ——— Recently used files (device-wide, by date modified) ———
        RecentFileSection("Recently used files", recentlyUsed, onSeeMore = onOpenFileExplorer, perRow = perRow, onOpen = onOpenFile)
        // ——— Recently installed apps ———
        RecentAppSection("Recently installed", recentlyInstalled, onSeeMore = onAllApps, perRow = perRow, onLaunch = onLaunch)
    }

    // Folder contents dialog (with rename + custom photo)
    openFolder?.let { idx ->
        val folder = folders.getOrNull(idx) ?: return@let
        val folderApps = folder.apps.mapNotNull { pkg -> apps.firstOrNull { it.packageName == pkg } }
        androidx.compose.ui.window.Dialog(onDismissRequest = { openFolder = null }) {
            Column(
                Modifier
                    .width(300.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.menuSurface)
                    .border(1.dp, theme.stroke, RoundedCornerShape(10.dp))
                    .padding(18.dp),
            ) {
                // Editable name
                var editName by remember(folder.name) { mutableStateOf(folder.name) }
                Text("Folder name", color = theme.textSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(theme.inputField)
                            .border(1.dp, theme.stroke, RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                    ) {
                        BasicTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            singleLine = true,
                            textStyle = TextStyle(color = theme.text, fontSize = 13.sp),
                            cursorBrush = SolidColor(theme.accent),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    ChipButton("Save") { onRenameFolder(idx, editName) }
                }

                Spacer(Modifier.height(12.dp))
                Text("Folder photo", color = theme.textSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipButton("Choose photo") { onPickFolderPhoto(idx) }
                    if (folder.image.isNotEmpty()) {
                        ChipButton("Remove") { onRemoveFolderPhoto(idx) }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text("Apps", color = theme.textSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                if (folderApps.isEmpty()) {
                    Text(
                        "Empty. In All apps, long-press an app and choose \"Add to folder.\"",
                        color = theme.textSecondary, fontSize = 12.sp, lineHeight = 16.sp,
                    )
                } else {
                    // Up to 10 apps, laid out 4 per row
                    folderApps.chunked(4).forEach { rowApps ->
                        Row(Modifier.fillMaxWidth()) {
                            rowApps.forEach { app ->
                                Box(Modifier.weight(1f)) {
                                    PinnedAppTile(
                                        app = app, isDockPinned = false,
                                        onClick = { openFolder = null; onLaunch(app) },
                                        onUnpin = {}, onToggleDockPin = {},
                                    )
                                }
                            }
                            repeat(4 - rowApps.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipButton(label: String, onClick: () -> Unit) {
    val theme = LocalLauncherTheme.current
    Box(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(theme.card)
            .border(1.dp, theme.stroke, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(label, color = theme.text, fontSize = 12.sp)
    }
}

@Composable
private fun RecentAppSection(
    title: String,
    apps: List<InstalledApp>,
    onSeeMore: () -> Unit,
    perRow: Int,
    onLaunch: (InstalledApp) -> Unit,
) {
    if (apps.isEmpty()) return
    val theme = LocalLauncherTheme.current
    Spacer(Modifier.height(12.dp))
    SectionHeader(title, "See more", onSeeMore)
    Spacer(Modifier.height(6.dp))
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        apps.take(perRow).forEach { app ->
            Box(Modifier.weight(1f)) {
                PinnedAppTile(
                    app = app, isDockPinned = false,
                    onClick = { onLaunch(app) }, onUnpin = {}, onToggleDockPin = {},
                )
            }
        }
        repeat(perRow - apps.take(perRow).size) { Spacer(Modifier.weight(1f)) }
    }
}

@Composable
private fun RecentFileSection(
    title: String,
    files: List<RecentFile>,
    onSeeMore: () -> Unit,
    perRow: Int,
    onOpen: (File) -> Unit,
) {
    if (files.isEmpty()) return
    Spacer(Modifier.height(12.dp))
    SectionHeader(title, "See more", onSeeMore)
    Spacer(Modifier.height(6.dp))
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        files.take(perRow).forEach { rf ->
            Box(Modifier.weight(1f)) {
                MiniFileTile(rf) { onOpen(rf.file) }
            }
        }
        repeat(perRow - files.take(perRow).size) { Spacer(Modifier.weight(1f)) }
    }
}

@Composable
private fun MiniFileTile(rf: RecentFile, onClick: () -> Unit) {
    val theme = LocalLauncherTheme.current
    Column(
        Modifier.clip(RoundedCornerShape(4.dp)).clickable { onClick() }.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Outlined.Description, null, Modifier.size(30.dp), tint = theme.accent)
        Spacer(Modifier.height(4.dp))
        Text(
            rf.name, color = theme.text, fontSize = 10.sp, maxLines = 1,
            overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
    }
}

@Composable
private fun FolderTile(folder: StartFolder, apps: List<InstalledApp>, onClick: () -> Unit) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    val icons = folder.apps.mapNotNull { pkg -> apps.firstOrNull { it.packageName == pkg }?.icon }

    // Decode a custom folder photo off the main thread, if set
    var photo by remember(folder.image) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(folder.image) {
        photo = if (folder.image.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                ImageStore.decodeSampled(folder.image, 128)?.asImageBitmap()
            }
        } else null
    }

    Column(
        Modifier.clip(RoundedCornerShape(6.dp)).clickable { onClick() }.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(38.dp).clip(RoundedCornerShape(9.dp)).background(theme.card),
            contentAlignment = Alignment.Center,
        ) {
            val pic = photo
            when {
                pic != null -> Image(
                    bitmap = pic, contentDescription = null,
                    modifier = Modifier.size(38.dp).clip(RoundedCornerShape(9.dp)),
                    contentScale = ContentScale.Crop,
                    filterQuality = androidx.compose.ui.graphics.FilterQuality.High,
                )
                icons.isEmpty() -> Icon(Icons.Filled.Folder, null, Modifier.size(22.dp), tint = Color(0xFFFFCA28))
                else -> {
                    // 2x2 mini grid of the folder's app icons
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        icons.chunked(2).take(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                row.forEach { ic ->
                                    Image(bitmap = ic, contentDescription = null, modifier = Modifier.size(14.dp), filterQuality = androidx.compose.ui.graphics.FilterQuality.High)
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(5.dp))
        Text(
            folder.name, color = theme.text, fontSize = 11.sp, maxLines = 1,
            overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center,
        )
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
    iconSizeDp: androidx.compose.ui.unit.Dp = 32.dp,
    sizeLevel: Int = 1,
    onSetSize: ((Int) -> Unit)? = null,
) {
    val theme = LocalLauncherTheme.current
    var menuOpen by remember { mutableStateOf(false) }
    var sizeChooser by remember { mutableStateOf(false) }
    // Resizable pinned tiles get a min width so labels stay readable and the
    // FlowRow spaces them; recents/folder tiles keep their content sizing.
    val widthMod = if (onSetSize != null) Modifier.widthIn(min = 60.dp) else Modifier
    val labelSp = if (onSetSize != null) (iconSizeDp.value * 0.16f).coerceIn(11f, 20f) else 11f
    Box {
        Column(
            widthMod
                .clip(RoundedCornerShape(4.dp))
                .pointerInput(app.packageName) {
                    detectTapGestures(
                        onTap = { onClick() },
                        onLongPress = { menuOpen = true },
                    )
                }
                .padding(vertical = 8.dp, horizontal = if (onSetSize != null) 6.dp else 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (app.icon != null) {
                Image(bitmap = app.icon, contentDescription = app.label, modifier = Modifier.size(iconSizeDp), filterQuality = androidx.compose.ui.graphics.FilterQuality.High)
            } else {
                Box(
                    Modifier.size(iconSizeDp).background(theme.card, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(app.label.take(1).uppercase(), color = theme.text, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(5.dp))
            Text(
                app.label, color = theme.text, fontSize = labelSp.sp, maxLines = 1,
                overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 2.dp),
            )
        }
        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { menuOpen = false; sizeChooser = false },
            shape = RoundedCornerShape(8.dp),
            containerColor = theme.menuSurface,
        ) {
            if (sizeChooser && onSetSize != null) {
                for (level in 1..START_ICON_SIZE_COUNT) {
                    val label = when (level) {
                        1 -> "1 — smallest"
                        START_ICON_SIZE_COUNT -> "$level — largest"
                        else -> level.toString()
                    }
                    ContextMenuItem(
                        if (sizeLevel == level) Icons.Outlined.RadioButtonChecked
                        else Icons.Outlined.RadioButtonUnchecked,
                        label,
                    ) {
                        menuOpen = false
                        sizeChooser = false
                        onSetSize(level)
                    }
                }
            } else {
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
                if (onSetSize != null) {
                    ContextMenuItem(Icons.Outlined.PhotoSizeSelectLarge, "Size") {
                        sizeChooser = true
                    }
                }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AllAppsView(
    apps: List<InstalledApp>,
    pins: List<String>,
    dockPins: List<String>,
    folders: List<StartFolder>,
    onBack: () -> Unit,
    onLaunch: (InstalledApp) -> Unit,
    onTogglePin: (String) -> Unit,
    onToggleDockPin: (String) -> Unit,
    onAddToDesktop: (InstalledApp) -> Unit,
    onAddToFolder: (String, Int) -> Unit,
    onUninstall: (String) -> Unit,
    onAddToQuickApps: (String) -> Unit,
    onChangeIcon: (String) -> Unit,
    onResetIcon: (String) -> Unit,
    hasCustomIcon: (String) -> Boolean,
    onBulkToFolder: (Int, List<String>) -> Unit,
    onBulkToDesktop: (List<String>) -> Unit,
    onBulkToQuick: (List<String>) -> Unit,
    onBulkPin: (List<String>) -> Unit,
    onBulkChangeIcon: (List<String>) -> Unit,
    onBulkSize: (Int, List<String>) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    // Multi-select mode: check several apps, then move them all at once
    var selecting by remember { mutableStateOf(false) }
    val selected = remember { androidx.compose.runtime.mutableStateListOf<String>() }
    var sizePick by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(bottom = 10.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                if (selecting) "Selected: ${selected.size}" else "All apps",
                color = theme.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            if (selecting) {
                HeaderChip(
                    if (selected.size == apps.size) "None" else "Select all",
                    Icons.Outlined.Check,
                ) {
                    if (selected.size == apps.size) {
                        selected.clear()
                    } else {
                        selected.clear()
                        selected.addAll(apps.map { it.packageName })
                    }
                }
                Spacer(Modifier.width(8.dp))
            }
            HeaderChip(
                if (selecting) "Done" else "Select",
                if (selecting) Icons.Outlined.Check else Icons.Outlined.Add,
            ) {
                if (selecting) { selected.clear(); sizePick = false }
                selecting = !selecting
            }
            Spacer(Modifier.width(8.dp))
            HeaderChip("Back", Icons.Outlined.ChevronRight, onBack)
        }
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
                    var folderChooser by remember(app.packageName) { mutableStateOf(false) }
                    val isPinned = pins.contains(app.packageName)
                    val isChecked = selected.contains(app.packageName)
                    Box {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .pointerInput(app.packageName, selecting) {
                                    detectTapGestures(
                                        onTap = {
                                            if (selecting) {
                                                if (isChecked) selected.remove(app.packageName)
                                                else selected.add(app.packageName)
                                            } else onLaunch(app)
                                        },
                                        onLongPress = { if (!selecting) menuOpen = true },
                                    )
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (app.icon != null) {
                                Image(bitmap = app.icon, contentDescription = null, modifier = Modifier.size(24.dp), filterQuality = androidx.compose.ui.graphics.FilterQuality.High)
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
                                modifier = Modifier.weight(1f),
                            )
                            if (selecting) {
                                Box(
                                    Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(if (isChecked) theme.accent else theme.card)
                                        .border(1.dp, theme.stroke, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (isChecked) {
                                        Icon(Icons.Outlined.Check, null, Modifier.size(13.dp), tint = theme.accentText)
                                    }
                                }
                            }
                        }
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false; folderChooser = false },
                            shape = RoundedCornerShape(8.dp),
                            containerColor = theme.menuSurface,
                        ) {
                            if (!folderChooser) {
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
                                ContextMenuItem(
                                    Icons.Outlined.AddToHomeScreen,
                                    "Create desktop shortcut",
                                ) {
                                    menuOpen = false
                                    onAddToDesktop(app)
                                }
                                ContextMenuItem(
                                    Icons.Filled.Folder,
                                    "Add to Quick apps",
                                ) {
                                    menuOpen = false
                                    onAddToQuickApps(app.packageName)
                                }
                                ContextMenuItem(
                                    Icons.Outlined.Image,
                                    "Change icon",
                                ) {
                                    menuOpen = false
                                    onChangeIcon(app.packageName)
                                }
                                if (hasCustomIcon(app.packageName)) {
                                    ContextMenuItem(
                                        Icons.Outlined.Refresh,
                                        "Reset icon",
                                    ) {
                                        menuOpen = false
                                        onResetIcon(app.packageName)
                                    }
                                }
                                ContextMenuItem(
                                    Icons.Filled.Folder,
                                    "Add to folder",
                                ) {
                                    folderChooser = true
                                }
                                ContextMenuItem(
                                    Icons.Outlined.Delete,
                                    "Uninstall",
                                ) {
                                    menuOpen = false
                                    onUninstall(app.packageName)
                                }
                            } else {
                                folders.forEachIndexed { index, folder ->
                                    ContextMenuItem(
                                        Icons.Filled.Folder,
                                        folder.name,
                                    ) {
                                        menuOpen = false
                                        folderChooser = false
                                        onAddToFolder(app.packageName, index)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bulk action bar: move / restyle every checked app in one tap
        if (selecting && selected.isNotEmpty()) {
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (sizePick) {
                    // Second tier: pick the size level to apply to all checked
                    for (level in 1..START_ICON_SIZE_COUNT) {
                        ChipButton("Size $level") {
                            onBulkSize(level, selected.toList())
                            sizePick = false; selected.clear(); selecting = false
                        }
                    }
                    ChipButton("Cancel") { sizePick = false }
                } else {
                    ChipButton("→ Desktop") {
                        onBulkToDesktop(selected.toList()); selected.clear(); selecting = false
                    }
                    ChipButton("→ Pin to Start") {
                        onBulkPin(selected.toList()); selected.clear(); selecting = false
                    }
                    ChipButton("→ Quick apps") {
                        onBulkToQuick(selected.toList()); selected.clear(); selecting = false
                    }
                    ChipButton("→ Change icon") {
                        onBulkChangeIcon(selected.toList()); selected.clear(); selecting = false
                    }
                    ChipButton("→ Icon size") { sizePick = true }
                    folders.forEachIndexed { index, folder ->
                        ChipButton("→ ${folder.name}") {
                            onBulkToFolder(index, selected.toList()); selected.clear(); selecting = false
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
                                Image(bitmap = appIcon, contentDescription = null, modifier = Modifier.size(28.dp), filterQuality = androidx.compose.ui.graphics.FilterQuality.High)
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

// ————— Guided help book (the "?" in the Start menu footer) —————

private data class HelpSection(val title: String, val body: String)

private val HELP_SECTIONS = listOf(
    HelpSection(
        "The basics",
        "NeverSoft 11 is a Windows 11-style shell for your phone. The taskbar " +
            "runs along the bottom: Start button, Search (opens Spotlight when " +
            "installed), Task View, the Quick apps folder, your pinned apps, " +
            "then battery and clock. Tap the thin sliver at the far right to " +
            "minimize everything and show the desktop.",
    ),
    HelpSection(
        "Desktop icons",
        "Tap an icon to open it. Click and HOLD an icon and its menu pops up — " +
            "Open, Size (5 levels, up to half the screen), App info, Rename, " +
            "Change icon (any photo — works on the Recycle Bin too), Remove, " +
            "Uninstall. Hold and DRAG to move an icon; it snaps to the grid.",
    ),
    HelpSection(
        "Adding apps",
        "Hold empty desktop space → Add apps: check as many apps as you want " +
            "and they're placed on free grid cells automatically. The same menu " +
            "has Core apps (toggle Recycle Bin, File Explorer, Browser, This PC, " +
            "Settings), New folder, and Config & permissions.",
    ),
    HelpSection(
        "Start menu",
        "Pinned apps live at the top — use the Add apps chip to pin more, or " +
            "long-press a tile to unpin, pin to taskbar, or set its Size. Below " +
            "sit 4 folders, then rows of recent apps and files. Tap a folder to " +
            "open it; from there you can rename it or give it a photo.",
    ),
    HelpSection(
        "Folders & bulk moves",
        "In All apps, long-press any app → Add to folder to file it into one of " +
            "the 4 Start folders (4 apps each). Or tap Select at the top, check " +
            "several apps at once, and use the arrow buttons to send them all to " +
            "the desktop, Start pins, Quick apps, or a folder in one tap.",
    ),
    HelpSection(
        "Extra desktops & Work",
        "Tap the Task View button (two overlapping squares) on the taskbar. At " +
            "the bottom: Desktop 1, New desktop (add up to 2 blank pages), and " +
            "Work — a separate desktop you can lock with a PIN like a secure " +
            "folder. First entry offers to set the PIN; after that it's required " +
            "every time.",
    ),
    HelpSection(
        "Quick apps",
        "The little folder on the taskbar holds up to 6 favorites. Fill it from " +
            "All apps → long-press → Add to Quick apps. Tap an app inside to " +
            "launch; long-press to remove.",
    ),
    HelpSection(
        "Make it yours",
        "Settings → Personalization: 6 themes (Dark, Light, MacBook with real " +
            "traffic-light window buttons, Glass, Metallic, Nev 7 Aero), custom " +
            "wallpaper with fit control, a custom Start button image, and icon " +
            "packs. Long-press any app in All apps → Change icon for a per-app " +
            "photo icon.",
    ),
    HelpSection(
        "Windows",
        "Apps like File Explorer and Settings open in real windows: drag the " +
            "title bar (or long-press anywhere) to move, drag the bottom-right " +
            "button to resize, and use the caption buttons to minimize, " +
            "maximize, or close. The Start menu and calendar resize from their " +
            "corner grips, and remember their size.",
    ),
    HelpSection(
        "Permissions",
        "Hold empty desktop space → Config & permissions to see every " +
            "permission with a green/red dot — each row jumps straight to the " +
            "exact system page: Usage access, All-files access, Contacts & " +
            "media, Notifications, and Default home app.",
    ),
    HelpSection(
        "Tips & tricks",
        "• Long-press empty taskbar space to pin apps to it fast.\n" +
            "• Size 5 icons make great photo widgets when paired with Change icon.\n" +
            "• The clock opens the calendar + notifications panel; battery opens quick settings.\n" +
            "• Search box in Start finds apps, settings, and files as you type.\n" +
            "• If anything crashes, the report screen shows the real error and " +
            "saves it to Downloads/neversoft11_crash_log.txt.",
    ),
)

@Composable
private fun HelpDialog(onDismiss: () -> Unit) {
    val theme = LocalLauncherTheme.current
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .width(340.dp)
                .heightIn(max = 560.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(theme.windowSurface)
                .border(1.dp, theme.stroke, RoundedCornerShape(10.dp))
                .padding(18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Outlined.HelpOutline, null,
                    Modifier.size(20.dp), tint = theme.accent,
                )
                Spacer(Modifier.width(8.dp))
                Text("NeverSoft 11 guide", color = theme.text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                ChipButton("Close", onDismiss)
            }
            Spacer(Modifier.height(10.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
            ) {
                HELP_SECTIONS.forEach { section ->
                    Text(section.title, color = theme.accent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        section.body, color = theme.text, fontSize = 12.sp, lineHeight = 17.sp,
                    )
                    Spacer(Modifier.height(14.dp))
                }
            }
        }
    }
}

