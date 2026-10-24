package com.neversoft.launcher.shell

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ShellScreenTest {
    @Test
    fun parseSizeRejectsMalformedAndNonPositiveValues() {
        assertNull(parseSize(""))
        assertNull(parseSize("320"))
        assertNull(parseSize("0,420"))
        assertNull(parseSize("320,-1"))
        assertNull(parseSize("NaN,420"))
        assertNull(parseSize("Infinity,420"))
    }

    @Test
    fun parseSizeAcceptsPositiveFiniteDimensions() {
        val size = parseSize("320.5,420")!!
        assertEquals(320.5f, size.width.value, 0.001f)
        assertEquals(420f, size.height.value, 0.001f)
    }
}
