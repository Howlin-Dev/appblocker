package com.serhii.appblocker.profiles.domain.usecase

import com.serhii.appblocker.profiles.domain.model.Profile
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository
import kotlinx.coroutines.flow.Flow

class UpdateProfileUseCase(
    private val profilesRepository: ProfilesRepository
) {
    suspend operator fun invoke(profile: Profile) = profilesRepository.update(profile)
}