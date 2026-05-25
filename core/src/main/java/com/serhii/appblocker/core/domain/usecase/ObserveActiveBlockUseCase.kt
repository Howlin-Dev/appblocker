package com.serhii.appblocker.core.domain.usecase

import com.serhii.appblocker.core.domain.model.ActiveBlock
import com.serhii.appblocker.core.domain.repository.BlockRepository
import kotlinx.coroutines.flow.Flow

class ObserveActiveBlockUseCase(private val blockRepository: BlockRepository) {
    operator fun invoke(): Flow<ActiveBlock?> = blockRepository.activeBlock
}
