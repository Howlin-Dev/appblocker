package com.serhii.appblocker.profiles.domain.usecase

import com.serhii.appblocker.profiles.domain.model.Profile
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository

class CreateProfileUseCase(
    private val repository: ProfilesRepository,
) {
    suspend operator fun invoke(
        name: String,
        appPackages: List<String>,
    ) {
        val profile = Profile(
            id = 0,
            name = name,
            description = "",
            appPackages = appPackages,
            durationMillis = null,
        )
        repository.insert(profile)
    }
}
