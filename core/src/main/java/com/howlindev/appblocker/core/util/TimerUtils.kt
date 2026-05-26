package com.howlindev.appblocker.core.util

import android.content.Context
import com.howlindev.appblocker.core.R
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun formatMillis(millis: Long): String {
    val totalSeconds = millis / 1000

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d:%02d".format(
        hours,
        minutes,
        seconds,
    )
}

fun Long?.millisToTimeString(context: Context): String? {
    if (this == null) return null
    val duration = toDuration(DurationUnit.MILLISECONDS)
    return duration.toComponents { hours, minutes, _, _ ->
        val h = hours.toInt()
        val m = minutes
        when {
            h > 0 && m > 0 -> context.getString(R.string.duration_hours_minutes, h, m)
            h > 0 -> context.getString(R.string.duration_hours, h)
            m > 0 -> context.getString(R.string.duration_minutes, m)
            else -> context.getString(R.string.duration_minutes, 0)
        }
    }
}

