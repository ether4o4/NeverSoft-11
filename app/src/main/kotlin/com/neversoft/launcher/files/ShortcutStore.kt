package com.neversoft.launcher.files

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Persists launcher-only app references next to a physical folder.
 *
 * Android app shortcuts are not files and must never be copied as APKs. The
 * small metadata file lets File Explorer render and launch those references
 * while keeping desktop and Explorer backed by the same physical directory.
 */
object ShortcutStore {
    const val FILE_NAME = ".neversoft_shortcuts.json"

    data class Entry(
        val id: String,
        val label: String,
        val packageName: String,
        val iconPath: String = "",
    )

    fun list(directory: File): List<Entry> = read(directory).mapNotNull { json ->
        runCatching {
            Entry(
                id = json.getString("id"),
                label = json.getString("label"),
                packageName = json.getString("packageName"),
                iconPath = json.optString("iconPath", ""),
            )
        }.getOrNull()
    }

    @Synchronized
    fun add(directory: File, entry: Entry): Boolean {
        if (!directory.isDirectory && !directory.mkdirs()) return false
        val current = list(directory).toMutableList()
        if (current.any { it.id == entry.id || it.packageName == entry.packageName }) return false
        current += entry
        return write(directory, current)
    }

    @Synchronized
    fun remove(directory: File, id: String): Boolean {
        val current = list(directory)
        val updated = current.filterNot { it.id == id }
        if (updated.size == current.size) return false
        return write(directory, updated)
    }

    private fun read(directory: File): List<JSONObject> = runCatching {
        val file = File(directory, FILE_NAME)
        if (!file.isFile) {
            emptyList()
        } else {
            val array = JSONArray(file.readText())
            List(array.length()) { array.getJSONObject(it) }
        }
    }.getOrDefault(emptyList())

    private fun write(directory: File, entries: List<Entry>): Boolean = runCatching {
        val target = File(directory, FILE_NAME)
        val temp = File(directory, "$FILE_NAME.tmp")
        val json = JSONArray().apply {
            entries.forEach { entry ->
                put(
                    JSONObject()
                        .put("id", entry.id)
                        .put("label", entry.label)
                        .put("packageName", entry.packageName)
                        .put("iconPath", entry.iconPath),
                )
            }
        }
        temp.writeText(json.toString())
        runCatching {
            Files.move(
                temp.toPath(),
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
            )
        }.getOrElse {
            if (target.exists() && !target.delete()) return false
            if (!temp.renameTo(target)) return false
        }
        true
    }.getOrDefault(false)
}
