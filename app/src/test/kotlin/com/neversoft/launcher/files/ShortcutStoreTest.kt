package com.neversoft.launcher.files

import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class ShortcutStoreTest {
    @Test
    fun addListAndRemovePersistedShortcut() {
        val directory = Files.createTempDirectory("shortcut-store").toFile()
        try {
            val entry = ShortcutStore.Entry(
                id = "app_snapchat",
                label = "Snapchat",
                packageName = "com.snapchat.android",
            )

            assertTrue(ShortcutStore.add(directory, entry))
            assertEquals(listOf(entry), ShortcutStore.list(directory))

            // The store is file-backed; reading again must not depend on an in-memory cache.
            assertEquals(listOf(entry), ShortcutStore.list(directory))
            assertTrue(ShortcutStore.remove(directory, entry.id))
            assertTrue(ShortcutStore.list(directory).isEmpty())
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun duplicateIdOrPackageIsRejected() {
        val directory = Files.createTempDirectory("shortcut-store-duplicates").toFile()
        try {
            val first = ShortcutStore.Entry("one", "One", "com.example.one")
            assertTrue(ShortcutStore.add(directory, first))
            assertFalse(ShortcutStore.add(directory, first.copy(label = "Renamed")))
            assertFalse(
                ShortcutStore.add(
                    directory,
                    ShortcutStore.Entry("two", "Two", first.packageName),
                ),
            )
            assertEquals(listOf(first), ShortcutStore.list(directory))
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun malformedMetadataIsIgnoredWithoutBreakingValidEntries() {
        val directory = Files.createTempDirectory("shortcut-store-malformed").toFile()
        try {
            java.io.File(directory, ShortcutStore.FILE_NAME).writeText(
                """[{"id":"ok","label":"Okay","packageName":"com.example.ok"},{"id":"bad"}]""",
            )
            assertEquals(
                listOf(ShortcutStore.Entry("ok", "Okay", "com.example.ok")),
                ShortcutStore.list(directory),
            )
        } finally {
            directory.deleteRecursively()
        }
    }
}
