package com.serhii.appblocker.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TimerUtilsTest {

    @Test
    fun `formatMillis should format correctly`() {
        assertEquals("00:00:00", formatMillis(0))
        assertEquals("00:00:01", formatMillis(1000))
        assertEquals("00:01:00", formatMillis(60000))
        assertEquals("01:00:00", formatMillis(3600000))
        assertEquals("01:01:01", formatMillis(3661000))
    }

    @Test
    fun `millisToTimeString should return null for null input`() {
        assertNull(null.millisToTimeString())
    }

    @Test
    fun `millisToTimeString should format hours and minutes correctly`() {
        assertEquals("1h 30m", (90 * 60 * 1000L).millisToTimeString())
        assertEquals("1h", (60 * 60 * 1000L).millisToTimeString())
        assertEquals("30m", (30 * 60 * 1000L).millisToTimeString())
        assertEquals("0m", 0L.millisToTimeString())
    }
}
