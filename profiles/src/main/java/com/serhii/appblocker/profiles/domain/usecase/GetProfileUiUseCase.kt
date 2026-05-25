package com.serhii.appblocker.profiles.domain.usecase

import com.serhii.appblocker.core.domain.repository.InstalledAppsRepository
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import com.serhii.appblocker.profiles.presentation.list.model.toUi

class GetProfileUiUseCase(
    private val profilesRepository: ProfilesRepository,
    private val installedAppsRepository: InstalledAppsRepository,
) {
    suspend operator fun invoke(id: Long): ProfileUi {
        val profile = profilesRepository.getById(id)
        return profile.toUi(installedAppsRepository)
    }
}
