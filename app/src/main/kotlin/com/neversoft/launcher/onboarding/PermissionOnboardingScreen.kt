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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.neversoft.launcher.theme.LocalLauncherTheme

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

    val canContinue = runtimeGranted && allFilesGranted && usageStatsGranted

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.surfaceBrush())
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "NeverSoft 11 Setup",
                style = MaterialTheme.typography.headlineSmall,
                color = theme.onSurface,
            )
            Text(
                text = "Grant launcher permissions on first run before entering the desktop shell.",
                style = MaterialTheme.typography.bodyMedium,
                color = theme.onSurface.copy(alpha = 0.75f),
            )
            Spacer(Modifier.height(4.dp))

            PermissionRow(label = "Contacts + media", granted = runtimeGranted)
            PermissionRow(label = "Manage all files", granted = allFilesGranted)
            PermissionRow(label = "Usage access", granted = usageStatsGranted)

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    runtimePermissionLauncher.launch(runtimePermissionsForDevice())
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Grant contacts/media permissions")
            }

            OutlinedButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Open Manage All Files")
            }

            OutlinedButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Open Usage Access")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onComplete,
                enabled = canContinue,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Continue to NeverSoft 11")
            }
        }
    }
}

@Composable
private fun PermissionRow(label: String, granted: Boolean) {
    Text(
        text = if (granted) "✓ $label" else "• $label",
        style = MaterialTheme.typography.bodyMedium,
    )
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
