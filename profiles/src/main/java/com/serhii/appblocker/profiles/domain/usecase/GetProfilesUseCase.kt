package com.serhii.appblocker.profiles.domain.usecase

import com.serhii.appblocker.profiles.domain.model.Profile
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository
import kotlinx.coroutines.flow.Flow

class GetProfilesUseCase(
    private val profilesRepository: ProfilesRepository
) {
    operator fun invoke(): Flow<List<Profile>> = profilesRepository.getAll()
}
