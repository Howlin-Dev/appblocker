package com.howlindev.appblocker.timer.data.repository

import com.howlindev.appblocker.core.domain.repository.BlockRepository
import com.howlindev.appblocker.timer.data.datastore.TimerDataStore
import com.howlindev.appblocker.timer.data.scheduler.TimerScheduler
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerRepositoryImplTest {

    private val dataStore = mockk<TimerDataStore>(relaxed = true)
    private val scheduler = mockk<TimerScheduler>(relaxed = true)
    private val blockRepository = mockk<BlockRepository>(relaxed = true)

    private val repository = TimerRepositoryImpl(
        dataStore = dataStore,
        scheduler = scheduler,
        blockRepository = blockRepository,
    )

    @Test
    fun `startTimer should set end time and schedule`() = runTest {
        val duration = 60000L

        repository.startTimer(duration)

        coVerify { dataStore.setEndTime(any()) }
        coVerify { scheduler.schedule(any()) }
    }

    @Test
    fun `cancelTimer should cancel scheduler and clear data`() = runTest {
        repository.cancelTimer()

        coVerify { scheduler.cancel() }
        coVerify { dataStore.clear() }
        coVerify { blockRepository.deactivate() }
    }
}

