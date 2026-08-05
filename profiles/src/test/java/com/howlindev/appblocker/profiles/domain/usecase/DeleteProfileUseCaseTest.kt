package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.core.domain.model.ActiveBlock
import com.howlindev.appblocker.core.domain.repository.BlockRepository
import com.howlindev.appblocker.core.domain.repository.ProfilesRepository
import com.howlindev.appblocker.schedule.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteProfileUseCaseTest {

    private val profilesRepository = mockk<ProfilesRepository>(relaxed = true)
    private val blockRepository = mockk<BlockRepository>(relaxed = true)
    private val scheduleRepository = mockk<ScheduleRepository>(relaxed = true)
    private lateinit var useCase: DeleteProfileUseCase

    @Before
    fun setup() {
        useCase = DeleteProfileUseCase(profilesRepository, blockRepository, scheduleRepository)
    }

    @Test
    fun `when profile is deleted, schedule events should also be deleted`() = runTest {
        val profileId = 1L
        every { blockRepository.activeBlock } returns flowOf(null)

        useCase(profileId)

        coVerify { scheduleRepository.deleteEventsByProfileId(profileId) }
        coVerify { profilesRepository.delete(profileId) }
    }

    @Test
    fun `when active profile is deleted, it should be deactivated before deletion`() = runTest {
        val profileId = 1L
        val activeBlock = ActiveBlock(profileId, emptyList(), emptyList(), isTimed = true, isScheduled = false)
        every { blockRepository.activeBlock } returns flowOf(activeBlock)
        coEvery { blockRepository.deactivateTimed() } returns Unit

        useCase(profileId)

        coVerify { blockRepository.deactivateTimed() }
        coVerify { scheduleRepository.deleteEventsByProfileId(profileId) }
        coVerify { profilesRepository.delete(profileId) }
    }
}
