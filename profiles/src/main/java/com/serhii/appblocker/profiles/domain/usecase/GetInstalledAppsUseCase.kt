package com.serhii.appblocker.profiles.domain.usecase

import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.profiles.domain.repository.InstalledAppsRepository

class GetInstalledAppsUseCase(
    private val repository: InstalledAppsRepository
) {
    suspend operator fun invoke(): List<AppInfo> = repository.getInstalledApps()
}
