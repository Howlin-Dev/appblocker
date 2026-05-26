package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.core.domain.repository.InstalledAppsRepository
import com.howlindev.appblocker.profiles.domain.repository.ProfilesRepository
import com.howlindev.appblocker.profiles.presentation.list.model.ProfileUi
import com.howlindev.appblocker.profiles.presentation.list.model.toUi

class GetProfileUiUseCase(
    private val profilesRepository: ProfilesRepository,
    private val installedAppsRepository: InstalledAppsRepository,
) {
    suspend operator fun invoke(id: Long): ProfileUi {
        val profile = profilesRepository.getById(id)
        return profile.toUi(installedAppsRepository)
    }
}

