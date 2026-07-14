package com.neversoft.launcher.desktop

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
private data class DeskItem(
    val id: String,
    val kind: String, // "builtin" | "app" | "folder"
    val label: String,
    val pkg: String? = null,
    val path: String? = null,
)

private fun DeskItem.toJson(): JSONObject = JSONObject()
    .put("id", id).put("kind", kind).put("label", label)
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
) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    var items by remember { mutableStateOf<List<DeskItem>>(emptyList()) }
    var itemsLoaded by remember { mutableStateOf(false) }
    var desktopMenuAt by remember { mutableStateOf<Offset?>(null) }
    var appPickerOpen by remember { mutableStateOf(false) }
    var pickerApps by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    var renameTarget by remember { mutableStateOf<DeskItem?>(null) }

    fun persistItems(newItems: List<DeskItem>) {
        items = newItems
        scope.launch {
            AppSettings.setDesktopItems(
                context, JSONArray().apply { newItems.forEach { put(it.toJson()) } }.toString(),
            )
        }
    }

    LaunchedEffect(Unit) {
        val stored = AppSettings.desktopItemsFlow(context).first()
        items = if (stored.isEmpty()) {
            // First run: seed with the built-in shell icons
            persistItems(BUILTINS)
            BUILTINS
        } else {
            runCatching {
                val arr = JSONArray(stored)
                List(arr.length()) { deskItemFrom(arr.getJSONObject(it)) }
            }.getOrDefault(BUILTINS)
        }
        // Drop shortcuts to apps that were uninstalled
        val pm = context.packageManager
        val cleaned = items.filter { item ->
            item.kind != "app" || runCatching { pm.getApplicationInfo(item.pkg!!, 0) }.isSuccess
        }
        if (cleaned.size != items.size) persistItems(cleaned)
        itemsLoaded = true
    }

    // App icons for shortcuts, honoring the active icon pack. Resolved off the
    // main thread so decoding never blocks the UI during a home transition.
    val iconPack by AppSettings.iconPackFlow(context).collectAsState(initial = "")
    var appIcons by remember { mutableStateOf(emptyMap<String, androidx.compose.ui.graphics.ImageBitmap>()) }
    LaunchedEffect(items, iconPack) {
        appIcons = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            items.filter { it.kind == "app" }.mapNotNull { item ->
                InstalledAppsRepository.loadIcon(context, iconPack, item.pkg!!)
                    ?.let { item.id to it.asImageBitmap() }
            }.toMap()
        }
    }

    val cellPx = with(density) { 92.dp.toPx() }
    val marginPx = with(density) { 12.dp.toPx() }
    val positions = remember { mutableStateMapOf<String, Offset>() }
    var positionsLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(itemsLoaded, items.size) {
        if (!itemsLoaded) return@LaunchedEffect
        val stored = runCatching { JSONObject(AppSettings.desktopIconPositionsFlow(context).first()) }
            .getOrDefault(JSONObject())
        items.forEachIndexed { index, item ->
            if (positions.containsKey(item.id)) return@forEachIndexed
            val arr = stored.optJSONArray(item.id)
            positions[item.id] = if (arr != null && arr.length() == 2) {
                Offset(arr.optDouble(0, 0.0).toFloat(), arr.optDouble(1, 0.0).toFloat())
            } else {
                Offset(marginPx, marginPx + index * cellPx)
            }
        }
        positionsLoaded = true
    }

    fun persistPositions() {
        val json = JSONObject()
        positions.forEach { (id, pos) ->
            json.put(id, JSONArray().put(pos.x.toDouble()).put(pos.y.toDouble()))
        }
        scope.launch { AppSettings.setDesktopIconPositions(context, json.toString()) }
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
                        modifier = Modifier
                            .offset { IntOffset(pos.x.roundToInt(), pos.y.roundToInt()) }
                            .pointerInput(item.id) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
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
                            },
                        onOpen = { activate(item) },
                        onRemove = { persistItems(items - item) },
                        onRename = { renameTarget = item },
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
                    DesktopMenuItem(Icons.Outlined.Shortcut, "New shortcut") {
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

    // App picker for "New shortcut"
    if (appPickerOpen) {
        Dialog(onDismissRequest = { appPickerOpen = false }) {
            Column(
                Modifier
                    .width(320.dp)
                    .height(440.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.windowSurface)
                    .border(1.dp, theme.stroke, RoundedCornerShape(8.dp))
                    .padding(16.dp),
            ) {
                Text("Choose an app", color = theme.text, fontSize = 16.sp)
                Spacer(Modifier.height(10.dp))
                if (pickerApps.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Loading…", color = theme.textSecondary, fontSize = 12.sp)
                    }
                } else {
                    LazyColumn {
                        items(pickerApps, key = { it.packageName }) { app ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable {
                                        appPickerOpen = false
                                        val id = "app_${app.packageName}"
                                        if (items.none { it.id == id }) {
                                            persistItems(
                                                items + DeskItem(
                                                    id = id, kind = "app",
                                                    label = app.label, pkg = app.packageName,
                                                ),
                                            )
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (app.icon != null) {
                                    Image(bitmap = app.icon, contentDescription = null, modifier = Modifier.size(26.dp), filterQuality = androidx.compose.ui.graphics.FilterQuality.High)
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
    }
}

@Composable
private fun DesktopIconWithMenu(
    item: DeskItem,
    appIcon: ImageBitmap?,
    modifier: Modifier,
    onOpen: () -> Unit,
    onRemove: () -> Unit,
    onRename: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    var menuOpen by remember { mutableStateOf(false) }

    Box(modifier) {
        Column(
            Modifier
                .width(80.dp)
                .pointerInput(item.id) {
                    detectTapGestures(
                        onTap = { onOpen() },
                        onLongPress = { menuOpen = true },
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when {
                item.kind == "app" && appIcon != null ->
                    Image(bitmap = appIcon, contentDescription = item.label, modifier = Modifier.size(38.dp), filterQuality = androidx.compose.ui.graphics.FilterQuality.High)
                item.kind == "folder" ->
                    Icon(Icons.Filled.Folder, item.label, Modifier.size(38.dp), tint = Color(0xFFFFCA28))
                else -> {
                    val (icon, tint) = builtinIcon(item.id)
                    Icon(icon, item.label, Modifier.size(38.dp), tint = tint)
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(
                item.label,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 11.sp,
                    shadow = Shadow(Color(0xB3000000), blurRadius = 4f),
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { menuOpen = false },
            shape = RoundedCornerShape(8.dp),
            containerColor = theme.menuSurface,
        ) {
            DesktopMenuItem(Icons.Outlined.OpenInNew, "Open") {
                menuOpen = false
                onOpen()
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
