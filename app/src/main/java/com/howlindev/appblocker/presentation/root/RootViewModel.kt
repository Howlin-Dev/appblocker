package com.howlindev.appblocker.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.core.domain.usecase.ObserveActiveBlockUseCase
import com.howlindev.appblocker.core.domain.usecase.ObserveRemainingTimeUseCase
import com.howlindev.appblocker.platform.notification.manager.BlockNotificationManager
import com.howlindev.appblocker.settings.domain.usecase.GetSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn

class RootViewModel(
    getSettingsUseCase: GetSettingsUseCase,
    observeActiveBlockUseCase: ObserveActiveBlockUseCase,
    observeRemainingTimeUseCase: ObserveRemainingTimeUseCase,
    blockNotificationManager: BlockNotificationManager,
) : ViewModel() {

    val settings = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    init {
        combine(
            observeActiveBlockUseCase(),
            observeRemainingTimeUseCase(),
        ) { activeBlock, remainingTime ->
            blockNotificationManager.updateNotification(activeBlock, remainingTime)
        }.launchIn(viewModelScope)
    }
}

