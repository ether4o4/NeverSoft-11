package com.neversoft.launcher.apps

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.neversoft.launcher.data.AppSettings
import com.neversoft.launcher.files.ImageStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

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
    @Volatile private var cachedOverrides: String? = null
    @Volatile private var cachedAt: Long = 0
    private val iconMemo = HashMap<String, ImageBitmap?>()
    private const val CACHE_TTL_MS = 30_000L

    suspend fun loadApps(context: Context): List<InstalledApp> = withContext(Dispatchers.IO) {
        val iconPack = AppSettings.iconPackFlow(context).first()
        val overridesJson = AppSettings.appIconOverridesFlow(context).first()
        val overrides = parseOverrides(overridesJson)
        cachedApps?.let { cached ->
            if (cachedPack == iconPack && cachedOverrides == overridesJson &&
                System.currentTimeMillis() - cachedAt < CACHE_TTL_MS
            ) {
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
                val override = overrides[pkg]?.takeIf { File(it).exists() }
                val memoKey = if (override != null) "override|$pkg|$override" else "$iconPack|$pkg|$activity"
                val icon = synchronized(iconMemo) { iconMemo[memoKey] } ?: runCatching {
                    if (override != null) {
                        ImageStore.decodeSampled(override, ICON_SIZE_PX)?.asImageBitmap()
                    } else {
                        IconPacks.getIcon(context, iconPack, pkg, activity)?.asImageBitmap()
                            ?: resolveInfo.loadIcon(pm).toBitmap(ICON_SIZE_PX, ICON_SIZE_PX).asImageBitmap()
                    }
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
        cachedOverrides = overridesJson
        cachedAt = System.currentTimeMillis()
        apps
    }

    // Icon for a single package: custom override first, then the active icon
    // pack, then the system icon.
    fun loadIcon(
        context: Context,
        iconPack: String,
        packageName: String,
        overridePath: String? = null,
    ): Bitmap? {
        val pm = context.packageManager
        return runCatching {
            overridePath?.takeIf { File(it).exists() }
                ?.let { ImageStore.decodeSampled(it, ICON_SIZE_PX) }
                ?: IconPacks.getIcon(
                    context, iconPack, packageName,
                    pm.getLaunchIntentForPackage(packageName)?.component?.className,
                )
                ?: pm.getApplicationIcon(packageName).toBitmap(ICON_SIZE_PX, ICON_SIZE_PX)
        }.getOrNull()
    }

    private fun parseOverrides(json: String): Map<String, String> = runCatching {
        val obj = org.json.JSONObject(json)
        buildMap {
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val v = obj.optString(k)
                if (v.isNotEmpty()) put(k, v)
            }
        }
    }.getOrDefault(emptyMap())

    fun launch(context: Context, packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }

    private const val ICON_SIZE_PX = 168
}
