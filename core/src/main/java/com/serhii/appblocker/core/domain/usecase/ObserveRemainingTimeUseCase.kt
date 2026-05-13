package com.serhii.appblocker.core.domain.usecase

import com.serhii.appblocker.core.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow

class ObserveRemainingTimeUseCase(private val timerRepository: TimerRepository) {
    operator fun invoke(): Flow<Long> = timerRepository.observeRemainingTime()
}