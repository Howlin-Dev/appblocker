package com.serhii.appblocker.profiles.domain.usecase

import com.serhii.appblocker.core.domain.repository.BlockRepository

class DeactivateProfileUseCase(
    private val blockRepository: BlockRepository
) {
    suspend operator fun invoke() {
        blockRepository.deactivate()
    }
}