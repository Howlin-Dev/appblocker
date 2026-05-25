package com.serhii.appblocker.core.util

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
        seconds
    )
}

fun Long?.millisToTimeString(): String {
    if (this == null) return "No Timer"
    val duration = toDuration(DurationUnit.MILLISECONDS)
    return duration.toComponents { hours, minutes, _, _ ->
        val h = hours.toInt()
        val m = minutes
        when {
            h > 0 && m > 0 -> "${h}h ${m}m"
            h > 0 -> "${h}h"
            m > 0 -> "${m}m"
            else -> "0m"
        }
    }
}
