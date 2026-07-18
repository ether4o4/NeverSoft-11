package com.neversoft.launcher.startmenu

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Environment
import com.neversoft.launcher.apps.InstalledApp
import com.neversoft.launcher.apps.InstalledAppsRepository
import com.neversoft.launcher.data.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File

data class RecentFile(val file: File, val name: String)

// Data sources for the Start-menu recent rows. All resolved off the main thread.
object StartData {

    // Most-recently-used apps (needs Usage Access, which onboarding requests).
    suspend fun recentApps(context: Context, allApps: List<InstalledApp>, limit: Int): List<InstalledApp> =
        withContext(Dispatchers.IO) {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                ?: return@withContext emptyList()
            val now = 0L.let { System.currentTimeMillis() }
            val start = now - 1000L * 60 * 60 * 24 * 14 // last 14 days
            val stats = runCatching {
                usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, now)
            }.getOrNull().orEmpty()
            val byPkg = allApps.associateBy { it.packageName }
            stats
                .filter { it.lastTimeUsed > 0 && it.packageName != context.packageName }
                .sortedByDescending { it.lastTimeUsed }
                .mapNotNull { byPkg[it.packageName] }
                .distinctBy { it.packageName }
                .take(limit)
        }

    // Apps by most-recent install time.
    suspend fun recentlyInstalledApps(context: Context, allApps: List<InstalledApp>, limit: Int): List<InstalledApp> =
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            allApps
                .mapNotNull { app ->
                    runCatching { pm.getPackageInfo(app.packageName, 0).firstInstallTime }
                        .getOrNull()?.let { app to it }
                }
                .sortedByDescending { it.second }
                .map { it.first }
                .take(limit)
        }

    // Files most-recently created/modified across common directories.
    suspend fun recentlyAddedFiles(limit: Int): List<RecentFile> = withContext(Dispatchers.IO) {
        scanDirs()
            .sortedByDescending { it.lastModified() }
            .take(limit)
            .map { RecentFile(it, it.name) }
    }

    // Files the user opened through the launcher (persisted), newest first.
    suspend fun recentlyOpenedFiles(context: Context, limit: Int): List<RecentFile> =
        withContext(Dispatchers.IO) {
            val raw = AppSettings.recentOpenedFilesFlow(context).first()
            runCatching {
                val arr = JSONArray(raw)
                (0 until arr.length()).map { arr.getString(it) }
            }.getOrDefault(emptyList<String>())
                .map { File(it) }
                .filter { it.exists() && it.isFile }
                .take(limit)
                .map { RecentFile(it, it.name) }
        }

    // Convenience: fully load the app list then a recent slice.
    suspend fun loadApps(context: Context) = InstalledAppsRepository.loadApps(context)

    private fun scanDirs(): List<File> {
        val dirs = listOf(
            Environment.DIRECTORY_DOWNLOADS, Environment.DIRECTORY_DOCUMENTS,
            Environment.DIRECTORY_PICTURES, Environment.DIRECTORY_DCIM,
            Environment.DIRECTORY_MOVIES, Environment.DIRECTORY_MUSIC,
        ).map { Environment.getExternalStoragePublicDirectory(it) }
        return dirs.flatMap { dir ->
            dir.listFiles()?.filter { it.isFile && !it.name.startsWith(".") } ?: emptyList()
        }
    }
}

// One Start-menu folder: a name and up to 4 app package names.
data class StartFolder(val name: String, val apps: List<String>)

object StartFolders {
    const val COUNT = 4
    const val CAPACITY = 4

    fun parse(json: String): List<StartFolder> {
        val parsed = runCatching {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                val appsArr = o.optJSONArray("apps") ?: JSONArray()
                StartFolder(
                    name = o.optString("name", "Folder ${i + 1}"),
                    apps = (0 until appsArr.length()).map { appsArr.getString(it) }.take(CAPACITY),
                )
            }
        }.getOrDefault(emptyList())
        // Always present exactly COUNT folders
        return (0 until COUNT).map { i ->
            parsed.getOrNull(i) ?: StartFolder("Folder ${i + 1}", emptyList())
        }
    }

    fun serialize(folders: List<StartFolder>): String {
        val arr = JSONArray()
        folders.forEach { f ->
            val o = org.json.JSONObject()
            o.put("name", f.name)
            o.put("apps", JSONArray(f.apps))
            arr.put(o)
        }
        return arr.toString()
    }
}
