package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.profiles.domain.model.Profile
import com.howlindev.appblocker.profiles.domain.repository.ProfilesRepository
import kotlinx.coroutines.flow.Flow

class GetProfilesUseCase(
    private val profilesRepository: ProfilesRepository,
) {
    operator fun invoke(): Flow<List<Profile>> = profilesRepository.getAll()
}

