package com.howlindev.appblocker.profiles.presentation.list.model

import com.howlindev.appblocker.core.domain.model.AppInfo
import com.howlindev.appblocker.core.domain.repository.InstalledAppsRepository
import com.howlindev.appblocker.profiles.domain.model.Profile

data class ProfileUi(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val blockedApps: List<AppInfo> = emptyList(),
    val durationMillis: Long? = null,
)

// --- mapping ---

suspend fun Profile.toUi(installedAppsRepository: InstalledAppsRepository): ProfileUi {
    return ProfileUi(
        id = id,
        name = name,
        description = description,
        blockedApps = appPackages.map {
            installedAppsRepository.getAppInfo(it)
        },
        durationMillis = durationMillis,
    )
}

fun ProfileUi.toDomain(): Profile {
    return Profile(
        id = id,
        name = name,
        description = description,
        appPackages = blockedApps.map { it.packageName },
        durationMillis = durationMillis,
    )
}

