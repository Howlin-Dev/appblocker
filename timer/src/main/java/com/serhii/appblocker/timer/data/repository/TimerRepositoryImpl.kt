package com.serhii.appblocker.timer.data.repository

import com.serhii.appblocker.core.domain.repository.BlockRepository
import com.serhii.appblocker.core.domain.repository.TimerRepository
import com.serhii.appblocker.timer.data.datastore.TimerDataStore
import com.serhii.appblocker.timer.data.scheduler.TimerScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class TimerRepositoryImpl(
    private val dataStore: TimerDataStore,
    private val scheduler: TimerScheduler,
    private val blockRepository: BlockRepository,
) : TimerRepository {

    override suspend fun startTimer(durationMillis: Long) {
        val endTime = System.currentTimeMillis() + durationMillis
        dataStore.setEndTime(endTime)
        scheduler.schedule(endTime)
    }

    override suspend fun cancelTimer() {
        onTimerFinished()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeRemainingTime(): Flow<Long> =
        combine(
            dataStore.observeEndTime(),
            blockRepository.activeBlock
        ) { endTime, activeBlock ->
            endTime to activeBlock
        }.flatMapLatest { (endTime, activeBlock) ->
            if (endTime <= 0L && activeBlock?.isTimed == true) {
                onTimerFinished()
                flowOf(0L)
            } else {
                flow {
                    while (true) {
                        val remaining =
                            (endTime - System.currentTimeMillis()).coerceAtLeast(0)

                        emit(remaining)

                        if (remaining <= 0L && activeBlock?.isTimed == true) {
                            onTimerFinished()
                            break
                        }

                        delay(1000)
                    }
                }
            }
        }

    private suspend fun onTimerFinished() {
        scheduler.cancel()
        dataStore.clear()
        blockRepository.deactivate()
    }
}
