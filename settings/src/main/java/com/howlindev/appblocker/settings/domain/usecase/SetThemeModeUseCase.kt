package com.howlindev.appblocker.settings.domain.usecase

import com.howlindev.appblocker.core.domain.model.ThemeMode
import com.howlindev.appblocker.settings.domain.repository.SettingsRepository

class SetThemeModeUseCase(
    private val repository: SettingsRepository,
) {

    suspend operator fun invoke(themeMode: ThemeMode) {
        repository.setThemeMode(themeMode)
    }
}

