package com.neversoft.launcher.controlpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neversoft.launcher.theme.LauncherThemes
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.theme.ThemePreset

@Composable
fun ControlPanelWindow(
    selectedPreset: ThemePreset,
    onSelectPreset: (ThemePreset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalLauncherTheme.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.surfaceColor.copy(alpha = 0.25f))
            .padding(16.dp),
    ) {
        Text(
            text = "Control Panel",
            style = MaterialTheme.typography.titleLarge,
            color = theme.onSurface,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Launcher appearance presets",
            style = MaterialTheme.typography.bodySmall,
            color = theme.onSurface.copy(alpha = 0.6f),
        )
        Spacer(Modifier.height(16.dp))

        LauncherThemes.all.chunked(2).forEach { rowThemes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowThemes.forEach { launcherTheme ->
                    val isSelected = launcherTheme.preset == selectedPreset
                    ThemePresetCard(
                        title = launcherTheme.displayName,
                        swatch = launcherTheme.accentColor,
                        background = launcherTheme.surfaceColor,
                        isSelected = isSelected,
                        onClick = { onSelectPreset(launcherTheme.preset) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowThemes.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ThemePresetCard(
    title: String,
    swatch: Color,
    background: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) swatch else swatch.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp),
            )
            .background(background.copy(alpha = 0.2f))
            .clickable { onClick() }
            .padding(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(swatch),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            color = if (isSelected) swatch else LocalLauncherTheme.current.onSurface,
            maxLines = 1,
        )
    }
}
