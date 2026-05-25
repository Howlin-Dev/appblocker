package com.serhii.appblocker.settings.presentation.language

import com.serhii.appblocker.core.domain.model.AppLanguage

sealed interface LanguageAction {
    data object BackClick: LanguageAction
    data class LanguageChange(val language: AppLanguage): LanguageAction
}
