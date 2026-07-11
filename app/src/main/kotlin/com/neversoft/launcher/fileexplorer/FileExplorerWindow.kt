package com.neversoft.launcher.fileexplorer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.neversoft.launcher.files.FileOpener
import com.neversoft.launcher.files.Trash
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.ui.AccentButton
import com.neversoft.launcher.ui.SubtleButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class Clipboard(val source: File, val isCut: Boolean)

// Windows 11 File Explorer: command bar, breadcrumb address bar,
// navigation sidebar, details list, working file operations.
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileExplorerWindow(initialPath: String? = null, windowTitle: String = "File Explorer") {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    val home = remember { Environment.getExternalStorageDirectory() }

    var currentDir by remember { mutableStateOf(File(initialPath ?: home.absolutePath)) }
    var refreshTick by remember { mutableIntStateOf(0) }
    var selectedPath by remember { mutableStateOf<String?>(null) }
    var clipboard by remember { mutableStateOf<Clipboard?>(null) }
    var renameTarget by remember { mutableStateOf<File?>(null) }
    val backStack = remember { ArrayDeque<File>() }

    val inTrash = Trash.isTrash(currentDir)

    // null means the directory could not be read (missing permission or unreadable path)
    val listing: List<File>? = remember(currentDir, refreshTick) {
        currentDir.listFiles()
            ?.filter { !(it.name.startsWith(".") && it.parentFile?.absolutePath == home.absolutePath) }
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
    }

    fun navigate(dir: File) {
        if (dir.absolutePath == currentDir.absolutePath) return
        backStack.addLast(currentDir)
        currentDir = dir
        selectedPath = null
    }

    fun goBack() {
        backStack.removeLastOrNull()?.let {
            currentDir = it
            selectedPath = null
        }
    }

    val selectedFile = selectedPath?.let { File(it) }?.takeIf { it.exists() }

    Column(Modifier.fillMaxSize()) {
        // ————— Command bar —————
        Row(
            Modifier.fillMaxWidth().height(44.dp).padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        val target = Trash.uniqueName(currentDir, "New folder")
                        if (target.mkdirs()) refreshTick++
                    }
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Add, null, Modifier.size(16.dp), tint = theme.accent)
                Spacer(Modifier.width(6.dp))
                Text("New", color = theme.text, fontSize = 13.sp)
            }
            Box(Modifier.padding(horizontal = 8.dp).size(1.dp, 22.dp).background(theme.divider))

            CommandIcon(Icons.Outlined.ContentCut, "Cut", enabled = selectedFile != null) {
                selectedFile?.let { clipboard = Clipboard(it, isCut = true) }
            }
            CommandIcon(Icons.Outlined.ContentCopy, "Copy", enabled = selectedFile != null) {
                selectedFile?.let { clipboard = Clipboard(it, isCut = false) }
            }
            CommandIcon(Icons.Outlined.ContentPaste, "Paste", enabled = clipboard != null) {
                clipboard?.let { clip ->
                    val target = Trash.uniqueName(currentDir, clip.source.name)
                    val ok = runCatching {
                        if (clip.isCut) {
                            clip.source.renameTo(target) ||
                                (clip.source.copyRecursively(target) && clip.source.deleteRecursively())
                        } else {
                            clip.source.copyRecursively(target)
                        }
                    }.getOrDefault(false)
                    if (ok) {
                        if (clip.isCut) clipboard = null
                        refreshTick++
                    }
                }
            }
            CommandIcon(Icons.Outlined.DriveFileRenameOutline, "Rename", enabled = selectedFile != null) {
                renameTarget = selectedFile
            }
            CommandIcon(Icons.Outlined.Delete, "Delete", enabled = selectedFile != null) {
                selectedFile?.let { file ->
                    val ok = if (inTrash) file.deleteRecursively() else Trash.moveToTrash(file)
                    if (ok) {
                        selectedPath = null
                        refreshTick++
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            if (inTrash && listing?.isNotEmpty() == true) {
                Text(
                    "Empty Recycle Bin",
                    color = theme.accent, fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            listing.forEach { it.deleteRecursively() }
                            refreshTick++
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(theme.divider))

        // ————— Address bar —————
        Row(
            Modifier.fillMaxWidth().height(40.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CommandIcon(Icons.Outlined.ArrowBack, "Back", enabled = backStack.isNotEmpty()) { goBack() }
            CommandIcon(
                Icons.Outlined.ArrowUpward, "Up",
                enabled = currentDir.parentFile != null && currentDir.absolutePath != "/",
            ) {
                currentDir.parentFile?.let { navigate(it) }
            }
            Spacer(Modifier.width(4.dp))
            // Breadcrumbs
            Row(
                Modifier
                    .weight(1f)
                    .height(30.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(theme.inputField)
                    .border(1.dp, theme.stroke, RoundedCornerShape(4.dp))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                breadcrumbsFor(currentDir, home, windowTitle).forEachIndexed { index, (label, dir) ->
                    if (index > 0) {
                        Icon(Icons.Outlined.ChevronRight, null, Modifier.size(14.dp), tint = theme.textSecondary)
                    }
                    Text(
                        label,
                        color = theme.text, fontSize = 12.sp, maxLines = 1,
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .clickable(enabled = dir != null) { dir?.let { navigate(it) } }
                            .padding(horizontal = 5.dp, vertical = 3.dp),
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
            CommandIcon(Icons.Outlined.Refresh, "Refresh", enabled = true) { refreshTick++ }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(theme.divider))

        // ————— Sidebar + file list —————
        Row(Modifier.fillMaxWidth().weight(1f)) {
            Column(
                Modifier.width(132.dp).fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 6.dp, horizontal = 5.dp),
            ) {
                SidebarItem("Home", Icons.Outlined.Home, currentDir == home) { navigate(home) }
                SidebarItem(
                    "Downloads", Icons.Outlined.Download,
                    currentDir.name == "Download",
                ) { navigate(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)) }
                SidebarItem(
                    "Documents", Icons.Outlined.Description,
                    currentDir.name == "Documents",
                ) { navigate(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)) }
                SidebarItem(
                    "Pictures", Icons.Outlined.Image,
                    currentDir.name == "Pictures",
                ) { navigate(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)) }
                SidebarItem(
                    "Music", Icons.Outlined.MusicNote,
                    currentDir.name == "Music",
                ) { navigate(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)) }
                SidebarItem(
                    "Videos", Icons.Outlined.VideoLibrary,
                    currentDir.name == "Movies",
                ) { navigate(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)) }
                Box(Modifier.fillMaxWidth().padding(vertical = 5.dp).height(1.dp).background(theme.divider))
                SidebarItem("This PC", Icons.Outlined.Computer, currentDir.absolutePath == "/storage") {
                    navigate(File("/storage"))
                }
                SidebarItem("Recycle Bin", Icons.Outlined.Delete, inTrash) { navigate(Trash.dir()) }
            }
            Box(Modifier.width(1.dp).fillMaxHeight().background(theme.divider))

            Column(Modifier.weight(1f).fillMaxHeight()) {
                // Column headers
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Name", color = theme.textSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f))
                    Text("Modified", color = theme.textSecondary, fontSize = 11.sp, modifier = Modifier.width(70.dp))
                    Text("Size", color = theme.textSecondary, fontSize = 11.sp, modifier = Modifier.width(58.dp))
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(theme.divider))

                Box(Modifier.fillMaxWidth().weight(1f)) {
                    when {
                        listing == null -> PermissionEmptyState()
                        listing.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                if (inTrash) "Recycle Bin is empty" else "This folder is empty",
                                color = theme.textSecondary, fontSize = 12.sp,
                            )
                        }
                        else -> LazyColumn(Modifier.fillMaxSize()) {
                            items(listing, key = { it.absolutePath }) { file ->
                                val isSelected = file.absolutePath == selectedPath
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(if (isSelected) theme.accent.copy(alpha = 0.18f) else Color.Transparent)
                                        .combinedClickable(
                                            onClick = {
                                                if (file.isDirectory) navigate(file)
                                                else FileOpener.open(context, file)
                                            },
                                            onLongClick = {
                                                selectedPath =
                                                    if (isSelected) null else file.absolutePath
                                            },
                                        )
                                        .padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        if (file.isDirectory) Icons.Filled.Folder else fileIcon(file.name),
                                        contentDescription = null,
                                        tint = if (file.isDirectory) Color(0xFFFFCA28) else theme.textSecondary,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Spacer(Modifier.width(9.dp))
                                    Text(
                                        file.name, fontSize = 12.sp, color = theme.text, maxLines = 1,
                                        overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f),
                                    )
                                    Text(
                                        SimpleDateFormat("M/d/yy", Locale.getDefault())
                                            .format(Date(file.lastModified())),
                                        fontSize = 10.sp, color = theme.textSecondary,
                                        modifier = Modifier.width(70.dp),
                                    )
                                    Text(
                                        if (file.isDirectory) "" else formatSize(file.length()),
                                        fontSize = 10.sp, color = theme.textSecondary,
                                        modifier = Modifier.width(58.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                // Status bar
                Box(Modifier.fillMaxWidth().height(1.dp).background(theme.divider))
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${listing?.size ?: 0} items" +
                            (if (selectedFile != null) "   |   1 item selected" else ""),
                        color = theme.textSecondary, fontSize = 11.sp,
                    )
                }
            }
        }
    }

    // Rename dialog
    renameTarget?.let { target ->
        var newName by remember(target) { mutableStateOf(target.name) }
        Dialog(onDismissRequest = { renameTarget = null }) {
            Column(
                Modifier
                    .width(300.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.windowSurface)
                    .border(1.dp, theme.stroke, RoundedCornerShape(8.dp))
                    .padding(20.dp),
            ) {
                Text("Rename", color = theme.text, fontSize = 16.sp)
                Spacer(Modifier.height(14.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(theme.inputField)
                        .border(1.dp, theme.stroke, RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    BasicTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        singleLine = true,
                        textStyle = TextStyle(color = theme.text, fontSize = 13.sp),
                        cursorBrush = SolidColor(theme.accent),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccentButton("OK", modifier = Modifier.weight(1f), onClick = {
                        if (newName.isNotBlank() && newName != target.name) {
                            if (target.renameTo(File(target.parentFile, newName.trim()))) {
                                selectedPath = null
                                refreshTick++
                            }
                        }
                        renameTarget = null
                    })
                    SubtleButton("Cancel", modifier = Modifier.weight(1f), onClick = { renameTarget = null })
                }
            }
        }
    }
}

@Composable
private fun CommandIcon(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Box(
        Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon, description, Modifier.size(16.dp),
            tint = if (enabled) theme.text else theme.textDisabled,
        )
    }
}

@Composable
private fun SidebarItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) theme.hover else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, Modifier.size(15.dp), tint = if (selected) theme.accent else theme.textSecondary)
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 12.sp, color = theme.text, maxLines = 1)
    }
}

@Composable
private fun PermissionEmptyState() {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Outlined.Lock, null, Modifier.size(30.dp), tint = theme.textDisabled)
        Spacer(Modifier.height(8.dp))
        Text("Can't read this folder", color = theme.text, fontSize = 13.sp)
        Text("Storage access may be required", color = theme.textSecondary, fontSize = 11.sp)
        Spacer(Modifier.height(12.dp))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            AccentButton("Grant all-files access", onClick = {
                runCatching {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Uri.parse("package:${context.packageName}"),
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                }
            })
        }
    }
}

// Breadcrumb segments: label + navigable dir (null = not navigable)
private fun breadcrumbsFor(
    currentDir: File,
    home: File,
    windowTitle: String,
): List<Pair<String, File?>> {
    val homePath = home.absolutePath
    val path = currentDir.absolutePath
    return when {
        Trash.isTrash(currentDir) -> listOf("Recycle Bin" to null)
        path.startsWith(homePath) -> {
            val crumbs = mutableListOf<Pair<String, File?>>("Home" to home)
            val rest = path.removePrefix(homePath).trim('/')
            if (rest.isNotEmpty()) {
                var acc = home
                rest.split('/').forEach { part ->
                    acc = File(acc, part)
                    crumbs += part to acc
                }
            }
            crumbs
        }
        else -> {
            val crumbs = mutableListOf<Pair<String, File?>>("This PC" to File("/storage"))
            val rest = path.removePrefix("/storage").trim('/')
            if (rest.isNotEmpty()) {
                var acc = File("/storage")
                rest.split('/').forEach { part ->
                    acc = File(acc, part)
                    crumbs += part to acc
                }
            } else if (path != "/storage") {
                crumbs += path to currentDir
            }
            crumbs
        }
    }
}

private fun fileIcon(name: String): ImageVector {
    val ext = name.substringAfterLast('.', "").lowercase(Locale.ROOT)
    return when (ext) {
        "jpg", "jpeg", "png", "gif", "webp", "bmp" -> Icons.Outlined.Image
        "mp4", "mkv", "webm", "avi", "mov" -> Icons.Outlined.VideoFile
        "mp3", "wav", "ogg", "flac", "m4a" -> Icons.Outlined.AudioFile
        "pdf" -> Icons.Outlined.PictureAsPdf
        "zip", "rar", "7z", "tar", "gz" -> Icons.Outlined.FolderZip
        "apk" -> Icons.Outlined.Android
        "txt", "md", "log", "json", "xml" -> Icons.Outlined.Description
        else -> Icons.AutoMirrored.Outlined.InsertDriveFile
    }
}

private fun formatSize(bytes: Long): String = when {
    bytes >= 1_073_741_824 -> String.format(Locale.ROOT, "%.1f GB", bytes / 1_073_741_824.0)
    bytes >= 1_048_576 -> String.format(Locale.ROOT, "%.1f MB", bytes / 1_048_576.0)
    bytes >= 1_024 -> String.format(Locale.ROOT, "%.1f KB", bytes / 1_024.0)
    else -> "$bytes B"
}
