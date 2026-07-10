package com.neversoft.launcher.apps

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.neversoft.launcher.data.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class InstalledApp(
    val label: String,
    val packageName: String,
    val icon: ImageBitmap?,
    val category: Int = ApplicationInfo.CATEGORY_UNDEFINED,
)

/** Start-menu buckets, Windows 11 style category folders. */
enum class AppCategory(val title: String) {
    CONNECT("Connect"),
    CREATE("Create"),
    MEDIA("Media"),
    GAMES("Games"),
    DISCOVER("Discover"),
    UTILITIES("Utilities");

    companion object {
        fun of(app: InstalledApp): AppCategory = when (app.category) {
            ApplicationInfo.CATEGORY_SOCIAL -> CONNECT
            ApplicationInfo.CATEGORY_PRODUCTIVITY -> CREATE
            ApplicationInfo.CATEGORY_AUDIO,
            ApplicationInfo.CATEGORY_VIDEO,
            ApplicationInfo.CATEGORY_IMAGE -> MEDIA
            ApplicationInfo.CATEGORY_GAME -> GAMES
            ApplicationInfo.CATEGORY_NEWS,
            ApplicationInfo.CATEGORY_MAPS -> DISCOVER
            else -> UTILITIES
        }
    }
}

object InstalledAppsRepository {

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun loadApps(context: Context): List<InstalledApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        pm.queryIntentActivities(launcherIntent, 0)
            .filter { it.activityInfo.packageName != context.packageName }
            .map { resolveInfo ->
                InstalledApp(
                    label = resolveInfo.loadLabel(pm).toString(),
                    packageName = resolveInfo.activityInfo.packageName,
                    icon = runCatching {
                        resolveInfo.loadIcon(pm).toBitmap(ICON_SIZE_PX, ICON_SIZE_PX).asImageBitmap()
                    }.getOrNull(),
                    category = resolveInfo.activityInfo.applicationInfo.category,
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }

    fun launch(context: Context, packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
        val appContext = context.applicationContext
        ioScope.launch { AppSettings.recordRecentApp(appContext, packageName) }
    }

    private const val ICON_SIZE_PX = 96
}
