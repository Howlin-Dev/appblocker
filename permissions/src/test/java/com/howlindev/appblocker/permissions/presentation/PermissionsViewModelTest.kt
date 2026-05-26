package com.howlindev.appblocker.permissions.presentation

import app.cash.turbine.test
import com.howlindev.appblocker.permissions.domain.model.RequiredPermission
import com.howlindev.appblocker.permissions.domain.usecase.GetMissingPermissionsUseCase
import com.howlindev.appblocker.permissions.domain.usecase.RequestPermissionUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PermissionsViewModelTest {

    private val getMissingPermissionsUseCase = mockk<GetMissingPermissionsUseCase>()
    private val requestPermissionUseCase = mockk<RequestPermissionUseCase>()
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
    fun `checkPermissions should update state with missing permissions`() = runTest {
        val missingPermissions = listOf(RequiredPermission.Overlay)
        every { getMissingPermissionsUseCase() } returns missingPermissions

        val viewModel = PermissionsViewModel(getMissingPermissionsUseCase, requestPermissionUseCase)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(missingPermissions, state.missingRequiredPermissions)
            assertEquals(false, state.allPermissionsGranted)
        }
    }

    @Test
    fun `checkPermissions should set allPermissionsGranted to true if no missing permissions`() = runTest {
        every { getMissingPermissionsUseCase() } returns emptyList()

        val viewModel = PermissionsViewModel(getMissingPermissionsUseCase, requestPermissionUseCase)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.allPermissionsGranted)
            assertTrue(state.missingRequiredPermissions.isEmpty())
        }
    }

    @Test
    fun `requestPermission should call use case`() = runTest {
        val permission = RequiredPermission.Accessibility
        every { requestPermissionUseCase(permission) } returns Unit
        every { getMissingPermissionsUseCase() } returns emptyList()

        val viewModel = PermissionsViewModel(getMissingPermissionsUseCase, requestPermissionUseCase)
        viewModel.requestPermission(permission)

        verify { requestPermissionUseCase(permission) }
    }
}

