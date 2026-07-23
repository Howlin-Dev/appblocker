package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.core.domain.model.Profile
import com.howlindev.appblocker.core.domain.repository.ProfilesRepository

class CreateProfileUseCase(
    private val repository: ProfilesRepository,
) {
    suspend operator fun invoke(
        name: String,
        appPackages: List<String>,
        blockedWebsites: List<String> = emptyList(),
    ): Long {
        val profile = Profile(
            id = 0,
            name = name,
            description = "",
            appPackages = appPackages,
            blockedWebsites = blockedWebsites,
            durationMillis = null,
        )
        return repository.insert(profile)
    }
}
