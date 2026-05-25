package com.serhii.appblocker.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.settings.domain.usecase.GetSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class RootViewModel(
    getSettingsUseCase: GetSettingsUseCase,
) : ViewModel() {

    val settings = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )
}
