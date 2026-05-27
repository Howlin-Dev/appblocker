package com.howlindev.appblocker.core.domain.model

data class ActiveBlock(
    val profileId: Long,
    val blockedPackages: List<String>,
    val isTimed: Boolean,
)
