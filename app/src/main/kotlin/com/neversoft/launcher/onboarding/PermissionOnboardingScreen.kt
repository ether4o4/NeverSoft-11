package com.neversoft.launcher.onboarding

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.neversoft.launcher.theme.BloomWallpaper
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.ui.AccentButton
import com.neversoft.launcher.ui.StartLogo
import com.neversoft.launcher.ui.SubtleButton

// Windows 11 OOBE-style setup: bloom wallpaper, centered card.
@Composable
fun PermissionOnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val theme = LocalLauncherTheme.current

    var runtimeGranted by remember { mutableStateOf(hasRuntimePermissions(context)) }
    var allFilesGranted by remember { mutableStateOf(hasAllFilesPermission()) }
    var usageStatsGranted by remember { mutableStateOf(hasUsageStatsPermission(context)) }

    val runtimePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        runtimeGranted = hasRuntimePermissions(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                runtimeGranted = hasRuntimePermissions(context)
                allFilesGranted = hasAllFilesPermission()
                usageStatsGranted = hasUsageStatsPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier.fillMaxSize()) {
        BloomWallpaper(isDark = theme.isDark, modifier = Modifier.fillMaxSize())

        Column(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(theme.windowSurface)
                .border(1.dp, theme.stroke, RoundedCornerShape(8.dp))
                .verticalScroll(rememberScrollState())
                .padding(28.dp),
        ) {
            StartLogo(40.dp)
            Spacer(Modifier.height(18.dp))
            Text(
                "Let's set up your device",
                color = theme.text, fontSize = 24.sp, fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "NeverSoft 11 needs a few permissions so the desktop, search, and File Explorer work.",
                color = theme.textSecondary, fontSize = 13.sp, lineHeight = 18.sp,
            )
            Spacer(Modifier.height(20.dp))

            PermissionRow(
                label = "Contacts and media",
                description = "Used by search and the photo library",
                granted = runtimeGranted,
                actionLabel = "Grant",
            ) {
                runtimePermissionLauncher.launch(runtimePermissionsForDevice())
            }
            Spacer(Modifier.height(10.dp))
            PermissionRow(
                label = "All-files access",
                description = "Lets File Explorer browse your storage",
                granted = allFilesGranted,
                actionLabel = "Open settings",
            ) {
                runCatching {
                    context.startActivity(
                        Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            PermissionRow(
                label = "Usage access",
                description = "Improves app suggestions",
                granted = usageStatsGranted,
                actionLabel = "Open settings",
            ) {
                runCatching {
                    context.startActivity(
                        Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                AccentButton("Continue", onClick = onComplete)
            }
        }
    }
}

@Composable
private fun PermissionRow(
    label: String,
    description: String,
    granted: Boolean,
    actionLabel: String,
    onGrant: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(theme.card)
            .border(1.dp, theme.stroke, RoundedCornerShape(6.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(22.dp).clip(CircleShape)
                .background(if (granted) theme.accent else theme.hover),
            contentAlignment = Alignment.Center,
        ) {
            if (granted) {
                Icon(Icons.Outlined.Check, null, Modifier.size(13.dp), tint = theme.accentText)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(label, color = theme.text, fontSize = 13.sp)
            Text(description, color = theme.textSecondary, fontSize = 11.sp)
        }
        if (!granted) {
            Spacer(Modifier.width(8.dp))
            SubtleButton(actionLabel, onClick = onGrant)
        }
    }
}

private fun runtimePermissionsForDevice(): Array<String> {
    val permissions = mutableListOf(Manifest.permission.READ_CONTACTS)
    if (Build.VERSION.SDK_INT >= 33) {
        permissions += Manifest.permission.READ_MEDIA_IMAGES
        permissions += Manifest.permission.READ_MEDIA_VIDEO
        permissions += Manifest.permission.READ_MEDIA_AUDIO
    } else {
        permissions += Manifest.permission.READ_EXTERNAL_STORAGE
    }
    return permissions.toTypedArray()
}

private fun hasRuntimePermissions(context: Context): Boolean {
    return runtimePermissionsForDevice().all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}

private fun hasAllFilesPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        true
    }
}

private fun hasUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName,
    )
    return mode == AppOpsManager.MODE_ALLOWED
}
