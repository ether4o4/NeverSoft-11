package com.neversoft.launcher.search

import android.os.Environment
import android.provider.Settings
import com.neversoft.launcher.apps.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface SearchProvider { suspend fun query(q: String): List<SearchResult> }

data class SearchResult(val id: String, val label: String, val subtitle: String = "",
    val type: ResultType = ResultType.APP, val packageName: String? = null, val filePath: String? = null)

enum class ResultType { APP, FILE, MEDIA, CONTACT, SETTING }

class LauncherSearchEngine(private val providers: List<SearchProvider>) {
    suspend fun search(query: String): List<SearchResult> {
        val q = query.trim()
        if (q.isEmpty()) return emptyList()
        return providers
            .flatMap { provider -> runCatching { provider.query(q) }.getOrDefault(emptyList()) }
            .sortedWith(
                compareBy(
                    { !it.label.startsWith(q, ignoreCase = true) },
                    { it.type.ordinal },
                    { it.label.lowercase() },
                ),
            )
    }
}

class AppSearchProvider(private val apps: () -> List<InstalledApp>) : SearchProvider {
    override suspend fun query(q: String): List<SearchResult> =
        apps()
            .filter { it.label.contains(q, ignoreCase = true) }
            .map {
                SearchResult(
                    id = it.packageName,
                    label = it.label,
                    subtitle = "App",
                    type = ResultType.APP,
                    packageName = it.packageName,
                )
            }
}

class SettingsSearchProvider : SearchProvider {
    private val entries = listOf(
        "Settings" to Settings.ACTION_SETTINGS,
        "Wi-Fi settings" to Settings.ACTION_WIFI_SETTINGS,
        "Bluetooth settings" to Settings.ACTION_BLUETOOTH_SETTINGS,
        "Display settings" to Settings.ACTION_DISPLAY_SETTINGS,
        "Sound settings" to Settings.ACTION_SOUND_SETTINGS,
        "Storage settings" to Settings.ACTION_INTERNAL_STORAGE_SETTINGS,
        "Battery saver" to Settings.ACTION_BATTERY_SAVER_SETTINGS,
        "Date and time" to Settings.ACTION_DATE_SETTINGS,
        "Apps" to Settings.ACTION_APPLICATION_SETTINGS,
        "Security" to Settings.ACTION_SECURITY_SETTINGS,
    )

    override suspend fun query(q: String): List<SearchResult> =
        entries
            .filter { it.first.contains(q, ignoreCase = true) }
            .map { SearchResult(id = it.second, label = it.first, subtitle = "Setting", type = ResultType.SETTING) }
}

class FileSearchProvider : SearchProvider {
    override suspend fun query(q: String): List<SearchResult> = withContext(Dispatchers.IO) {
        val dirs = listOf(
            Environment.DIRECTORY_DOWNLOADS,
            Environment.DIRECTORY_DOCUMENTS,
            Environment.DIRECTORY_PICTURES,
            Environment.DIRECTORY_MUSIC,
            Environment.DIRECTORY_MOVIES,
        ).map { Environment.getExternalStoragePublicDirectory(it) }

        dirs.flatMap { dir -> dir.listFiles()?.toList() ?: emptyList() }
            .filter { it.name.contains(q, ignoreCase = true) }
            .take(MAX_FILE_RESULTS)
            .map {
                SearchResult(
                    id = it.absolutePath,
                    label = it.name,
                    subtitle = it.parentFile?.name ?: "",
                    type = ResultType.FILE,
                    filePath = it.absolutePath,
                )
            }
    }

    private companion object { const val MAX_FILE_RESULTS = 20 }
}
