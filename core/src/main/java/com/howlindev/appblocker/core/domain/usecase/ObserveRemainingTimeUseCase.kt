package com.howlindev.appblocker.core.domain.usecase

import com.howlindev.appblocker.core.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow

class ObserveRemainingTimeUseCase(private val timerRepository: TimerRepository) {
    operator fun invoke(): Flow<Long> = timerRepository.observeRemainingTime()
}

