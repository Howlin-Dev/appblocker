package com.serhii.appblocker.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    suspend fun startTimer(durationMillis: Long)
    suspend fun cancelTimer()
    fun observeRemainingTime(): Flow<Long>
}