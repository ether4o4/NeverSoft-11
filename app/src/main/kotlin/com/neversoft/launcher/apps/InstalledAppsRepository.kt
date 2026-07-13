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

    // Process-wide cache: the app list is served instantly after the first
    // load (the shell warms it at startup). Icons are memoized separately so
    // a refresh only decodes icons for apps it hasn't seen.
    @Volatile private var cachedApps: List<InstalledApp>? = null
    @Volatile private var cachedPack: String? = null
    @Volatile private var cachedAt: Long = 0
    private val iconMemo = HashMap<String, ImageBitmap?>()
    private const val CACHE_TTL_MS = 30_000L

    suspend fun loadApps(context: Context): List<InstalledApp> = withContext(Dispatchers.IO) {
        val iconPack = AppSettings.iconPackFlow(context).first()
        cachedApps?.let { cached ->
            if (cachedPack == iconPack && System.currentTimeMillis() - cachedAt < CACHE_TTL_MS) {
                return@withContext cached
            }
        }
        val pm = context.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(launcherIntent, 0)
            .filter { it.activityInfo.packageName != context.packageName }
            .map { resolveInfo ->
                val pkg = resolveInfo.activityInfo.packageName
                val activity = resolveInfo.activityInfo.name
                val memoKey = "$iconPack|$pkg|$activity"
                val icon = synchronized(iconMemo) { iconMemo[memoKey] } ?: runCatching {
                    IconPacks.getIcon(context, iconPack, pkg, activity)?.asImageBitmap()
                        ?: resolveInfo.loadIcon(pm).toBitmap(ICON_SIZE_PX, ICON_SIZE_PX).asImageBitmap()
                }.getOrNull().also { loaded ->
                    synchronized(iconMemo) {
                        if (iconMemo.size > 600) iconMemo.clear()
                        iconMemo[memoKey] = loaded
                    }
                }
                InstalledApp(
                    label = resolveInfo.loadLabel(pm).toString(),
                    packageName = pkg,
                    icon = icon,
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
        cachedApps = apps
        cachedPack = iconPack
        cachedAt = System.currentTimeMillis()
        apps
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

    private const val ICON_SIZE_PX = 168
}
