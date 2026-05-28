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
        every { context.getString(R.string.duration_hours_minutes_seconds, 1, 1, 1) } returns "1h 1m 1s"
        every { context.getString(R.string.duration_minutes_seconds, 1, 0) } returns "1m 0s"
        every { context.getString(R.string.duration_minutes_seconds, 0, 1) } returns "0m 1s"

        assertEquals("1h 1m 1s", 3661000L.millisToTimerString(context))
        assertEquals("1m 0s", 60000L.millisToTimerString(context))
        assertEquals("0m 1s", 1000L.millisToTimerString(context))
    }

    @Test
    fun `millisToTimeString should return null for null input`() {
        assertNull(null.millisToTimeString(context))
    }

    @Test
    fun `millisToTimeString should format hours and minutes correctly`() {
        every { context.getString(R.string.duration_hours_minutes, 1, 30) } returns "1h 30m"
        every { context.getString(R.string.duration_hours, 1) } returns "1h"
        every { context.getString(R.string.duration_minutes, 30) } returns "30m"
        every { context.getString(R.string.duration_minutes, 0) } returns "0m"

        assertEquals("1h 30m", (90 * 60 * 1000L).millisToTimeString(context))
        assertEquals("1h", (60 * 60 * 1000L).millisToTimeString(context))
        assertEquals("30m", (30 * 60 * 1000L).millisToTimeString(context))
        assertEquals("0m", 0L.millisToTimeString(context))
    }
}
