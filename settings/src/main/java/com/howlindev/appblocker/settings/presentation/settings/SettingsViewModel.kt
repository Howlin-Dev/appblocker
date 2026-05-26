package com.howlindev.appblocker.settings.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.core.domain.model.SettingsData
import com.howlindev.appblocker.core.domain.model.ThemeMode
import com.howlindev.appblocker.settings.domain.usecase.GetSettingsUseCase
import com.howlindev.appblocker.settings.domain.usecase.SetDynamicColorUseCase
import com.howlindev.appblocker.settings.domain.usecase.SetThemeModeUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    getSettingsUseCase: GetSettingsUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val setDynamicColorUseCase: SetDynamicColorUseCase,
) : ViewModel() {

    val settings = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsData(),
        )

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase(themeMode)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            setDynamicColorUseCase(enabled)
        }
    }
}

