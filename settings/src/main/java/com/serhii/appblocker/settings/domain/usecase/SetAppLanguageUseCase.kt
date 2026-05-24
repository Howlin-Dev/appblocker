package com.serhii.appblocker.settings.domain.usecase

import com.serhii.appblocker.core.domain.model.AppLanguage
import com.serhii.appblocker.settings.domain.repository.SettingsRepository

class SetAppLanguageUseCase(
    private val repository: SettingsRepository
) {

    suspend operator fun invoke(language: AppLanguage) {
        repository.setLanguage(language)
    }
}