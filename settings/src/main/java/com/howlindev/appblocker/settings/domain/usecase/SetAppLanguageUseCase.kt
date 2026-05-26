package com.howlindev.appblocker.settings.domain.usecase

import com.howlindev.appblocker.core.domain.model.AppLanguage
import com.howlindev.appblocker.settings.domain.repository.SettingsRepository

class SetAppLanguageUseCase(
    private val repository: SettingsRepository,
) {

    suspend operator fun invoke(language: AppLanguage) {
        repository.setLanguage(language)
    }
}

