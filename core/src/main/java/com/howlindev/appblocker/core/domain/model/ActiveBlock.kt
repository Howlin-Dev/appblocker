package com.howlindev.appblocker.core.domain.model

data class ActiveBlock(
    val profileId: Long?,
    val blockedPackages: List<String>,
    val blockedWebsites: List<String> = emptyList(),
    val isTimed: Boolean,
    val isScheduled: Boolean,
    val hasTimer: Boolean = isTimed,
    val scheduledProfileEndTimes: Map<Long, String> = emptyMap(),
)
