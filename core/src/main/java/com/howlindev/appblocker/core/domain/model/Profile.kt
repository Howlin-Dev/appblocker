package com.howlindev.appblocker.core.domain.model

data class Profile(
    val id: Long,
    val name: String,
    val description: String,
    val appPackages: List<String>,
    val blockedWebsites: List<String>,
    val durationMillis: Long?,
)
