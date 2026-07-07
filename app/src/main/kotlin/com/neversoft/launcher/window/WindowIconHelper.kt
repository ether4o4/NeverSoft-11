package com.neversoft.launcher.window
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

fun toIcon(type: WindowContentType): ImageVector = when (type) {
    WindowContentType.FILE_EXPLORER  -> Icons.Default.Folder
    WindowContentType.CONTROL_PANEL -> Icons.Default.Settings
    WindowContentType.BROWSER       -> Icons.Default.Language
    WindowContentType.TERMINAL      -> Icons.Default.Terminal
    else                             -> Icons.Default.WebAsset
}
