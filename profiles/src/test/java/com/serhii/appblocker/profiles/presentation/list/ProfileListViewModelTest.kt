package com.serhii.appblocker.profiles.presentation.list

import app.cash.turbine.test
import com.serhii.appblocker.core.domain.model.ActiveBlock
import com.serhii.appblocker.core.domain.usecase.ObserveRemainingTimeUseCase
import com.serhii.appblocker.profiles.domain.model.Profile
import com.serhii.appblocker.profiles.domain.repository.InstalledAppsRepository
import com.serhii.appblocker.profiles.domain.usecase.ActivateProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.DeactivateProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.GetProfilesUseCase
import com.serhii.appblocker.profiles.domain.usecase.ObserveActiveBlockUseCase
import com.serhii.appblocker.profiles.domain.usecase.UpdateProfileUseCase
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.profiles.presentation.list.model.toUi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val observeRemainingTimeUseCase = mockk<ObserveRemainingTimeUseCase>()
    private val getProfilesUseCase = mockk<GetProfilesUseCase>()
    private val observeActiveBlockUseCase = mockk<ObserveActiveBlockUseCase>()
    private val activateProfileUseCase = mockk<ActivateProfileUseCase>()
    private val deactivateProfileUseCase = mockk<DeactivateProfileUseCase>()
    private val installedAppsRepository = mockk<InstalledAppsRepository>()
    private val updateProfileUseCase = mockk<UpdateProfileUseCase>()

    private lateinit var viewModel: ProfileListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { observeRemainingTimeUseCase() } returns flowOf(0L)
        every { getProfilesUseCase() } returns flowOf(emptyList())
        every { observeActiveBlockUseCase() } returns flowOf(null)
        coEvery { installedAppsRepository.getAppInfo(any()) } returns AppInfo("pkg", "App Name", null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when profiles are fetched, state should contain them as inactive`() = runTest {
        val profiles = listOf(
            Profile(1, "Work", "Work profile", listOf("pkg.1"), 3600L),
            Profile(2, "Study", "Study profile", listOf("pkg.2"), null)
        )
        every { getProfilesUseCase() } returns flowOf(profiles)

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.inactiveProfiles.size)
            assertNull(state.activeProfile)
            assertEquals("Work", state.inactiveProfiles[0].name)
        }
    }

    @Test
    fun `when a profile is active, it should be in activeProfile state`() = runTest {
        val profiles = listOf(
            Profile(1, "Work", "Work profile", listOf("pkg.1"), 3600L),
            Profile(2, "Study", "Study profile", listOf("pkg.2"), null)
        )
        every { getProfilesUseCase() } returns flowOf(profiles)
        every { observeActiveBlockUseCase() } returns flowOf(ActiveBlock(1, listOf("pkg.1"), true))

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.activeProfile)
            assertEquals(1L, state.activeProfile?.id)
            assertEquals(1, state.inactiveProfiles.size)
            assertEquals(2, state.inactiveProfiles[0].id)
        }
    }

    @Test
    fun `toggleProfileActivation should call activate when no active profile`() = runTest {
        val profiles = listOf(Profile(1, "Work", "Work profile", listOf("pkg.1"), 3600L))
        every { getProfilesUseCase() } returns flowOf(profiles)
        coEvery { activateProfileUseCase(any()) } returns Unit

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val profileUi = viewModel.state.value.inactiveProfiles[0]
        viewModel.toggleProfileActivation(profileUi)
        testDispatcher.scheduler.advanceUntilIdle() // Let the toggle coroutine finish

        coVerify { activateProfileUseCase(any()) }
    }

    @Test
    fun `toggleProfileActivation should call deactivate when toggling active profile`() = runTest {
        val profiles = listOf(Profile(1, "Work", "Work profile", listOf("pkg.1"), 3600L))
        every { getProfilesUseCase() } returns flowOf(profiles)
        every { observeActiveBlockUseCase() } returns flowOf(ActiveBlock(1, listOf("pkg.1"), true))
        coEvery { deactivateProfileUseCase() } returns Unit

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val profileUi = viewModel.state.value.activeProfile!!
        viewModel.toggleProfileActivation(profileUi)
        testDispatcher.scheduler.advanceUntilIdle() // Let the toggle coroutine finish

        coVerify { deactivateProfileUseCase() }
    }

    private fun createViewModel() = ProfileListViewModel(
        observeRemainingTimeUseCase,
        getProfilesUseCase,
        observeActiveBlockUseCase,
        activateProfileUseCase,
        deactivateProfileUseCase,
        installedAppsRepository,
        updateProfileUseCase
    )
}
