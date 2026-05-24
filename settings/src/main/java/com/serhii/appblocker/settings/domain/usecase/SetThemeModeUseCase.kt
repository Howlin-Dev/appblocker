package com.serhii.appblocker.settings.domain.usecase

import com.serhii.appblocker.core.domain.model.ThemeMode
import com.serhii.appblocker.settings.domain.repository.SettingsRepository

class SetThemeModeUseCase(
    private val repository: SettingsRepository
) {

    suspend operator fun invoke(themeMode: ThemeMode) {
        repository.setThemeMode(themeMode)
    }
}