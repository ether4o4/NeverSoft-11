package com.neversoft.launcher.fileexplorer

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.util.Locale

/**
 * Filesystem operations for the explorer, following Ghost Key's fs layer
 * rules: one level, on demand — no recursive scanning or indexing. Deletes
 * are soft by default: entries move to the on-device recycle bin folder and
 * are only destroyed when deleted from inside the bin itself.
 */
object FileOps {

    const val RECYCLE_DIR_NAME = ".\$recycle_bin\$"

    fun recycleBinDir(): File = File(Environment.getExternalStorageDirectory(), RECYCLE_DIR_NAME)

    fun isInRecycleBin(file: File): Boolean =
        file.absolutePath.startsWith(recycleBinDir().absolutePath)

    /** Soft delete: move into the recycle bin (falls back to copy+delete across volumes). */
    fun moveToRecycleBin(file: File): Boolean {
        val bin = recycleBinDir()
        if (!bin.exists() && !bin.mkdirs()) return false
        return moveInto(file, bin)
    }

    fun deletePermanently(file: File): Boolean = file.deleteRecursively()

    fun copyInto(src: File, destDir: File): Boolean {
        if (isRecursiveInto(src, destDir)) return false
        val dest = uniqueChild(destDir, src.name)
        return runCatching { src.copyRecursively(dest, overwrite = false) }.getOrDefault(false)
    }

    fun moveInto(src: File, destDir: File): Boolean {
        if (isRecursiveInto(src, destDir)) return false
        if (src.parentFile?.absolutePath == destDir.absolutePath) return true
        val dest = uniqueChild(destDir, src.name)
        if (src.renameTo(dest)) return true
        // Cross-volume move: copy then delete the original.
        val copied = runCatching { src.copyRecursively(dest, overwrite = false) }.getOrDefault(false)
        if (!copied) return false
        return src.deleteRecursively()
    }

    fun rename(file: File, newName: String): Boolean {
        val trimmed = newName.trim()
        if (trimmed.isEmpty() || trimmed.contains('/')) return false
        val dest = File(file.parentFile ?: return false, trimmed)
        if (dest.exists()) return false
        return file.renameTo(dest)
    }

    fun newFolder(parent: File, base: String = "New folder"): File? {
        val dir = uniqueChild(parent, base)
        return if (dir.mkdirs()) dir else null
    }

    /** "name", "name (1)", "name (2)"… like Windows does on collision. */
    fun uniqueChild(dir: File, name: String): File {
        var candidate = File(dir, name)
        if (!candidate.exists()) return candidate
        val dot = name.lastIndexOf('.')
        val stem = if (dot > 0) name.substring(0, dot) else name
        val ext = if (dot > 0) name.substring(dot) else ""
        var i = 1
        while (candidate.exists()) {
            candidate = File(dir, "$stem ($i)$ext")
            i++
        }
        return candidate
    }

    fun openFile(context: Context, file: File) {
        val ext = file.extension.lowercase(Locale.ROOT)
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: "*/*"
        runCatching {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            context.startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setDataAndType(uri, mime)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }

    /** Guard against copying/moving a folder into itself or a descendant. */
    private fun isRecursiveInto(src: File, destDir: File): Boolean =
        src.isDirectory && (destDir.absolutePath + "/").startsWith(src.absolutePath + "/")
}

/** Cut/copy clipboard shared by every explorer window (Ghost Key's cross-pane transfer). */
object FileClipboard {
    var paths: List<String> = emptyList()
        private set
    var isCut: Boolean = false
        private set

    val isEmpty: Boolean get() = paths.isEmpty()

    fun set(files: List<File>, cut: Boolean) {
        paths = files.map { it.absolutePath }
        isCut = cut
    }

    fun clear() {
        paths = emptyList()
        isCut = false
    }
}
