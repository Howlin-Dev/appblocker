package com.serhii.appblocker.profiles.domain.model

data class Profile(
    val id: Long,
    val name: String,
    val description: String,
    val appPackages: List<String>,
)