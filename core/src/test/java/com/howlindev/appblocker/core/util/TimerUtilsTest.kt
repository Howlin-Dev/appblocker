package com.howlindev.appblocker.core.util

import android.content.Context
import com.howlindev.appblocker.core.R
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TimerUtilsTest {

    private val context = mockk<Context>()

    @Test
    fun `millisToTimerString should format correctly`() {
        every { context.getString(R.string.duration_hours_minutes_seconds, any(), any(), any()) } returns "01:01:01"
        every { context.getString(R.string.duration_minutes_seconds, any(), any()) } returns "01:00"

        assertEquals("01:01:01", 3661000L.millisToTimerString(context))
        assertEquals("01:00", 60000L.millisToTimerString(context))
    }

    @Test
    fun `millisToTimeString should return null for null input`() {
        assertNull(null.millisToTimeString(context))
    }

    @Test
    fun `millisToTimeString should format hours and minutes correctly`() {
        every { context.getString(R.string.duration_hours_minutes, any(), any()) } returns "1h 30m"
        every { context.getString(R.string.duration_hours, any()) } returns "1h"
        every { context.getString(R.string.duration_minutes, any()) } returns "30m"

        assertEquals("1h 30m", (90 * 60 * 1000L).millisToTimeString(context))
        assertEquals("1h", (60 * 60 * 1000L).millisToTimeString(context))
        assertEquals("30m", (30 * 60 * 1000L).millisToTimeString(context))
    }
}
