package com.neversoft.launcher.files

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class ImageStoreTest {
    private val context: Context get() = RuntimeEnvironment.getApplication()

    private fun photo(): File = File.createTempFile("photo-", ".png", context.cacheDir).also { file ->
        val bitmap = Bitmap.createBitmap(1200, 600, Bitmap.Config.ARGB_8888)
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }

    @Test fun importedIconSurvivesSourceDeletionAndIsBounded() {
        val source = photo()
        val path = ImageStore.importImage(context, Uri.fromFile(source), "orb")!!
        source.delete()
        val bitmap = ImageStore.decodeSampled(path, 512)!!
        assertTrue(bitmap.width <= 512)
        assertTrue(bitmap.height <= 512)
        assertTrue(File(path).canonicalPath.startsWith(File(context.filesDir, "images").canonicalPath))
    }

    @Test fun failedReplacementKeepsPreviousImage() {
        val prior = ImageStore.importImage(context, Uri.fromFile(photo()), "orb")!!
        val bad = File.createTempFile("broken-", ".png", context.cacheDir).apply { writeText("not an image") }
        assertNull(ImageStore.importImage(context, Uri.fromFile(bad), "orb"))
        assertNotNull(ImageStore.decodeSampled(prior, 128))
        assertFalse(File(context.filesDir, "images").listFiles()!!.any { it.extension == "tmp" })
    }

    @Test fun replacementCannotDeleteSharedPriorIcon() {
        val prior = ImageStore.importImage(context, Uri.fromFile(photo()), "appicon-example")!!
        val next = ImageStore.importImage(context, Uri.fromFile(photo()), "appicon-example")!!
        assertNotEquals(prior, next)
        assertTrue(File(prior).exists())
        assertNotNull(ImageStore.decodeSampled(next, 168))
    }

    @Test fun oversizedImportLeavesNoPartialFile() {
        val large = File.createTempFile("large-", ".png", context.cacheDir)
        java.io.RandomAccessFile(large, "rw").use { it.setLength(32L * 1024 * 1024 + 1) }
        assertNull(ImageStore.importImage(context, Uri.fromFile(large), "orb"))
        assertFalse(File(context.filesDir, "images").listFiles()!!.any { it.extension == "tmp" })
    }
}
