package com.neversoft.launcher.fileexplorer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.neversoft.launcher.theme.LocalLauncherTheme
import java.io.File
import java.util.Locale

@Composable
fun FileExplorerWindow(initialPath: String? = null) {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    val home = remember { Environment.getExternalStorageDirectory() }

    var selected by remember { mutableStateOf("Home") }
    var currentDir by remember { mutableStateOf(File(initialPath ?: home.absolutePath)) }
    var refreshTick by remember { mutableIntStateOf(0) }

    // null means the directory could not be read (missing permission or unreadable path)
    val listing: List<File>? = remember(currentDir, refreshTick) {
        currentDir.listFiles()
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
    }

    val navItems = listOf("Home", "Documents", "Downloads", "Pictures", "Music", "Videos", "This PC")

    fun navigate(label: String) {
        selected = label
        currentDir = when (label) {
            "Home" -> home
            "Documents" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            "Downloads" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            "Pictures" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            "Music" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            "Videos" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
            "This PC" -> File("/storage")
            else -> home
        }
    }

    Row(Modifier.fillMaxSize()) {
        Column(Modifier.width(150.dp).fillMaxHeight().verticalScroll(rememberScrollState())
            .background(theme.surfaceColor.copy(alpha = 0.3f)).padding(8.dp)) {
            Text("NeverSoft 11", fontSize = 10.sp, color = theme.onSurface.copy(0.5f), modifier = Modifier.padding(4.dp))
            navItems.forEach { item ->
                Row(Modifier.fillMaxWidth().clickable { navigate(item) }
                    .background(if (selected == item) theme.accentColor.copy(0.15f) else Color.Transparent,
                        RoundedCornerShape(6.dp)).padding(8.dp, 6.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(navIcon(item), null, Modifier.size(16.dp), tint = theme.onSurface.copy(0.7f))
                    Spacer(Modifier.width(6.dp))
                    Text(item, fontSize = 12.sp, color = theme.onSurface)
                }
            }
        }
        Divider(Modifier.width(1.dp).fillMaxHeight(), color = theme.accentColor.copy(0.2f))
        Column(Modifier.weight(1f).fillMaxHeight().padding(10.dp)) {
            // Toolbar: up + path + refresh
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { currentDir.parentFile?.let { currentDir = it } },
                    enabled = currentDir.parentFile != null && currentDir.absolutePath != "/",
                    modifier = Modifier.size(28.dp),
                ) {
                    Icon(Icons.Default.ArrowUpward, "Up", Modifier.size(16.dp), tint = theme.onSurface)
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    currentDir.absolutePath,
                    fontSize = 11.sp,
                    color = theme.onSurface.copy(0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { refreshTick++ }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Refresh, "Refresh", Modifier.size(16.dp), tint = theme.onSurface)
                }
            }
            Spacer(Modifier.height(6.dp))
            Divider(color = theme.onSurface.copy(0.1f))
            Spacer(Modifier.height(6.dp))

            when {
                listing == null -> Column(
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

                listing.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("This folder is empty", color = theme.onSurface.copy(0.4f), fontSize = 12.sp)
                }

                else -> LazyColumn(Modifier.fillMaxSize()) {
                    items(listing, key = { it.absolutePath }) { file ->
                        FileRow(file = file, onSurface = theme.onSurface, accent = theme.accentColor) {
                            if (file.isDirectory) {
                                currentDir = file
                                selected = ""
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FileRow(file: File, onSurface: Color, accent: Color, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            if (file.isDirectory) Icons.Default.Folder else fileIcon(file.name),
            contentDescription = null,
            tint = if (file.isDirectory) accent else onSurface.copy(0.6f),
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(file.name, fontSize = 12.sp, color = onSurface, maxLines = 1,
            overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        Text(
            if (file.isDirectory) {
                val count = file.list()?.size
                if (count != null) "$count items" else ""
            } else formatSize(file.length()),
            fontSize = 10.sp,
            color = onSurface.copy(0.4f),
        )
    }
}

private fun fileIcon(name: String): ImageVector {
    val ext = name.substringAfterLast('.', "").lowercase(Locale.ROOT)
    return when (ext) {
        "jpg", "jpeg", "png", "gif", "webp", "bmp" -> Icons.Default.Image
        "mp4", "mkv", "webm", "avi", "mov" -> Icons.Default.VideoFile
        "mp3", "wav", "ogg", "flac", "m4a" -> Icons.Default.AudioFile
        "pdf" -> Icons.Default.PictureAsPdf
        "zip", "rar", "7z", "tar", "gz" -> Icons.Default.FolderZip
        "apk" -> Icons.Default.Android
        "txt", "md", "log", "json", "xml" -> Icons.Default.Description
        else -> Icons.Default.InsertDriveFile
    }
}

private fun formatSize(bytes: Long): String = when {
    bytes >= 1_073_741_824 -> String.format(Locale.ROOT, "%.1f GB", bytes / 1_073_741_824.0)
    bytes >= 1_048_576 -> String.format(Locale.ROOT, "%.1f MB", bytes / 1_048_576.0)
    bytes >= 1_024 -> String.format(Locale.ROOT, "%.1f KB", bytes / 1_024.0)
    else -> "$bytes B"
}

fun navIcon(label: String): ImageVector = when (label) {
    "Home" -> Icons.Default.Home
    "Documents" -> Icons.Default.Description
    "Downloads" -> Icons.Default.Download
    "Pictures" -> Icons.Default.Image
    "Music" -> Icons.Default.MusicNote
    "Videos" -> Icons.Default.VideoLibrary
    "This PC" -> Icons.Default.Computer
    else -> Icons.Default.Folder
}
