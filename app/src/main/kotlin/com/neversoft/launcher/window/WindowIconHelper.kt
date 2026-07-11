package com.neversoft.launcher.window
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.ui.graphics.vector.ImageVector

fun toIcon(type: WindowContentType): ImageVector = when (type) {
    WindowContentType.FILE_EXPLORER -> Icons.Filled.Folder
    WindowContentType.SETTINGS      -> Icons.Outlined.Settings
    WindowContentType.BROWSER       -> Icons.Outlined.Language
    WindowContentType.TERMINAL      -> Icons.Outlined.Terminal
    WindowContentType.PHOTOS        -> Icons.Outlined.Photo
    WindowContentType.MUSIC         -> Icons.Outlined.MusicNote
}
