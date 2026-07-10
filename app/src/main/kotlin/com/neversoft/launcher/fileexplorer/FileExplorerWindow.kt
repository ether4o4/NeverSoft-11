package com.neversoft.launcher.fileexplorer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.neversoft.launcher.theme.LocalLauncherTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class SortKey { NAME, SIZE, DATE, TYPE }
private enum class ViewMode { LIST, GRID }

/**
 * File explorer window: Ghost Key's explorer engine (one-level on-demand
 * listing, sortable columns, list/grid views, selection + context menu,
 * clipboard transfer, soft delete to the recycle bin, status bar) dressed in
 * the Windows 11 layout — quick-access sidebar, command bar, breadcrumb path.
 */
@Composable
fun FileExplorerWindow(initialPath: String? = null) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val home = remember { Environment.getExternalStorageDirectory() }

    var currentPath by remember { mutableStateOf(initialPath ?: home.absolutePath) }
    val currentDir = remember(currentPath) { File(currentPath) }
    var refreshTick by remember { mutableIntStateOf(0) }
    var sortKey by remember { mutableStateOf(SortKey.NAME) }
    var sortAsc by remember { mutableStateOf(true) }
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    var menuForPath by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var clipboardTick by remember { mutableIntStateOf(0) }

    var renameTarget by remember { mutableStateOf<File?>(null) }
    var showNewFolder by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    // null means the directory could not be read (missing permission or unreadable path)
    val listing: List<File>? = remember(currentPath, refreshTick) {
        File(currentPath).listFiles()?.toList()
    }
    val sorted = remember(listing, sortKey, sortAsc) { sortEntries(listing, sortKey, sortAsc) }
    val selectedFiles = remember(sorted, selected) { sorted.filter { it.absolutePath in selected } }

    fun navigate(path: String) {
        currentPath = path
        selected = emptySet()
        menuForPath = null
        statusMessage = null
    }

    fun refresh() { refreshTick++; selected = emptySet() }

    fun runOp(block: () -> String?) {
        scope.launch {
            val message = withContext(Dispatchers.IO) { block() }
            statusMessage = message
            refresh()
        }
    }

    fun open(file: File) {
        if (file.isDirectory) navigate(file.absolutePath) else FileOps.openFile(context, file)
    }

    fun paste() {
        val files = FileClipboard.paths.map(::File).filter { it.exists() }
        val cut = FileClipboard.isCut
        val dest = currentDir
        runOp {
            var ok = 0
            files.forEach { src ->
                val done = if (cut) FileOps.moveInto(src, dest) else FileOps.copyInto(src, dest)
                if (done) ok++
            }
            if (cut) { FileClipboard.clear(); clipboardTick++ }
            if (ok == files.size) "$ok item${if (ok == 1) "" else "s"} pasted"
            else "Pasted $ok of ${files.size} items"
        }
    }

    fun deleteSelected() {
        val files = selectedFiles
        val permanent = FileOps.isInRecycleBin(currentDir)
        runOp {
            var ok = 0
            files.forEach { f ->
                val done = if (permanent) FileOps.deletePermanently(f) else FileOps.moveToRecycleBin(f)
                if (done) ok++
            }
            if (permanent) "$ok item${if (ok == 1) "" else "s"} deleted"
            else "$ok item${if (ok == 1) "" else "s"} moved to Recycle Bin"
        }
    }

    Row(Modifier.fillMaxSize()) {
        Sidebar(
            currentPath = currentPath,
            homePath = home.absolutePath,
            onNavigate = ::navigate,
        )
        VerticalDivider(color = theme.onSurface.copy(0.08f))

        Column(Modifier.weight(1f).fillMaxHeight()) {
            CommandBar(
                hasSelection = selected.isNotEmpty(),
                singleSelection = selected.size == 1,
                canPaste = clipboardTick >= 0 && !FileClipboard.isEmpty,
                sortKey = sortKey,
                sortAsc = sortAsc,
                viewMode = viewMode,
                onNewFolder = { showNewFolder = true },
                onCut = {
                    FileClipboard.set(selectedFiles, cut = true); clipboardTick++
                    statusMessage = "${selected.size} item${if (selected.size == 1) "" else "s"} cut"
                },
                onCopy = {
                    FileClipboard.set(selectedFiles, cut = false); clipboardTick++
                    statusMessage = "${selected.size} item${if (selected.size == 1) "" else "s"} copied"
                },
                onPaste = ::paste,
                onRename = { renameTarget = selectedFiles.firstOrNull() },
                onDelete = { confirmDelete = true },
                onSort = { key ->
                    if (sortKey == key) sortAsc = !sortAsc else { sortKey = key; sortAsc = true }
                },
                onToggleView = {
                    viewMode = if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
                },
            )
            HorizontalDivider(color = theme.onSurface.copy(0.08f))

            // Navigation bar: up + breadcrumb + refresh
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { currentDir.parentFile?.let { navigate(it.absolutePath) } },
                    enabled = currentDir.parentFile != null && currentPath != "/",
                    modifier = Modifier.size(28.dp),
                ) {
                    Icon(Icons.Default.ArrowUpward, "Up", Modifier.size(15.dp), tint = theme.onSurface)
                }
                Breadcrumb(
                    currentDir = currentDir,
                    homePath = home.absolutePath,
                    onNavigate = ::navigate,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = ::refresh, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Refresh, "Refresh", Modifier.size(15.dp), tint = theme.onSurface)
                }
            }
            HorizontalDivider(color = theme.onSurface.copy(0.08f))

            Box(Modifier.weight(1f)) {
                when {
                    listing == null -> PermissionHint()

                    sorted.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("This folder is empty", color = theme.onSurface.copy(0.4f), fontSize = 12.sp)
                    }

                    viewMode == ViewMode.GRID -> LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 78.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(6.dp),
                    ) {
                        items(sorted.size, key = { sorted[it].absolutePath }) { i ->
                            val file = sorted[i]
                            EntryContainer(
                                file = file,
                                isSelected = file.absolutePath in selected,
                                menuOpen = menuForPath == file.absolutePath,
                                onDismissMenu = { menuForPath = null },
                                selectionActive = selected.isNotEmpty(),
                                onOpen = ::open,
                                onToggleSelect = { selected = selected.toggle(it.absolutePath) },
                                onLongPress = {
                                    selected = selected + it.absolutePath
                                    menuForPath = it.absolutePath
                                },
                                onCut = { FileClipboard.set(listOf(it), cut = true); clipboardTick++ },
                                onCopy = { FileClipboard.set(listOf(it), cut = false); clipboardTick++ },
                                onRename = { renameTarget = it },
                                onDelete = { selected = setOf(it.absolutePath); confirmDelete = true },
                            ) { GridEntry(file = file, selected = file.absolutePath in selected) }
                        }
                    }

                    else -> Column(Modifier.fillMaxSize()) {
                        ListHeader(sortKey = sortKey, sortAsc = sortAsc) { key ->
                            if (sortKey == key) sortAsc = !sortAsc else { sortKey = key; sortAsc = true }
                        }
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(sorted, key = { it.absolutePath }) { file ->
                                EntryContainer(
                                    file = file,
                                    isSelected = file.absolutePath in selected,
                                    menuOpen = menuForPath == file.absolutePath,
                                    onDismissMenu = { menuForPath = null },
                                    selectionActive = selected.isNotEmpty(),
                                    onOpen = ::open,
                                    onToggleSelect = { selected = selected.toggle(it.absolutePath) },
                                    onLongPress = {
                                        selected = selected + it.absolutePath
                                        menuForPath = it.absolutePath
                                    },
                                    onCut = { FileClipboard.set(listOf(it), cut = true); clipboardTick++ },
                                    onCopy = { FileClipboard.set(listOf(it), cut = false); clipboardTick++ },
                                    onRename = { renameTarget = it },
                                    onDelete = { selected = setOf(it.absolutePath); confirmDelete = true },
                                ) { ListEntry(file = file, selected = file.absolutePath in selected) }
                            }
                        }
                    }
                }
            }

            // Status bar
            HorizontalDivider(color = theme.onSurface.copy(0.08f))
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${sorted.size} item${if (sorted.size == 1) "" else "s"}" +
                        if (selected.isNotEmpty()) "  •  ${selected.size} selected" else "",
                    fontSize = 10.sp,
                    color = theme.onSurface.copy(0.5f),
                )
                Spacer(Modifier.weight(1f))
                statusMessage?.let {
                    Text(it, fontSize = 10.sp, color = theme.accentColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }

    renameTarget?.let { target ->
        NameDialog(
            title = "Rename",
            initial = target.name,
            confirmLabel = "Rename",
            onDismiss = { renameTarget = null },
        ) { newName ->
            renameTarget = null
            runOp { if (FileOps.rename(target, newName)) "Renamed to $newName" else "Couldn't rename" }
        }
    }

    if (showNewFolder) {
        NameDialog(
            title = "New folder",
            initial = "New folder",
            confirmLabel = "Create",
            onDismiss = { showNewFolder = false },
        ) { name ->
            showNewFolder = false
            runOp {
                if (FileOps.newFolder(currentDir, name.ifBlank { "New folder" }) != null) "Folder created"
                else "Couldn't create folder"
            }
        }
    }

    if (confirmDelete && selected.isNotEmpty()) {
        val permanent = FileOps.isInRecycleBin(currentDir)
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(if (permanent) "Delete permanently?" else "Delete ${selected.size} item${if (selected.size == 1) "" else "s"}?") },
            text = {
                Text(
                    if (permanent) "These items will be permanently deleted. This can't be undone."
                    else "Items will be moved to the Recycle Bin.",
                )
            },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; deleteSelected() }) {
                    Text("Delete", color = Color(0xFFFF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Cancel") }
            },
        )
    }
}

// ─── Sidebar ───

private data class SidebarItem(val label: String, val icon: ImageVector, val path: () -> String)

@Composable
private fun Sidebar(currentPath: String, homePath: String, onNavigate: (String) -> Unit) {
    val theme = LocalLauncherTheme.current
    val items = remember {
        listOf(
            SidebarItem("Home", Icons.Default.Home) { homePath },
            SidebarItem("Documents", Icons.Default.Description) { publicDir(Environment.DIRECTORY_DOCUMENTS) },
            SidebarItem("Downloads", Icons.Default.Download) { publicDir(Environment.DIRECTORY_DOWNLOADS) },
            SidebarItem("Pictures", Icons.Default.Image) { publicDir(Environment.DIRECTORY_PICTURES) },
            SidebarItem("Music", Icons.Default.MusicNote) { publicDir(Environment.DIRECTORY_MUSIC) },
            SidebarItem("Videos", Icons.Default.VideoLibrary) { publicDir(Environment.DIRECTORY_MOVIES) },
            SidebarItem("Recycle Bin", Icons.Default.Delete) { FileOps.recycleBinDir().absolutePath },
            SidebarItem("This PC", Icons.Default.Computer) { "/storage" },
        )
    }
    Column(
        Modifier.width(122.dp).fillMaxHeight().verticalScroll(rememberScrollState())
            .background(theme.onSurface.copy(alpha = 0.04f)).padding(6.dp),
    ) {
        Text(
            "NeverSoft 11",
            fontSize = 9.sp,
            color = theme.onSurface.copy(0.5f),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
        )
        items.forEach { item ->
            val path = item.path()
            val isSelected = currentPath == path
            Row(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) theme.accentColor.copy(0.18f) else Color.Transparent)
                    .clickable { onNavigate(path) }
                    .padding(horizontal = 7.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(item.icon, null, Modifier.size(15.dp), tint = theme.onSurface.copy(0.75f))
                Spacer(Modifier.width(6.dp))
                Text(
                    item.label, fontSize = 11.sp, color = theme.onSurface,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun publicDir(type: String): String =
    Environment.getExternalStoragePublicDirectory(type).absolutePath

// ─── Command bar ───

@Composable
private fun CommandBar(
    hasSelection: Boolean,
    singleSelection: Boolean,
    canPaste: Boolean,
    sortKey: SortKey,
    sortAsc: Boolean,
    viewMode: ViewMode,
    onNewFolder: () -> Unit,
    onCut: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onSort: (SortKey) -> Unit,
    onToggleView: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    var sortMenuOpen by remember { mutableStateOf(false) }

    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CommandButton(Icons.Default.CreateNewFolder, "New folder", enabled = true, onClick = onNewFolder)
        CommandButton(Icons.Default.ContentCut, "Cut", enabled = hasSelection, onClick = onCut)
        CommandButton(Icons.Default.ContentCopy, "Copy", enabled = hasSelection, onClick = onCopy)
        CommandButton(Icons.Default.ContentPaste, "Paste", enabled = canPaste, onClick = onPaste)
        CommandButton(Icons.Default.DriveFileRenameOutline, "Rename", enabled = singleSelection, onClick = onRename)
        CommandButton(Icons.Default.Delete, "Delete", enabled = hasSelection, onClick = onDelete)
        VerticalDivider(Modifier.height(18.dp).padding(horizontal = 2.dp), color = theme.onSurface.copy(0.15f))
        Box {
            CommandButton(Icons.AutoMirrored.Filled.Sort, "Sort", enabled = true) { sortMenuOpen = true }
            DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                SortKey.entries.forEach { key ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                key.name.lowercase().replaceFirstChar { it.uppercase() } +
                                    if (sortKey == key) (if (sortAsc) "  ↑" else "  ↓") else "",
                                fontSize = 12.sp,
                            )
                        },
                        onClick = { onSort(key); sortMenuOpen = false },
                    )
                }
            }
        }
        CommandButton(
            if (viewMode == ViewMode.LIST) Icons.Default.GridView else Icons.AutoMirrored.Filled.ViewList,
            "Toggle view",
            enabled = true,
            onClick = onToggleView,
        )
    }
}

@Composable
private fun CommandButton(icon: ImageVector, label: String, enabled: Boolean, onClick: () -> Unit) {
    val theme = LocalLauncherTheme.current
    IconButton(onClick = onClick, enabled = enabled, modifier = Modifier.size(30.dp)) {
        Icon(
            icon, label, Modifier.size(16.dp),
            tint = if (enabled) theme.onSurface.copy(0.85f) else theme.onSurface.copy(0.25f),
        )
    }
}

// ─── Breadcrumb ───

@Composable
private fun Breadcrumb(
    currentDir: File,
    homePath: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current
    val crumbs = remember(currentDir) { buildCrumbs(currentDir, homePath) }
    Row(
        modifier.horizontalScroll(rememberScrollState(Int.MAX_VALUE)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        crumbs.forEachIndexed { i, (label, path) ->
            if (i > 0) {
                Icon(
                    Icons.Default.ChevronRight, null, Modifier.size(12.dp),
                    tint = theme.onSurface.copy(0.35f),
                )
            }
            Text(
                label,
                fontSize = 11.sp,
                color = if (i == crumbs.lastIndex) theme.onSurface else theme.onSurface.copy(0.6f),
                maxLines = 1,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onNavigate(path) }
                    .padding(horizontal = 4.dp, vertical = 3.dp),
            )
        }
    }
}

private fun buildCrumbs(dir: File, homePath: String): List<Pair<String, String>> {
    val path = dir.absolutePath
    return if (path.startsWith(homePath)) {
        val crumbs = mutableListOf("Home" to homePath)
        val rel = path.removePrefix(homePath).trim('/')
        var acc = homePath
        if (rel.isNotEmpty()) rel.split('/').forEach { seg ->
            acc = "$acc/$seg"
            crumbs += (if (seg == FileOps.RECYCLE_DIR_NAME) "Recycle Bin" else seg) to acc
        }
        crumbs
    } else {
        val crumbs = mutableListOf("/" to "/")
        var acc = ""
        path.trim('/').split('/').filter { it.isNotEmpty() }.forEach { seg ->
            acc = "$acc/$seg"
            crumbs += seg to acc
        }
        crumbs
    }
}

// ─── Entries ───

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EntryContainer(
    file: File,
    isSelected: Boolean,
    menuOpen: Boolean,
    onDismissMenu: () -> Unit,
    selectionActive: Boolean,
    onOpen: (File) -> Unit,
    onToggleSelect: (File) -> Unit,
    onLongPress: (File) -> Unit,
    onCut: (File) -> Unit,
    onCopy: (File) -> Unit,
    onRename: (File) -> Unit,
    onDelete: (File) -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        Modifier.combinedClickable(
            onClick = { if (selectionActive) onToggleSelect(file) else onOpen(file) },
            onLongClick = { onLongPress(file) },
        ),
    ) {
        content()
        DropdownMenu(expanded = menuOpen, onDismissRequest = onDismissMenu) {
            MenuItem("Open") { onDismissMenu(); onOpen(file) }
            MenuItem("Cut") { onDismissMenu(); onCut(file) }
            MenuItem("Copy") { onDismissMenu(); onCopy(file) }
            MenuItem("Rename") { onDismissMenu(); onRename(file) }
            HorizontalDivider()
            MenuItem("Delete", danger = true) { onDismissMenu(); onDelete(file) }
        }
    }
}

@Composable
private fun MenuItem(label: String, danger: Boolean = false, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(label, fontSize = 12.sp, color = if (danger) Color(0xFFFF4444) else Color.Unspecified) },
        onClick = onClick,
    )
}

@Composable
private fun ListHeader(sortKey: SortKey, sortAsc: Boolean, onSort: (SortKey) -> Unit) {
    val theme = LocalLauncherTheme.current

    @Composable
    fun cell(label: String, key: SortKey, modifier: Modifier, align: TextAlign = TextAlign.Start) {
        Text(
            label + if (sortKey == key) (if (sortAsc) " ↑" else " ↓") else "",
            fontSize = 9.sp,
            color = if (sortKey == key) theme.accentColor else theme.onSurface.copy(0.5f),
            textAlign = align,
            maxLines = 1,
            modifier = modifier.clickable { onSort(key) }.padding(vertical = 4.dp),
        )
    }
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        cell("NAME", SortKey.NAME, Modifier.weight(1f))
        cell("MODIFIED", SortKey.DATE, Modifier.width(62.dp), TextAlign.End)
        cell("SIZE", SortKey.SIZE, Modifier.width(52.dp), TextAlign.End)
    }
    HorizontalDivider(color = theme.onSurface.copy(0.06f))
}

@Composable
private fun ListEntry(file: File, selected: Boolean) {
    val theme = LocalLauncherTheme.current
    Row(
        Modifier.fillMaxWidth()
            .background(if (selected) theme.accentColor.copy(0.22f) else Color.Transparent)
            .padding(vertical = 6.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            entryIcon(file),
            contentDescription = null,
            tint = if (file.isDirectory) Color(0xFFFFD262) else theme.onSurface.copy(0.6f),
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            file.name, fontSize = 12.sp, color = theme.onSurface, maxLines = 1,
            overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f),
        )
        Text(
            formatDate(file.lastModified()),
            fontSize = 9.sp,
            color = theme.onSurface.copy(0.4f),
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier.width(62.dp),
        )
        Text(
            if (file.isDirectory) "—" else formatSize(file.length()),
            fontSize = 9.sp,
            color = theme.onSurface.copy(0.4f),
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier.width(52.dp),
        )
    }
}

@Composable
private fun GridEntry(file: File, selected: Boolean) {
    val theme = LocalLauncherTheme.current
    Column(
        Modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) theme.accentColor.copy(0.22f) else Color.Transparent)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            entryIcon(file),
            contentDescription = null,
            tint = if (file.isDirectory) Color(0xFFFFD262) else theme.onSurface.copy(0.6f),
            modifier = Modifier.size(32.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            file.name, fontSize = 10.sp, color = theme.onSurface, maxLines = 2,
            overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, lineHeight = 12.sp,
        )
    }
}

@Composable
private fun PermissionHint() {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Default.Lock, null, Modifier.size(32.dp), tint = theme.onSurface.copy(0.4f))
        Spacer(Modifier.height(8.dp))
        Text("Can't read this folder", color = theme.onSurface.copy(0.7f), fontSize = 13.sp)
        Text("Storage access may be required", color = theme.onSurface.copy(0.4f), fontSize = 11.sp)
        Spacer(Modifier.height(10.dp))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            TextButton(onClick = {
                runCatching {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Uri.parse("package:${context.packageName}"),
                        ),
                    )
                }
            }) {
                Text("Grant all-files access", color = theme.accentColor, fontSize = 12.sp)
            }
        }
    }
}

// ─── Dialogs ───

@Composable
private fun NameDialog(
    title: String,
    initial: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var value by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(value = value, onValueChange = { value = it }, singleLine = true)
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(value) }, enabled = value.isNotBlank()) { Text(confirmLabel) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

// ─── Helpers ───

private fun Set<String>.toggle(path: String): Set<String> =
    if (path in this) this - path else this + path

private fun sortEntries(entries: List<File>?, key: SortKey, asc: Boolean): List<File> {
    if (entries == null) return emptyList()
    val dir = if (asc) 1 else -1
    // Directories always float to the top regardless of sort direction.
    return entries.sortedWith(
        compareBy<File> { !it.isDirectory }.thenComparator { a, b ->
            val cmp = when (key) {
                SortKey.SIZE -> a.length().compareTo(b.length())
                SortKey.DATE -> a.lastModified().compareTo(b.lastModified())
                SortKey.TYPE -> a.extension.lowercase().compareTo(b.extension.lowercase())
                SortKey.NAME -> 0
            }.let { if (it != 0) it else a.name.lowercase().compareTo(b.name.lowercase()) }
            cmp * dir
        },
    )
}

private fun entryIcon(file: File): ImageVector {
    if (file.isDirectory) return Icons.Default.Folder
    return when (file.extension.lowercase(Locale.ROOT)) {
        "jpg", "jpeg", "png", "gif", "webp", "bmp" -> Icons.Default.Image
        "mp4", "mkv", "webm", "avi", "mov" -> Icons.Default.VideoFile
        "mp3", "wav", "ogg", "flac", "m4a" -> Icons.Default.AudioFile
        "pdf" -> Icons.Default.PictureAsPdf
        "zip", "rar", "7z", "tar", "gz" -> Icons.Default.FolderZip
        "apk" -> Icons.Default.Android
        "txt", "md", "log", "json", "xml" -> Icons.Default.Description
        else -> Icons.AutoMirrored.Filled.InsertDriveFile
    }
}

private fun formatDate(millis: Long): String =
    if (millis <= 0) "—" else SimpleDateFormat("M/d/yy", Locale.getDefault()).format(Date(millis))

private fun formatSize(bytes: Long): String = when {
    bytes >= 1_073_741_824 -> String.format(Locale.ROOT, "%.1f GB", bytes / 1_073_741_824.0)
    bytes >= 1_048_576 -> String.format(Locale.ROOT, "%.1f MB", bytes / 1_048_576.0)
    bytes >= 1_024 -> String.format(Locale.ROOT, "%.1f KB", bytes / 1_024.0)
    else -> "$bytes B"
}
