package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.core.domain.model.AppInfo
import com.howlindev.appblocker.core.domain.repository.InstalledAppsRepository

class GetInstalledAppsUseCase(
    private val repository: InstalledAppsRepository,
) {
    suspend operator fun invoke(): List<AppInfo> = repository.getInstalledApps()
}

