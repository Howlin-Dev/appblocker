package com.serhii.appblocker.profiles.domain.usecase

import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository

class DeleteProfileUseCase(
    private val profilesRepository: ProfilesRepository
) {
    suspend operator fun invoke(id: Long) = profilesRepository.delete(id)
}