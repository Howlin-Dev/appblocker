package com.howlindev.appblocker.profiles.domain.model

data class Profile(
    val id: Long,
    val name: String,
    val description: String,
    val appPackages: List<String>,
    val durationMillis: Long?,
)

