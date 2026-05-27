package com.howlindev.appblocker.core.domain.usecase

import com.howlindev.appblocker.core.domain.model.ActiveBlock
import com.howlindev.appblocker.core.domain.repository.BlockRepository
import kotlinx.coroutines.flow.Flow

class ObserveActiveBlockUseCase(private val blockRepository: BlockRepository) {
    operator fun invoke(): Flow<ActiveBlock?> = blockRepository.activeBlock
}
