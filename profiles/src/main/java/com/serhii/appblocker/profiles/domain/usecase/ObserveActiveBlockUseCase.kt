package com.serhii.appblocker.profiles.domain.usecase

import com.serhii.appblocker.core.domain.model.ActiveBlock
import com.serhii.appblocker.core.domain.repository.BlockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveActiveBlockUseCase(private val blockRepository: BlockRepository) {
    operator fun invoke(): Flow<ActiveBlock?> = blockRepository.activeBlock
}