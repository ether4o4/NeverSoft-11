package com.neversoft.launcher.shell

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neversoft.launcher.theme.BloomWallpaper
import com.neversoft.launcher.theme.LocalLauncherTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Windows 11 lock screen: wallpaper + big clock, tap to dismiss.
@Composable
fun LockScreen(onUnlock: () -> Unit, modifier: Modifier = Modifier) {
    val theme = LocalLauncherTheme.current
    var time by remember { mutableStateOf(formatLockTime()) }
    var date by remember { mutableStateOf(formatLockDate()) }
    LaunchedEffect(Unit) {
        while (true) {
            time = formatLockTime()
            date = formatLockDate()
            kotlinx.coroutines.delay(1_000)
        }
    }

    Box(
        modifier.fillMaxSize().clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
        ) { onUnlock() },
    ) {
        BloomWallpaper(isDark = theme.isDark, modifier = Modifier.fillMaxSize())
        Column(
            Modifier.align(Alignment.TopCenter).padding(top = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                time,
                style = TextStyle(
                    color = Color.White, fontSize = 72.sp, fontWeight = FontWeight.SemiBold,
                    shadow = Shadow(Color(0x80000000), blurRadius = 12f),
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                date,
                style = TextStyle(
                    color = Color.White, fontSize = 18.sp,
                    shadow = Shadow(Color(0x80000000), blurRadius = 8f),
                ),
            )
        }
        Text(
            "Tap to unlock",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp),
        )
    }
}

private fun formatLockTime(): String =
    SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())

private fun formatLockDate(): String =
    SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
