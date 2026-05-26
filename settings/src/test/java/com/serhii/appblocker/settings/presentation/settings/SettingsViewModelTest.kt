package com.serhii.appblocker.settings.presentation.settings

import app.cash.turbine.test
import com.serhii.appblocker.core.domain.model.SettingsData
import com.serhii.appblocker.core.domain.model.ThemeMode
import com.serhii.appblocker.settings.domain.usecase.GetSettingsUseCase
import com.serhii.appblocker.settings.domain.usecase.SetThemeModeUseCase
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val getSettingsUseCase = mockk<GetSettingsUseCase>()
    private val setThemeModeUseCase = mockk<SetThemeModeUseCase>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `settings should emit values from use case`() = runTest {
        val settingsData = SettingsData(themeMode = ThemeMode.DARK)
        every { getSettingsUseCase() } returns flowOf(settingsData)

        val viewModel = SettingsViewModel(getSettingsUseCase, setThemeModeUseCase)

        viewModel.settings.test {
            // Skip initial value from stateIn
            assertEquals(SettingsData(), awaitItem())
            assertEquals(settingsData, awaitItem())
        }
    }

    @Test
    fun `setThemeMode should call use case`() = runTest {
        every { getSettingsUseCase() } returns flowOf(SettingsData())
        coEvery { setThemeModeUseCase(ThemeMode.LIGHT) } returns Unit

        val viewModel = SettingsViewModel(getSettingsUseCase, setThemeModeUseCase)
        viewModel.setThemeMode(ThemeMode.LIGHT)

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { setThemeModeUseCase(ThemeMode.LIGHT) }
    }
}
