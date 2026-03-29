package com.serhii.appblocker.profiles.presentation.list.model

import com.serhii.appblocker.core.domain.model.AppInfo

data class ProfileUi(
    val id: Long,
    val name: String,
    val description: String,
    val blockedApps: List<AppInfo>,
)
