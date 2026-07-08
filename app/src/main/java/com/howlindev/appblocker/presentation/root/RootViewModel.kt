package com.howlindev.appblocker.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.core.domain.usecase.ObserveActiveBlockUseCase
import com.howlindev.appblocker.core.domain.usecase.ObserveRemainingTimeUseCase
import com.howlindev.appblocker.permissions.domain.usecase.GetMissingPermissionsUseCase
import com.howlindev.appblocker.platform.notification.manager.BlockNotificationManager
import com.howlindev.appblocker.settings.domain.usecase.GetSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class RootViewModel(
    getSettingsUseCase: GetSettingsUseCase,
    observeActiveBlockUseCase: ObserveActiveBlockUseCase,
    observeRemainingTimeUseCase: ObserveRemainingTimeUseCase,
    blockNotificationManager: BlockNotificationManager,
    private val getMissingPermissionsUseCase: GetMissingPermissionsUseCase,
) : ViewModel() {

    val settings = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    private val _arePermissionsNeeded = MutableStateFlow<Boolean?>(null)
    val arePermissionsNeeded: StateFlow<Boolean?> = _arePermissionsNeeded.asStateFlow()

    init {
        combine(
            observeActiveBlockUseCase(),
            observeRemainingTimeUseCase(),
        ) { activeBlock, remainingTime ->
            blockNotificationManager.updateNotification(activeBlock, remainingTime)
        }.launchIn(viewModelScope)

        checkPermissions()
    }

    private fun checkPermissions() {
        val missingPermissions = getMissingPermissionsUseCase()
        _arePermissionsNeeded.update { missingPermissions.isNotEmpty() }
    }
}
