package com.serhii.appblocker.profiles.util

import kotlin.time.DurationUnit
import kotlin.time.toDuration

internal val timerUiTimeList = listOf<Long?>(
    null,
    60_000,
    15 * 60_000,

    30 * 60_000,
    45 * 60_000,
    60 * 60_000,

    80 * 60_000,
    100 * 60_000,
    120 * 60_000,
)

fun Long?.millisToTimeString(): String {
    if (this == null) return "No Timer"
    val duration = toDuration(DurationUnit.MILLISECONDS)
    duration.toComponents { hours, minutes, _, _ ->
        if (hours == 0L) return "${minutes}m"
        if(minutes == 0) return "${hours}h"
        return "${hours}h ${minutes}m"
    }
}