package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.core.domain.model.Profile
import com.howlindev.appblocker.core.domain.repository.ProfilesRepository

class GetProfileUseCase(
    private val profilesRepository: ProfilesRepository,
) {
    suspend operator fun invoke(id: Long): Profile? = profilesRepository.getById(id)
}
