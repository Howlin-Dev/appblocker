package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.core.domain.repository.BlockRepository

class DeactivateProfileUseCase(
    private val blockRepository: BlockRepository,
) {
    suspend operator fun invoke() {
        blockRepository.deactivate()
    }
}

