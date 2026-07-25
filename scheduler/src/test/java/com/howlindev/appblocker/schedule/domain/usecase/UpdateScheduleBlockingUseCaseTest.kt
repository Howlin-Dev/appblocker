package com.howlindev.appblocker.schedule.domain.usecase

import com.howlindev.appblocker.core.domain.model.Profile
import com.howlindev.appblocker.core.domain.repository.BlockRepository
import com.howlindev.appblocker.core.domain.repository.ProfilesRepository
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class UpdateScheduleBlockingUseCaseTest {

    private val scheduleRepository = mockk<ScheduleRepository>()
    private val profilesRepository = mockk<ProfilesRepository>()
    private val blockRepository = mockk<BlockRepository>(relaxed = true)
    private lateinit var useCase: UpdateScheduleBlockingUseCase

    @Before
    fun setUp() {
        useCase = UpdateScheduleBlockingUseCase(
            scheduleRepository,
            profilesRepository,
            blockRepository,
        )
    }

    @Test
    fun `when current time is within schedule, activate blocking`() = runBlocking {
        val now = LocalTime.now()
        val event = ScheduleEvent(
            id = 1L,
            profileId = 1L,
            startTime = now.minusHours(1),
            endTime = now.plusHours(1),
            daysOfWeek = setOf(LocalDate.now().dayOfWeek),
            title = "Work",
        )
        val profile = Profile(
            id = 1L,
            name = "Work",
            description = "",
            appPackages = listOf("com.social.app"),
            blockedWebsites = listOf("social.com"),
            durationMillis = null,
        )

        coEvery { scheduleRepository.getAllEvents() } returns flowOf(listOf(event))
        coEvery { profilesRepository.getAll() } returns flowOf(listOf(profile))

        useCase()

        coVerify {
            blockRepository.activateScheduledBlocking(
                listOf("com.social.app"),
                listOf("social.com"),
                profileEndTimes = emptyMap(),
            )
        }
    }

    @Test
    fun `when current time is outside schedule, deactivate blocking`() = runBlocking {
        val now = LocalTime.now()
        val event = ScheduleEvent(
            id = 1L,
            profileId = 1L,
            startTime = now.plusHours(1),
            endTime = now.plusHours(2),
            daysOfWeek = setOf(LocalDate.now().dayOfWeek),
            title = "Work",
        )

        coEvery { scheduleRepository.getAllEvents() } returns flowOf(listOf(event))

        useCase()

        coVerify { blockRepository.deactivateScheduled() }
    }
}
