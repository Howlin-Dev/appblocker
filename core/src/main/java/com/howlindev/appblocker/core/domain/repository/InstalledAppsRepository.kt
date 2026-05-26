package com.howlindev.appblocker.core.domain.repository

import com.howlindev.appblocker.core.domain.model.AppInfo

interface InstalledAppsRepository {
    suspend fun getInstalledApps(): List<AppInfo>
    suspend fun getAppInfo(packageName: String): AppInfo
}

