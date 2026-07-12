package com.neversoft.launcher.controlpanel

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neversoft.launcher.data.AppSettings
import com.neversoft.launcher.files.ImageStore
import com.neversoft.launcher.theme.LauncherThemes
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.theme.ThemePreset
import com.neversoft.launcher.ui.AccentButton
import com.neversoft.launcher.ui.SubtleButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private enum class SettingsPage(val label: String) {
    HOME("Home"), SYSTEM("System"), PERSONALIZATION("Personalization"), APPS("Apps")
}

// Windows 11 Settings app: left navigation + content pane.
@Composable
fun SettingsWindow(
    selectedPreset: ThemePreset,
    onSelectPreset: (ThemePreset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current
    var page by remember { mutableStateOf(SettingsPage.PERSONALIZATION) }

    Row(modifier.fillMaxSize()) {
        // Navigation
        Column(
            Modifier.width(136.dp).fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp, horizontal = 5.dp),
        ) {
            NavItem("Home", Icons.Outlined.Home, page == SettingsPage.HOME) { page = SettingsPage.HOME }
            NavItem("System", Icons.Outlined.Monitor, page == SettingsPage.SYSTEM) { page = SettingsPage.SYSTEM }
            NavItem("Personalization", Icons.Outlined.Palette, page == SettingsPage.PERSONALIZATION) {
                page = SettingsPage.PERSONALIZATION
            }
            NavItem("Apps", Icons.Outlined.Apps, page == SettingsPage.APPS) { page = SettingsPage.APPS }
        }
        Box(Modifier.width(1.dp).fillMaxHeight().background(theme.divider))

        Column(
            Modifier.weight(1f).fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            when (page) {
                SettingsPage.HOME -> HomePage()
                SettingsPage.SYSTEM -> SystemPage()
                SettingsPage.PERSONALIZATION -> PersonalizationPage(selectedPreset, onSelectPreset)
                SettingsPage.APPS -> AppsPage()
            }
        }
    }
}

@Composable
private fun NavItem(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    val theme = LocalLauncherTheme.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) theme.hover else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selected) {
            Box(
                Modifier.size(width = 3.dp, height = 14.dp)
                    .background(theme.accent, RoundedCornerShape(2.dp)),
            )
            Spacer(Modifier.width(6.dp))
        } else {
            Spacer(Modifier.width(9.dp))
        }
        Icon(icon, null, Modifier.size(15.dp), tint = theme.text)
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 12.sp, color = theme.text, maxLines = 1)
    }
}

@Composable
private fun PageTitle(text: String) {
    Text(
        text, color = LocalLauncherTheme.current.text,
        fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun SettingsCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
) {
    val theme = LocalLauncherTheme.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(theme.card)
            .border(1.dp, theme.stroke, RoundedCornerShape(6.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, Modifier.size(19.dp), tint = theme.text)
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = theme.text, fontSize = 13.sp)
            Text(subtitle, color = theme.textSecondary, fontSize = 11.sp)
        }
        if (onClick != null) {
            Icon(Icons.Outlined.ChevronRight, null, Modifier.size(15.dp), tint = theme.textSecondary)
        }
    }
}

@Composable
private fun HomePage() {
    val theme = LocalLauncherTheme.current
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(theme.accent),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    Build.MODEL.take(1).uppercase(), color = theme.accentText,
                    fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(Build.MODEL, color = theme.text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text("NeverSoft 11", color = theme.textSecondary, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        SettingsCard("System", "Display, sound, notifications", Icons.Outlined.Monitor) {
            context.open(Settings.ACTION_SETTINGS)
        }
        SettingsCard("Android settings", "Open the device settings app", Icons.Outlined.Info) {
            context.open(Settings.ACTION_SETTINGS)
        }
    }
}

@Composable
private fun SystemPage() {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PageTitle("System")
        Spacer(Modifier.height(4.dp))
        SettingsCard("Display", "Brightness, sleep, screen", Icons.Outlined.Monitor) {
            context.open(Settings.ACTION_DISPLAY_SETTINGS)
        }
        SettingsCard("Sound", "Volume levels, do not disturb", Icons.Outlined.VolumeUp) {
            context.open(Settings.ACTION_SOUND_SETTINGS)
        }
        SettingsCard("Notifications", "Alerts from apps and system", Icons.Outlined.Notifications) {
            context.open(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        }
        SettingsCard("Storage", "Drives, configuration", Icons.Outlined.Storage) {
            context.open(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
        }
        SettingsCard(
            "About",
            "${Build.MANUFACTURER} ${Build.MODEL} · Android ${Build.VERSION.RELEASE}",
            Icons.Outlined.Info,
        ) {
            context.open(Settings.ACTION_DEVICE_INFO_SETTINGS)
        }
    }
}

@Composable
private fun PersonalizationPage(
    selectedPreset: ThemePreset,
    onSelectPreset: (ThemePreset) -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Column {
        PageTitle("Personalization")
        Spacer(Modifier.height(12.dp))
        Text(
            "Select a theme — it applies to the taskbar, Start menu, flyouts, and windows",
            color = theme.textSecondary, fontSize = 12.sp,
        )
        Spacer(Modifier.height(8.dp))
        LauncherThemes.all.chunked(2).forEach { rowThemes ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowThemes.forEach { preset ->
                    ModeCard(
                        label = preset.displayName,
                        selected = selectedPreset == preset.preset,
                        previewTop = if (preset.isDark) Color(0xFF071A33) else Color(0xFF9CC7EE),
                        previewSurface = preset.windowSurface,
                        previewAccent = preset.accent,
                        onClick = { onSelectPreset(preset.preset) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowThemes.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text("Accent color", color = theme.textSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(26.dp).clip(CircleShape)
                    .background(theme.accent)
                    .border(2.dp, theme.text.copy(alpha = 0.4f), CircleShape),
            )
            Spacer(Modifier.width(10.dp))
            Text("Set by the theme", color = theme.text, fontSize = 12.sp)
        }

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val wallpaperSet by AppSettings.wallpaperImageFlow(context).collectAsState(initial = "")
        val orbSet by AppSettings.orbImageFlow(context).collectAsState(initial = "")

        val wallpaperPicker = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri ->
            uri?.let {
                scope.launch(Dispatchers.IO) {
                    ImageStore.importImage(context, it, "wallpaper")?.let { path ->
                        AppSettings.setWallpaperImage(context, path)
                    }
                }
            }
        }
        val orbPicker = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri ->
            uri?.let {
                scope.launch(Dispatchers.IO) {
                    ImageStore.importImage(context, it, "orb")?.let { path ->
                        AppSettings.setOrbImage(context, path)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Wallpaper", color = theme.textSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AccentButton("Choose from Photos", onClick = { wallpaperPicker.launch("image/*") })
            if (wallpaperSet.isNotEmpty()) {
                SubtleButton("Reset to Bloom", onClick = {
                    scope.launch { AppSettings.setWallpaperImage(context, "") }
                })
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Start button", color = theme.textSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AccentButton("Choose from Photos", onClick = { orbPicker.launch("image/*") })
            if (orbSet.isNotEmpty()) {
                SubtleButton("Reset to logo", onClick = {
                    scope.launch { AppSettings.setOrbImage(context, "") }
                })
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ModeCard(
    label: String,
    selected: Boolean,
    previewTop: Color,
    previewSurface: Color,
    previewAccent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current
    Column(
        modifier
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) theme.accent else theme.stroke,
                shape = RoundedCornerShape(6.dp),
            )
            .clickable { onClick() },
    ) {
        // Mini desktop preview: wallpaper + window + accent chip
        Box(
            Modifier.fillMaxWidth().height(64.dp)
                .background(Brush.verticalGradient(listOf(previewTop, previewTop.copy(alpha = 0.7f)))),
        ) {
            Box(
                Modifier.align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .size(width = 44.dp, height = 26.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(previewSurface),
            ) {
                Box(
                    Modifier.align(Alignment.BottomStart)
                        .padding(3.dp)
                        .size(width = 14.dp, height = 5.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(previewAccent),
                )
            }
        }
        Row(
            Modifier.fillMaxWidth().background(theme.card).padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, color = theme.text, fontSize = 12.sp)
            Spacer(Modifier.weight(1f))
            if (selected) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(theme.accent))
            }
        }
    }
}

@Composable
private fun AppsPage() {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PageTitle("Apps")
        Spacer(Modifier.height(4.dp))
        SettingsCard("Installed apps", "Uninstall, defaults, storage", Icons.Outlined.Apps) {
            context.open(Settings.ACTION_APPLICATION_SETTINGS)
        }
        SettingsCard("Default apps", "Choose the default home app", Icons.Outlined.Home) {
            context.open(Settings.ACTION_HOME_SETTINGS)
        }
    }
}

private fun android.content.Context.open(action: String) {
    runCatching { startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
}
