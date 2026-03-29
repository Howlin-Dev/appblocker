package com.serhii.appblocker.profiles.domain.repository

import com.serhii.appblocker.core.domain.model.AppInfo

interface InstalledAppsRepository {
    suspend fun getInstalledApps(): List<AppInfo>
}