package com.serhii.appblocker.profiles.domain.usecase

import com.serhii.appblocker.profiles.domain.model.Profile
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository
import kotlinx.coroutines.flow.Flow

class GetProfilesUseCase(
    private val profilesRepository: ProfilesRepository
) {
    fun execute(): Flow<List<Profile>> = profilesRepository.getAll()
}