package com.howlindev.appblocker.core.util

import android.content.Context
import com.howlindev.appblocker.core.R
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Long.millisToTimerString(context: Context): String {
    val duration = toDuration(DurationUnit.MILLISECONDS)
    return duration.toComponents { h, m, s, _ ->
        when {
            h > 0 -> context.getString(R.string.duration_hours_minutes_seconds, h, m, s)
            else -> context.getString(R.string.duration_minutes_seconds, m, s)
        }
    }
}

fun Long?.millisToTimeString(context: Context): String? {
    if (this == null) return null
    val duration = toDuration(DurationUnit.MILLISECONDS)
    return duration.toComponents { h, m, _, _ ->
        when {
            h > 0 && m > 0 -> context.getString(R.string.duration_hours_minutes, h, m)
            h > 0 -> context.getString(R.string.duration_hours, h)
            m > 0 -> context.getString(R.string.duration_minutes, m)
            else -> context.getString(R.string.duration_minutes, 0)
        }
    }
}

