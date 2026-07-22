package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.core.domain.repository.BlockRepository
import com.howlindev.appblocker.profiles.domain.repository.ProfilesRepository
import kotlinx.coroutines.flow.firstOrNull

class DeleteProfileUseCase(
    private val profilesRepository: ProfilesRepository,
    private val blockRepository: BlockRepository,
) {
    suspend operator fun invoke(id: Long) {
        val activeBlock = blockRepository.activeBlock.firstOrNull()
        if (activeBlock?.profileId == id) {
            blockRepository.deactivate()
        }
        profilesRepository.delete(id)
    }
}
