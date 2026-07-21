package com.neversoft.launcher.desktop

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhotoSizeSelectLarge
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Shortcut
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import com.neversoft.launcher.apps.InstalledApp
import com.neversoft.launcher.apps.InstalledAppsRepository
import com.neversoft.launcher.data.AppSettings
import com.neversoft.launcher.files.Trash
import com.neversoft.launcher.ui.AccentButton
import com.neversoft.launcher.ui.SubtleButton
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.window.WindowContentType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlin.math.roundToInt

// A desktop icon. Kinds: builtin shell entries, app shortcuts, folders.
// size is a level 1..5; the icon's on-screen side is that fraction of the
// page width (see ICON_SIZE_FRACTIONS): 1 = 1/16 of a page, 5 = 1/2 of a page.
private data class DeskItem(
    val id: String,
    val kind: String, // "builtin" | "app" | "folder"
    val label: String,
    val pkg: String? = null,
    val path: String? = null,
    val size: Int = 1,
)

// Five icon sizes as a fraction of the page (screen) width, stepping at an
// equal rate from 1/16 (level 1) to 1/2 (level 5).
private val ICON_SIZE_FRACTIONS = listOf(0.0625f, 0.171875f, 0.28125f, 0.390625f, 0.5f)
private const val ICON_SIZE_COUNT = 5

private fun DeskItem.toJson(): JSONObject = JSONObject()
    .put("id", id).put("kind", kind).put("label", label).put("size", size)
    .apply {
        pkg?.let { put("pkg", it) }
        path?.let { put("path", it) }
    }

private fun deskItemFrom(json: JSONObject): DeskItem = DeskItem(
    id = json.getString("id"),
    kind = json.getString("kind"),
    label = json.getString("label"),
    pkg = json.optString("pkg").takeIf { it.isNotEmpty() },
    path = json.optString("path").takeIf { it.isNotEmpty() },
    size = json.optInt("size", 1).coerceIn(1, ICON_SIZE_COUNT),
)

private val BUILTINS = listOf(
    DeskItem("bin", "builtin", "Recycle Bin"),
    DeskItem("files", "builtin", "File Explorer"),
    DeskItem("browser", "builtin", "Browser"),
    DeskItem("thispc", "builtin", "This PC"),
)

// The Windows 11 desktop: draggable grid-snapped icons, long-press
// context menus (New folder / New shortcut / Personalize on empty space;
// Open / App info / Remove / Uninstall on icons).
@Composable
fun Desktop(
    onOpenWindow: (WindowContentType, String, String?) -> Unit,
    modifier: Modifier = Modifier,
    page: Int = 1,
) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    var items by remember(page) { mutableStateOf<List<DeskItem>>(emptyList()) }
    var itemsLoaded by remember(page) { mutableStateOf(false) }
    var desktopMenuAt by remember { mutableStateOf<Offset?>(null) }
    var appPickerOpen by remember { mutableStateOf(false) }
    var pickerApps by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    var renameTarget by remember { mutableStateOf<DeskItem?>(null) }

    fun persistItems(newItems: List<DeskItem>) {
        items = newItems
        scope.launch {
            AppSettings.setDesktopItems(
                context, JSONArray().apply { newItems.forEach { put(it.toJson()) } }.toString(), page,
            )
        }
    }

    // Observe the desktop-items store live, so shortcuts added elsewhere
    // (e.g. "Create desktop shortcut" in the Start menu's All apps) appear
    // without a relaunch.
    LaunchedEffect(page) {
        AppSettings.desktopItemsFlow(context, page).collect { stored ->
            val parsed = if (stored.isEmpty()) {
                null // never seeded
            } else {
                runCatching {
                    val arr = JSONArray(stored)
                    List(arr.length()) { deskItemFrom(arr.getJSONObject(it)) }
                }.getOrNull()
            }
            if (parsed == null) {
                // First run: page 1 seeds the built-in shell icons; the Work
                // desktop (page 2) starts empty.
                if (page == 2) { items = emptyList(); itemsLoaded = true }
                else if (!itemsLoaded) persistItems(BUILTINS)
            } else {
                // Drop shortcuts to apps that were uninstalled
                val pm = context.packageManager
                val cleaned = parsed.filter { item ->
                    item.kind != "app" ||
                        runCatching { pm.getApplicationInfo(item.pkg!!, 0) }.isSuccess
                }
                items = cleaned
                if (cleaned.size != parsed.size) {
                    scope.launch {
                        AppSettings.setDesktopItems(
                            context,
                            JSONArray().apply { cleaned.forEach { put(it.toJson()) } }.toString(),
                            page,
                        )
                    }
                }
                itemsLoaded = true
            }
        }
    }

    // App icons for shortcuts, honoring the active icon pack. Resolved off the
    // main thread so decoding never blocks the UI during a home transition.
    val iconPack by AppSettings.iconPackFlow(context).collectAsState(initial = "")
    val iconOverridesJson by AppSettings.appIconOverridesFlow(context).collectAsState(initial = "{}")
    val iconOverrides = remember(iconOverridesJson) {
        runCatching {
            val o = JSONObject(iconOverridesJson)
            buildMap { o.keys().forEach { k -> put(k, o.optString(k)) } }
        }.getOrDefault(emptyMap())
    }
    var appIcons by remember { mutableStateOf(emptyMap<String, androidx.compose.ui.graphics.ImageBitmap>()) }
    LaunchedEffect(items, iconPack, iconOverrides) {
        appIcons = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            items.filter { it.kind == "app" }.mapNotNull { item ->
                InstalledAppsRepository.loadIcon(context, iconPack, item.pkg!!, iconOverrides[item.pkg])
                    ?.let { item.id to it.asImageBitmap() }
            }.toMap()
        }
    }

    val cellPx = with(density) { 92.dp.toPx() }
    val marginPx = with(density) { 12.dp.toPx() }
    val positions = remember(page) { mutableStateMapOf<String, Offset>() }
    var positionsLoaded by remember(page) { mutableStateOf(false) }

    // Usable desktop area for grid placement (screen minus taskbar/insets)
    val usableHeightPx = remember {
        context.resources.displayMetrics.heightPixels.toFloat() -
            with(density) { 150.dp.toPx() }
    }
    val usableWidthPx = remember {
        context.resources.displayMetrics.widthPixels.toFloat()
    }

    // First grid cell not occupied by any existing icon, filling each column
    // top-to-bottom then moving right — like Windows auto-arrange. Never
    // places off-screen.
    fun firstFreeCell(taken: MutableList<Offset>): Offset {
        val rows = (((usableHeightPx - marginPx) / cellPx).toInt()).coerceAtLeast(1)
        var col = 0
        while (col < 60) {
            for (row in 0 until rows) {
                val p = Offset(marginPx + col * cellPx, marginPx + row * cellPx)
                val free = taken.none { t ->
                    kotlin.math.abs(t.x - p.x) < cellPx * 0.5f &&
                        kotlin.math.abs(t.y - p.y) < cellPx * 0.5f
                }
                if (free) { taken.add(p); return p }
            }
            col++
        }
        return Offset(marginPx, marginPx)
    }

    fun persistPositions() {
        val json = JSONObject()
        positions.forEach { (id, pos) ->
            json.put(id, JSONArray().put(pos.x.toDouble()).put(pos.y.toDouble()))
        }
        scope.launch { AppSettings.setDesktopIconPositions(context, json.toString(), page) }
    }

    // Load positions and SELF-HEAL: any icon whose saved spot is off-screen
    // or stacked on another icon's cell (as older versions could produce) is
    // moved to the first free cell, and the repaired layout is saved back.
    LaunchedEffect(itemsLoaded, items.size, page) {
        if (!itemsLoaded) return@LaunchedEffect
        val stored = runCatching { JSONObject(AppSettings.desktopIconPositionsFlow(context, page).first()) }
            .getOrDefault(JSONObject())
        val taken = mutableListOf<Offset>()
        var repaired = false
        items.forEach { item ->
            val known = positions[item.id]
                ?: stored.optJSONArray(item.id)?.takeIf { it.length() == 2 }
                    ?.let { Offset(it.optDouble(0, 0.0).toFloat(), it.optDouble(1, 0.0).toFloat()) }
            val offScreen = known != null && (
                known.x < 0f || known.y < 0f ||
                    known.x > usableWidthPx - cellPx * 0.6f ||
                    known.y > usableHeightPx - cellPx * 0.25f
                )
            val stacked = known != null && taken.any { t ->
                kotlin.math.abs(t.x - known.x) < cellPx * 0.5f &&
                    kotlin.math.abs(t.y - known.y) < cellPx * 0.5f
            }
            val pos = if (known == null || offScreen || stacked) {
                repaired = true
                firstFreeCell(taken)
            } else {
                taken.add(known)
                known
            }
            if (positions[item.id] != pos) positions[item.id] = pos
        }
        positionsLoaded = true
        if (repaired) persistPositions()
    }

    fun activate(item: DeskItem) {
        when (item.kind) {
            "builtin" -> when (item.id) {
                "bin" -> onOpenWindow(WindowContentType.FILE_EXPLORER, "Recycle Bin", Trash.dir().absolutePath)
                "files" -> onOpenWindow(WindowContentType.FILE_EXPLORER, "File Explorer", null)
                "thispc" -> onOpenWindow(WindowContentType.FILE_EXPLORER, "This PC", "/storage")
                "browser" -> runCatching {
                    context.startActivity(
                        Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                }
            }
            "app" -> item.pkg?.let { InstalledAppsRepository.launch(context, it) }
            "folder" -> item.path?.let { onOpenWindow(WindowContentType.FILE_EXPLORER, item.label, it) }
        }
    }

    fun newFolder() {
        val desktopDir = File(Environment.getExternalStorageDirectory(), "Desktop").apply { mkdirs() }
        val dir = Trash.uniqueName(desktopDir, "New folder")
        if (dir.mkdirs()) {
            val item = DeskItem(
                id = "folder_${System.currentTimeMillis()}",
                kind = "folder",
                label = dir.name,
                path = dir.absolutePath,
            )
            persistItems(items + item)
            // Prompt for a name right away, like Windows' inline rename
            renameTarget = item
        }
    }

    fun applyRename(item: DeskItem, rawName: String) {
        val newName = rawName.trim()
        if (newName.isEmpty() || newName == item.label) return
        val updated = if (item.kind == "folder" && item.path != null) {
            val dir = File(item.path)
            val dest = File(dir.parentFile, newName)
            if (dest.exists() || !dir.renameTo(dest)) return
            item.copy(label = newName, path = dest.absolutePath)
        } else {
            item.copy(label = newName)
        }
        persistItems(items.map { if (it.id == item.id) updated else it })
    }

    fun setSize(item: DeskItem, level: Int) {
        val clamped = level.coerceIn(1, ICON_SIZE_COUNT)
        persistItems(items.map { if (it.id == item.id) it.copy(size = clamped) else it })
    }

    // The "page" the size fractions are relative to is the screen width.
    val pageWidthDp = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp.dp

    Box(
        modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = { pos -> desktopMenuAt = pos },
                onTap = { desktopMenuAt = null },
            )
        },
    ) {
        if (itemsLoaded && positionsLoaded) {
            items.forEach { item ->
                key(item.id) {
                    val pos = positions[item.id] ?: Offset.Zero
                    DesktopIconWithMenu(
                        item = item,
                        appIcon = appIcons[item.id],
                        iconSizeDp = pageWidthDp * ICON_SIZE_FRACTIONS[item.size - 1],
                        onSetSize = { level -> setSize(item, level) },
                        modifier = Modifier
                            .offset { IntOffset(pos.x.roundToInt(), pos.y.roundToInt()) },
                        onOpen = { activate(item) },
                        onRemove = { persistItems(items - item) },
                        onRename = { renameTarget = item },
                        onDragDelta = { dragAmount ->
                            val current = positions[item.id] ?: Offset.Zero
                            positions[item.id] = Offset(
                                (current.x + dragAmount.x).coerceAtLeast(0f),
                                (current.y + dragAmount.y).coerceAtLeast(0f),
                            )
                        },
                        onDragEnd = {
                            val current = positions[item.id] ?: Offset.Zero
                            positions[item.id] = Offset(
                                marginPx + ((current.x - marginPx) / cellPx).roundToInt()
                                    .coerceAtLeast(0) * cellPx,
                                marginPx + ((current.y - marginPx) / cellPx).roundToInt()
                                    .coerceAtLeast(0) * cellPx,
                            )
                            persistPositions()
                        },
                    )
                }
            }
        }

        // Empty-desktop context menu at the long-press position
        desktopMenuAt?.let { at ->
            Box(Modifier.offset { IntOffset(at.x.roundToInt(), at.y.roundToInt()) }) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { desktopMenuAt = null },
                    shape = RoundedCornerShape(8.dp),
                    containerColor = theme.menuSurface,
                ) {
                    DesktopMenuItem(Icons.Outlined.CreateNewFolder, "New folder") {
                        desktopMenuAt = null
                        newFolder()
                    }
                    DesktopMenuItem(Icons.Outlined.Shortcut, "Add apps") {
                        desktopMenuAt = null
                        appPickerOpen = true
                        scope.launch { pickerApps = InstalledAppsRepository.loadApps(context) }
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(theme.divider))
                    DesktopMenuItem(Icons.Outlined.Palette, "Personalize") {
                        desktopMenuAt = null
                        onOpenWindow(WindowContentType.SETTINGS, "Settings", null)
                    }
                }
            }
        }
    }

    // Rename dialog for desktop folders and shortcut labels
    renameTarget?.let { target ->
        var newName by remember(target.id) { mutableStateOf(target.label) }
        Dialog(onDismissRequest = { renameTarget = null }) {
            Column(
                Modifier
                    .width(300.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.windowSurface)
                    .border(1.dp, theme.stroke, RoundedCornerShape(8.dp))
                    .padding(20.dp),
            ) {
                Text(
                    if (target.kind == "folder") "Name this folder" else "Rename shortcut",
                    color = theme.text, fontSize = 16.sp,
                )
                Spacer(Modifier.height(14.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(theme.inputField)
                        .border(1.dp, theme.stroke, RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = theme.text, fontSize = 13.sp),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(theme.accent),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                    AccentButton("OK", modifier = Modifier.weight(1f), onClick = {
                        applyRename(target, newName)
                        renameTarget = null
                    })
                    SubtleButton("Cancel", modifier = Modifier.weight(1f), onClick = { renameTarget = null })
                }
            }
        }
    }

    // Multi-select app picker: add one or more apps to the desktop at once
    if (appPickerOpen) {
        com.neversoft.launcher.apps.AppPickerDialog(
            title = if (page == 2) "Add apps to Work desktop" else "Add apps to desktop",
            apps = pickerApps,
            onConfirm = { pkgs ->
                val byPkg = pickerApps.associateBy { it.packageName }
                val additions = pkgs.mapNotNull { pkg ->
                    val id = "app_$pkg"
                    if (items.none { it.id == id }) {
                        byPkg[pkg]?.let { DeskItem(id = id, kind = "app", label = it.label, pkg = pkg) }
                    } else null
                }
                if (additions.isNotEmpty()) {
                    // Place each new icon on a free grid cell right away so
                    // they always land visible (never stacked or off-screen)
                    val taken = positions.values.toMutableList()
                    additions.forEach { item -> positions[item.id] = firstFreeCell(taken) }
                    persistItems(items + additions)
                    persistPositions()
                }
                android.widget.Toast.makeText(
                    context,
                    when {
                        additions.isNotEmpty() -> "Added ${additions.size} app${if (additions.size == 1) "" else "s"}"
                        pkgs.isNotEmpty() -> "Already on this desktop"
                        else -> "Nothing selected"
                    },
                    android.widget.Toast.LENGTH_SHORT,
                ).show()
            },
            onDismiss = { appPickerOpen = false },
        )
    }
}

@Composable
private fun DesktopIconWithMenu(
    item: DeskItem,
    appIcon: ImageBitmap?,
    iconSizeDp: androidx.compose.ui.unit.Dp,
    onSetSize: (Int) -> Unit,
    modifier: Modifier,
    onOpen: () -> Unit,
    onRemove: () -> Unit,
    onRename: () -> Unit,
    onDragDelta: (Offset) -> Unit,
    onDragEnd: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    var menuOpen by remember { mutableStateOf(false) }
    var sizeChooser by remember { mutableStateOf(false) }
    // Keep small icons' labels readable; large icons let the tile grow.
    val tileWidth = if (iconSizeDp > 72.dp) iconSizeDp else 72.dp
    // Label scales gently with the icon so big icons don't get a tiny caption.
    val labelSp = (iconSizeDp.value * 0.16f).coerceIn(11f, 22f)

    Box(modifier) {
        Column(
            Modifier
                .width(tileWidth)
                // Quick tap opens the icon.
                .pointerInput(item.id) {
                    detectTapGestures(onTap = { onOpen() })
                }
                // Long-press (click and hold) auto-pops the menu immediately.
                // If you then drag, the menu dismisses and the icon follows,
                // so both "hold for options" and "drag to move" work.
                .pointerInput(item.id) {
                    var moved = false
                    detectDragGesturesAfterLongPress(
                        onDragStart = { moved = false; menuOpen = true },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            if (!moved) { moved = true; menuOpen = false }
                            onDragDelta(dragAmount)
                        },
                        onDragEnd = { if (moved) onDragEnd() },
                        onDragCancel = { },
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // The icon fits to the chosen size (fills the sized box).
            when {
                item.kind == "app" && appIcon != null ->
                    Image(bitmap = appIcon, contentDescription = item.label, modifier = Modifier.size(iconSizeDp), filterQuality = androidx.compose.ui.graphics.FilterQuality.High)
                item.kind == "folder" ->
                    Icon(Icons.Filled.Folder, item.label, Modifier.size(iconSizeDp), tint = Color(0xFFFFCA28))
                else -> {
                    val (icon, tint) = builtinIcon(item.id)
                    Icon(icon, item.label, Modifier.size(iconSizeDp), tint = tint)
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(
                item.label,
                style = TextStyle(
                    color = Color.White,
                    fontSize = labelSp.sp,
                    shadow = Shadow(Color(0xB3000000), blurRadius = 4f),
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { menuOpen = false; sizeChooser = false },
            shape = RoundedCornerShape(8.dp),
            containerColor = theme.menuSurface,
        ) {
            if (sizeChooser) {
                for (level in 1..ICON_SIZE_COUNT) {
                    val label = when (level) {
                        1 -> "1 — smallest"
                        ICON_SIZE_COUNT -> "$level — largest"
                        else -> level.toString()
                    }
                    DesktopMenuItem(
                        if (item.size == level) Icons.Outlined.RadioButtonChecked
                        else Icons.Outlined.RadioButtonUnchecked,
                        label,
                    ) {
                        menuOpen = false
                        sizeChooser = false
                        onSetSize(level)
                    }
                }
            } else {
                DesktopMenuItem(Icons.Outlined.OpenInNew, "Open") {
                    menuOpen = false
                    onOpen()
                }
                DesktopMenuItem(Icons.Outlined.PhotoSizeSelectLarge, "Size") {
                    sizeChooser = true
                }
                if (item.kind == "app") {
                    DesktopMenuItem(Icons.Outlined.Info, "App info") {
                        menuOpen = false
                        runCatching {
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:${item.pkg}"),
                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            )
                        }
                    }
                }
                if (item.kind == "folder" || item.kind == "app") {
                    DesktopMenuItem(Icons.Outlined.DriveFileRenameOutline, "Rename") {
                        menuOpen = false
                        onRename()
                    }
                }
                DesktopMenuItem(Icons.Outlined.RemoveCircleOutline, "Remove from desktop") {
                    menuOpen = false
                    onRemove()
                }
                if (item.kind == "app") {
                    DesktopMenuItem(Icons.Outlined.Delete, "Uninstall") {
                        menuOpen = false
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_DELETE, Uri.parse("package:${item.pkg}"))
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    val theme = LocalLauncherTheme.current
    Row(
        Modifier
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp)
            .width(160.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, Modifier.size(15.dp), tint = theme.text)
        Spacer(Modifier.width(10.dp))
        Text(label, color = theme.text, fontSize = 13.sp)
    }
}

private fun builtinIcon(id: String): Pair<ImageVector, Color> = when (id) {
    "bin" -> Icons.Outlined.Delete to Color.White
    "files" -> Icons.Filled.Folder to Color(0xFFFFCA28)
    "browser" -> Icons.Outlined.Language to Color(0xFF6EC6F5)
    "thispc" -> Icons.Outlined.Computer to Color.White
    else -> Icons.Filled.Folder to Color.White
}
