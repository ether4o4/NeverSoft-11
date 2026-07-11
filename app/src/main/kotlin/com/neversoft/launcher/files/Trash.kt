package com.neversoft.launcher.files

import android.os.Environment
import java.io.File

// The launcher's Recycle Bin: deleted files are moved here instead of
// being destroyed, mirroring the Windows recycle flow.
object Trash {
    fun dir(): File =
        File(Environment.getExternalStorageDirectory(), ".neversoft_trash").apply { mkdirs() }

    fun isTrash(path: File): Boolean = path.absolutePath == dir().absolutePath

    // Move a file into the bin, de-duplicating names
    fun moveToTrash(file: File): Boolean {
        val target = uniqueName(dir(), file.name)
        return runCatching { file.renameTo(target) }.getOrDefault(false)
    }

    fun uniqueName(parent: File, name: String): File {
        var candidate = File(parent, name)
        var counter = 1
        val base = name.substringBeforeLast('.', name)
        val ext = name.substringAfterLast('.', "")
        while (candidate.exists()) {
            val suffixed = if (ext.isEmpty() || base == name) "$name ($counter)"
            else "$base ($counter).$ext"
            candidate = File(parent, suffixed)
            counter++
        }
        return candidate
    }
}
