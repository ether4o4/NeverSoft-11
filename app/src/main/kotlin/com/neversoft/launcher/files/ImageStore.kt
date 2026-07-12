package com.neversoft.launcher.files

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

// Copies user-picked photos into app storage so they survive reboots and
// permission revocations, and decodes them at a sane size.
object ImageStore {
    // Returns the stored file path, or null on failure. Older imports with
    // the same prefix are deleted.
    fun importImage(context: Context, uri: Uri, prefix: String): String? = runCatching {
        val dir = File(context.filesDir, "images").apply { mkdirs() }
        val target = File(dir, "$prefix-${System.currentTimeMillis()}.img")
        context.contentResolver.openInputStream(uri)?.use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        dir.listFiles()
            ?.filter { it.name.startsWith("$prefix-") && it.absolutePath != target.absolutePath }
            ?.forEach { it.delete() }
        target.absolutePath
    }.getOrNull()

    fun decodeSampled(path: String, targetPx: Int): Bitmap? = runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sample = 1
        while (bounds.outWidth / (sample * 2) >= targetPx ||
            bounds.outHeight / (sample * 2) >= targetPx
        ) {
            sample *= 2
        }
        BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample })
    }.getOrNull()
}
