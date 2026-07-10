package com.neversoft.launcher.startmenu

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neversoft.launcher.apps.AppCategory
import com.neversoft.launcher.apps.InstalledApp
import com.neversoft.launcher.apps.InstalledAppsRepository
import com.neversoft.launcher.data.AppSettings
import com.neversoft.launcher.search.AppSearchProvider
import com.neversoft.launcher.search.FileSearchProvider
import com.neversoft.launcher.search.LauncherSearchEngine
import com.neversoft.launcher.search.ResultType
import com.neversoft.launcher.search.SearchResult
import com.neversoft.launcher.search.SettingsSearchProvider
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.window.WindowContentType
import kotlinx.coroutines.launch
import org.json.JSONArray

private const val PIN_COLUMNS = 4
private const val PIN_ROWS = 3

/** A pinned tile: either a launcher builtin window or an installed app. */
private sealed class PinnedItem(val key: String) {
    class Builtin(val id: String, val label: String, val icon: ImageVector, val window: WindowContentType, val title: String) :
        PinnedItem("builtin:$id")

    class App(val app: InstalledApp) : PinnedItem("app:${app.packageName}")
}

private fun builtins() = listOf(
    PinnedItem.Builtin("files", "File Explorer", Icons.Default.Folder, WindowContentType.FILE_EXPLORER, "File Explorer"),
    PinnedItem.Builtin("terminal", "Terminal", Icons.Default.Terminal, WindowContentType.TERMINAL, "Terminal"),
    PinnedItem.Builtin("browser", "Browser", Icons.Default.Language, WindowContentType.BROWSER, "Browser"),
    PinnedItem.Builtin("controlpanel", "Control Panel", Icons.Default.DisplaySettings, WindowContentType.CONTROL_PANEL, "Control Panel"),
)

/** The page currently shown in the start menu body. */
private sealed class MenuPage {
    data object Main : MenuPage()
    data object AllApps : MenuPage()
    data class Category(val category: AppCategory) : MenuPage()
}

@Composable
fun StartMenu(
    onDismiss: () -> Unit,
    onOpenWindow: (WindowContentType, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var page by remember { mutableStateOf<MenuPage>(MenuPage.Main) }

    var appsLoaded by remember { mutableStateOf(false) }
    var apps by remember { mutableStateOf(emptyList<InstalledApp>()) }
    LaunchedEffect(Unit) {
        apps = InstalledAppsRepository.loadApps(context)
        appsLoaded = true
    }

    // ── Pinned (persisted as JSON list of "builtin:x" / "app:pkg" keys) ──
    val storedPins by AppSettings.startPinsFlow(context).collectAsState(initial = null)
    val pins: List<PinnedItem> = remember(storedPins, apps) {
        val keys = runCatching {
            val arr = JSONArray(storedPins ?: "[]")
            List(arr.length()) { arr.getString(it) }
        }.getOrDefault(emptyList())
        resolvePins(keys, apps)
    }

    fun persistPins(keys: List<String>) {
        scope.launch { AppSettings.setStartPins(context, JSONArray(keys).toString()) }
    }

    fun pinKeys(): List<String> = pins.map { it.key }

    fun togglePin(key: String) {
        val keys = pinKeys()
        persistPins(if (key in keys) keys - key else keys + key)
    }

    // ── Recents ──
    val recentPackages by AppSettings.recentAppsFlow(context).collectAsState(initial = emptyList())
    val recentApps = remember(recentPackages, apps) {
        recentPackages.mapNotNull { pkg -> apps.firstOrNull { it.packageName == pkg } }.take(PIN_COLUMNS)
    }

    // ── Search ──
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

    fun launchApp(app: InstalledApp) {
        InstalledAppsRepository.launch(context, app.packageName)
        onDismiss()
    }

    fun openBuiltin(item: PinnedItem.Builtin) {
        onOpenWindow(item.window, item.title)
        onDismiss()
    }

    fun activate(result: SearchResult) {
        when (result.type) {
            ResultType.APP -> result.packageName?.let { InstalledAppsRepository.launch(context, it) }
            ResultType.SETTING -> runCatching { context.startActivity(Intent(result.id)) }
            ResultType.FILE -> onOpenWindow(WindowContentType.FILE_EXPLORER, "File Explorer")
            else -> Unit
        }
        onDismiss()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .fillMaxHeight(0.84f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(Modifier.fillMaxSize().background(theme.windowBrush()).padding(14.dp)) {
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                placeholder = { Text("Search apps, settings, documents…", fontSize = 13.sp, color = theme.onSurface.copy(0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = theme.onSurface, unfocusedTextColor = theme.onSurface,
                    focusedBorderColor = theme.accentColor, unfocusedBorderColor = theme.onSurface.copy(0.3f),
                    cursorColor = theme.accentColor,
                ),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = theme.onSurface.copy(0.6f)) },
                singleLine = true,
            )
            Spacer(Modifier.height(10.dp))

            Box(Modifier.weight(1f)) {
                when {
                    !appsLoaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = theme.accentColor)
                    }

                    query.isNotBlank() -> SearchResults(
                        query = query,
                        results = results,
                        apps = apps,
                        onActivate = ::activate,
                    )

                    page is MenuPage.AllApps || page is MenuPage.Category -> {
                        val (title, pageApps) = when (val p = page) {
                            is MenuPage.Category -> p.category.title to apps.filter { AppCategory.of(it) == p.category }
                            else -> "All apps" to apps
                        }
                        AppDrawerPage(
                            title = title,
                            apps = pageApps,
                            pinnedKeys = pinKeys().toSet(),
                            onBack = { page = MenuPage.Main },
                            onLaunch = ::launchApp,
                            onTogglePin = { togglePin("app:${it.packageName}") },
                        )
                    }

                    else -> MainPage(
                        pins = pins,
                        recentApps = recentApps,
                        apps = apps,
                        onLaunchApp = ::launchApp,
                        onOpenBuiltin = ::openBuiltin,
                        onUnpin = { togglePin(it.key) },
                        onOpenCategory = { page = MenuPage.Category(it) },
                        onOpenAllApps = { page = MenuPage.AllApps },
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            HorizontalDivider(color = theme.onSurface.copy(0.1f))
            Spacer(Modifier.height(4.dp))
            // Plain, compact footer: Control Panel + phone Settings only.
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        onOpenWindow(WindowContentType.CONTROL_PANEL, "Control Panel")
                        onDismiss()
                    },
                    modifier = Modifier.size(34.dp),
                ) {
                    Icon(Icons.Default.DisplaySettings, "Control Panel", Modifier.size(18.dp), tint = theme.onSurface.copy(0.8f))
                }
                Spacer(Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        runCatching { context.startActivity(Intent(Settings.ACTION_SETTINGS)) }
                        onDismiss()
                    },
                    modifier = Modifier.size(34.dp),
                ) {
                    Icon(Icons.Default.Settings, "Settings", Modifier.size(18.dp), tint = theme.onSurface.copy(0.8f))
                }
            }
        }
    }
}

/** Resolve stored pin keys against loaded apps, seeding Windows-style defaults when unset. */
private fun resolvePins(keys: List<String>, apps: List<InstalledApp>): List<PinnedItem> {
    val builtinItems = builtins().associateBy { it.key }
    val effectiveKeys = keys.ifEmpty {
        val defaults = builtinItems.keys.toMutableList()
        apps.take(PIN_COLUMNS * PIN_ROWS - defaults.size).forEach { defaults += "app:${it.packageName}" }
        defaults
    }
    return effectiveKeys.mapNotNull { key ->
        when {
            key.startsWith("builtin:") -> builtinItems[key]
            key.startsWith("app:") -> apps.firstOrNull { it.packageName == key.removePrefix("app:") }
                ?.let { PinnedItem.App(it) }
            else -> null
        }
    }
}

// ─── Main page: Pinned → Recent → category folders ───

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainPage(
    pins: List<PinnedItem>,
    recentApps: List<InstalledApp>,
    apps: List<InstalledApp>,
    onLaunchApp: (InstalledApp) -> Unit,
    onOpenBuiltin: (PinnedItem.Builtin) -> Unit,
    onUnpin: (PinnedItem) -> Unit,
    onOpenCategory: (AppCategory) -> Unit,
    onOpenAllApps: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        SectionHeader("Pinned")
        pins.take(PIN_COLUMNS * PIN_ROWS).chunked(PIN_COLUMNS).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { pin ->
                    PinnedTile(
                        pin = pin,
                        onLaunchApp = onLaunchApp,
                        onOpenBuiltin = onOpenBuiltin,
                        onUnpin = onUnpin,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(PIN_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }

        if (recentApps.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            SectionHeader("Recent")
            Row(Modifier.fillMaxWidth()) {
                recentApps.forEach { app ->
                    AppTile(app = app, onClick = { onLaunchApp(app) }, modifier = Modifier.weight(1f))
                }
                repeat(PIN_COLUMNS - recentApps.size) { Spacer(Modifier.weight(1f)) }
            }
        }

        Spacer(Modifier.height(8.dp))
        SectionHeader("All apps")
        val cards: List<Pair<String, List<InstalledApp>>> =
            AppCategory.entries.map { cat -> cat.title to apps.filter { AppCategory.of(it) == cat } } +
                listOf("All" to apps)
        cards.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (title, categoryApps) ->
                    FolderCard(
                        title = title,
                        apps = categoryApps,
                        onClick = {
                            if (title == "All") onOpenAllApps()
                            else onOpenCategory(AppCategory.entries.first { it.title == title })
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(2.dp))
        Text(
            "Long-press an app to pin or unpin it",
            fontSize = 9.sp,
            color = theme.onSurface.copy(0.35f),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = LocalLauncherTheme.current.onSurface.copy(0.85f),
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PinnedTile(
    pin: PinnedItem,
    onLaunchApp: (InstalledApp) -> Unit,
    onOpenBuiltin: (PinnedItem.Builtin) -> Unit,
    onUnpin: (PinnedItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current
    var menuOpen by remember { mutableStateOf(false) }
    Box(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = {
                        when (pin) {
                            is PinnedItem.App -> onLaunchApp(pin.app)
                            is PinnedItem.Builtin -> onOpenBuiltin(pin)
                        }
                    },
                    onLongClick = { menuOpen = true },
                )
                .padding(vertical = 7.dp),
        ) {
            when (pin) {
                is PinnedItem.App -> AppIcon(pin.app, size = 38.dp)
                is PinnedItem.Builtin -> Box(
                    Modifier.size(38.dp).background(theme.accentColor.copy(0.18f), RoundedCornerShape(9.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(pin.icon, null, Modifier.size(21.dp), tint = theme.onSurface)
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(
                when (pin) {
                    is PinnedItem.App -> pin.app.label
                    is PinnedItem.Builtin -> pin.label
                },
                color = theme.onSurface, fontSize = 10.sp, maxLines = 1,
                overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 2.dp),
            )
        }
        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            DropdownMenuItem(
                text = { Text("Unpin from Start", fontSize = 12.sp) },
                onClick = { menuOpen = false; onUnpin(pin) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppTile(
    app: InstalledApp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
) {
    val theme = LocalLauncherTheme.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 7.dp),
    ) {
        AppIcon(app, size = 38.dp)
        Spacer(Modifier.height(3.dp))
        Text(
            app.label, color = theme.onSurface, fontSize = 10.sp, maxLines = 1,
            overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
    }
}

@Composable
private fun AppIcon(app: InstalledApp, size: androidx.compose.ui.unit.Dp) {
    val theme = LocalLauncherTheme.current
    if (app.icon != null) {
        Image(bitmap = app.icon, contentDescription = app.label, modifier = Modifier.size(size))
    } else {
        Box(
            Modifier.size(size).background(theme.accentColor.copy(0.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(app.label.take(1).uppercase(), color = theme.onSurface, fontSize = 15.sp)
        }
    }
}

@Composable
private fun FolderCard(
    title: String,
    apps: List<InstalledApp>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current
    Column(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(theme.onSurface.copy(0.06f))
            .clickable { onClick() }
            .padding(10.dp),
    ) {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = theme.onSurface, maxLines = 1)
        Text("${apps.size} app${if (apps.size == 1) "" else "s"}", fontSize = 9.sp, color = theme.onSurface.copy(0.5f))
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (title == "All" || apps.isEmpty()) {
                Box(
                    Modifier.size(18.dp).background(theme.accentColor.copy(0.18f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Apps, null, Modifier.size(11.dp), tint = theme.onSurface.copy(0.8f))
                }
            }
            if (title != "All") {
                apps.take(3).forEach { app ->
                    AppIcon(app, size = 18.dp)
                    Spacer(Modifier.width(4.dp))
                }
            }
        }
    }
}

// ─── Drawer pages (category folder / all apps) ───

@Composable
private fun AppDrawerPage(
    title: String,
    apps: List<InstalledApp>,
    pinnedKeys: Set<String>,
    onBack: () -> Unit,
    onLaunch: (InstalledApp) -> Unit,
    onTogglePin: (InstalledApp) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    var menuFor by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.size(30.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", Modifier.size(16.dp), tint = theme.onSurface)
            }
            Spacer(Modifier.width(4.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = theme.onSurface,
            )
            Spacer(Modifier.weight(1f))
            Text("${apps.size}", fontSize = 10.sp, color = theme.onSurface.copy(0.4f))
        }
        if (apps.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nothing here yet", color = theme.onSurface.copy(0.4f), fontSize = 12.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(PIN_COLUMNS),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(apps, key = { it.packageName }) { app ->
                    Box {
                        AppTile(
                            app = app,
                            onClick = { onLaunch(app) },
                            onLongClick = { menuFor = app.packageName },
                        )
                        DropdownMenu(
                            expanded = menuFor == app.packageName,
                            onDismissRequest = { menuFor = null },
                        ) {
                            val pinned = "app:${app.packageName}" in pinnedKeys
                            DropdownMenuItem(
                                text = { Text(if (pinned) "Unpin from Start" else "Pin to Start", fontSize = 12.sp) },
                                onClick = { menuFor = null; onTogglePin(app) },
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Search results ───

@Composable
private fun SearchResults(
    query: String,
    results: List<SearchResult>,
    apps: List<InstalledApp>,
    onActivate: (SearchResult) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Column(Modifier.fillMaxSize()) {
        Text("Results", style = MaterialTheme.typography.labelSmall, color = theme.onSurface.copy(0.6f))
        Spacer(Modifier.height(6.dp))
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
                    ) { onActivate(result) }
                }
            }
        }
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
