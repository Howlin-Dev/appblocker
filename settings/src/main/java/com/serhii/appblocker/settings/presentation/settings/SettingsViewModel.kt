package com.serhii.appblocker.settings.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.core.domain.model.SettingsData
import com.serhii.appblocker.core.domain.model.ThemeMode
import com.serhii.appblocker.settings.domain.usecase.GetSettingsUseCase
import com.serhii.appblocker.settings.domain.usecase.SetThemeModeUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    getSettingsUseCase: GetSettingsUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
): ViewModel() {

    val settings = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsData()
        )

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase(themeMode)
        }
    }
}