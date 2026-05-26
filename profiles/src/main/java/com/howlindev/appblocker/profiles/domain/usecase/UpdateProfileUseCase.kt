package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.profiles.domain.model.Profile
import com.howlindev.appblocker.profiles.domain.repository.ProfilesRepository

class UpdateProfileUseCase(
    private val profilesRepository: ProfilesRepository,
) {
    suspend operator fun invoke(profile: Profile) = profilesRepository.update(profile)
}

