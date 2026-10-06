package com.neversoft.launcher.files

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

// Copies user-picked photos into app storage so they survive reboots and
// permission revocations, and decodes them at a sane size.
object ImageStore {
    // Validate before publishing; retain prior files because other shortcuts
    // or persisted settings may still reference them. No URI grant is needed
    // after this bounded copy completes in app-private storage.
    fun importImage(context: Context, uri: Uri, prefix: String): String? = runCatching {
        val dir = File(context.filesDir, "images").apply { mkdirs() }
        val safePrefix = prefix.replace(Regex("[^a-zA-Z0-9._-]"), "_").take(100)
        val staging = File.createTempFile("import-", ".tmp", dir)
        val target = File(dir, "$safePrefix-${java.util.UUID.randomUUID()}.png")
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                staging.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var total = 0L
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        total += count
                        require(total <= 32L * 1024 * 1024) { "Image exceeds 32 MB" }
                        output.write(buffer, 0, count)
                    }
                }
            } ?: error("Image is unavailable")
            require(hasImageHeader(staging)) { "Unsupported image" }
            val bitmap = decodeSampled(staging.absolutePath, if (prefix == "wallpaper") 2048 else 512)
                ?: error("Image could not be decoded")
            try {
                target.outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
            } finally { bitmap.recycle() }
            target.absolutePath
        } catch (e: Exception) {
            target.delete()
            throw e
        } finally { staging.delete() }
    }.onFailure {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            android.widget.Toast.makeText(context.applicationContext,
                "Could not import image. Choose a readable image under 32 MB; your previous icon is kept.",
                android.widget.Toast.LENGTH_LONG).show()
        }
    }.getOrNull()

    private fun hasImageHeader(file: File): Boolean {
        val header = ByteArray(16)
        val count = file.inputStream().use { it.read(header) }
        if (count < 8) return false
        fun ascii(start: Int, end: Int) = String(header, start, end - start, Charsets.US_ASCII)
        return (header[0] == 0x89.toByte() && ascii(1, 4) == "PNG") ||
            (header[0] == 0xff.toByte() && header[1] == 0xd8.toByte() && header[2] == 0xff.toByte()) ||
            ascii(0, 4) == "GIF8" || ascii(0, 2) == "BM" ||
            (count >= 12 && ascii(0, 4) == "RIFF" && ascii(8, 12) == "WEBP") ||
            (count >= 12 && ascii(4, 8) == "ftyp")
    }

    fun decodeSampled(path: String, targetPx: Int): Bitmap? = runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0 ||
            bounds.outWidth.toLong() * bounds.outHeight > 100_000_000L) return null
        val limit = targetPx.coerceIn(32, 2048)
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / sample > limit
        ) {
            sample *= 2
        }
        BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample })
    }.getOrNull()

    /**
     * Make a flat, solid background transparent so a custom Start orb (or any
     * shaped logo) doesn't show a white/colored box behind it.
     *
     * Flood-fills inward from the four corners, clearing every pixel connected
     * to an edge whose color is close to the sampled background color. Only the
     * background *touching the border* is removed, so white or light detail
     * inside the logo is preserved, and it works regardless of the logo's
     * shape. If the corners are already transparent (a real cut-out PNG) or the
     * corners disagree (a full-bleed photo), nothing is changed.
     */
    fun removeFlatBackground(src: Bitmap, tolerance: Int = 40): Bitmap {
        val w = src.width
        val h = src.height
        if (w < 2 || h < 2) return src

        val corners = intArrayOf(
            src.getPixel(0, 0),
            src.getPixel(w - 1, 0),
            src.getPixel(0, h - 1),
            src.getPixel(w - 1, h - 1),
        )
        // Already has a cut-out (any corner transparent) -> leave it alone
        if (corners.any { android.graphics.Color.alpha(it) < 24 }) return src
        // Corners must agree on one background color, else it's a photo
        val bg = corners[0]
        if (corners.any { colorDistance(it, bg) > tolerance }) return src

        val out = src.copy(Bitmap.Config.ARGB_8888, true)
        val pixels = IntArray(w * h)
        out.getPixels(pixels, 0, w, 0, 0, w, h)

        val visited = BooleanArray(w * h)
        val stack = ArrayDeque<Int>()
        fun seed(x: Int, y: Int) {
            val i = y * w + x
            if (!visited[i]) { visited[i] = true; stack.addLast(i) }
        }
        for (x in 0 until w) { seed(x, 0); seed(x, h - 1) }
        for (y in 0 until h) { seed(0, y); seed(w - 1, y) }

        while (stack.isNotEmpty()) {
            val i = stack.removeLast()
            if (colorDistance(pixels[i], bg) > tolerance) continue
            pixels[i] = pixels[i] and 0x00FFFFFF // clear alpha, keep RGB
            val x = i % w
            val y = i / w
            if (x > 0) seed(x - 1, y)
            if (x < w - 1) seed(x + 1, y)
            if (y > 0) seed(x, y - 1)
            if (y < h - 1) seed(x, y + 1)
        }
        out.setPixels(pixels, 0, w, 0, 0, w, h)
        return out
    }

    private fun colorDistance(a: Int, b: Int): Int {
        val dr = android.graphics.Color.red(a) - android.graphics.Color.red(b)
        val dg = android.graphics.Color.green(a) - android.graphics.Color.green(b)
        val db = android.graphics.Color.blue(a) - android.graphics.Color.blue(b)
        // Chebyshev distance keeps the threshold intuitive per-channel
        return maxOf(kotlin.math.abs(dr), kotlin.math.abs(dg), kotlin.math.abs(db))
    }
}
