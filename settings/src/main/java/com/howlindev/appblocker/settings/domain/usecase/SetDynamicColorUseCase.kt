package com.howlindev.appblocker.settings.domain.usecase

import com.howlindev.appblocker.settings.domain.repository.SettingsRepository

class SetDynamicColorUseCase(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setDynamicColor(enabled)
    }
}

