package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.profiles.domain.model.Profile
import com.howlindev.appblocker.profiles.domain.repository.ProfilesRepository

class CreateProfileUseCase(
    private val repository: ProfilesRepository,
) {
    suspend operator fun invoke(
        name: String,
        appPackages: List<String>,
        blockedWebsites: List<String> = emptyList(),
    ) {
        val profile = Profile(
            id = 0,
            name = name,
            description = "",
            appPackages = appPackages,
            blockedWebsites = blockedWebsites,
            durationMillis = null,
        )
        repository.insert(profile)
    }
}
