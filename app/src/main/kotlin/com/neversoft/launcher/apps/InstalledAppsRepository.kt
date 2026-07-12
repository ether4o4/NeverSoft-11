package com.neversoft.launcher.apps

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.neversoft.launcher.data.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

data class InstalledApp(
    val label: String,
    val packageName: String,
    val icon: ImageBitmap?,
)

object InstalledAppsRepository {

    suspend fun loadApps(context: Context): List<InstalledApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val iconPack = AppSettings.iconPackFlow(context).first()
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        pm.queryIntentActivities(launcherIntent, 0)
            .filter { it.activityInfo.packageName != context.packageName }
            .map { resolveInfo ->
                InstalledApp(
                    label = resolveInfo.loadLabel(pm).toString(),
                    packageName = resolveInfo.activityInfo.packageName,
                    icon = runCatching {
                        IconPacks.getIcon(
                            context, iconPack,
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name,
                        )?.asImageBitmap()
                            ?: resolveInfo.loadIcon(pm).toBitmap(ICON_SIZE_PX, ICON_SIZE_PX).asImageBitmap()
                    }.getOrNull(),
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }

    // Icon for a single package, honoring the active icon pack
    fun loadIcon(context: Context, iconPack: String, packageName: String): Bitmap? {
        val pm = context.packageManager
        return runCatching {
            IconPacks.getIcon(
                context, iconPack, packageName,
                pm.getLaunchIntentForPackage(packageName)?.component?.className,
            ) ?: pm.getApplicationIcon(packageName).toBitmap(ICON_SIZE_PX, ICON_SIZE_PX)
        }.getOrNull()
    }

    fun launch(context: Context, packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }

    private const val ICON_SIZE_PX = 96
}
