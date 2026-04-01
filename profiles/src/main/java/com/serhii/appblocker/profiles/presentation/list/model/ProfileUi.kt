package com.serhii.appblocker.profiles.presentation.list.model

import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.profiles.domain.model.Profile
import com.serhii.appblocker.profiles.domain.repository.InstalledAppsRepository

data class ProfileUi(
    val id: Long,
    val name: String,
    val description: String,
    val blockedApps: List<AppInfo>,
)