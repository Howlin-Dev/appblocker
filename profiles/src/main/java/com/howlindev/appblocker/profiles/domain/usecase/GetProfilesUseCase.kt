package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.core.domain.model.Profile
import com.howlindev.appblocker.core.domain.repository.ProfilesRepository
import kotlinx.coroutines.flow.Flow

class GetProfilesUseCase(
    private val profilesRepository: ProfilesRepository,
) {
    operator fun invoke(): Flow<List<Profile>> = profilesRepository.getAll()
}
