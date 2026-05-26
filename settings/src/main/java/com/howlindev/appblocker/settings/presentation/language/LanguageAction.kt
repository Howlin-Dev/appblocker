package com.howlindev.appblocker.settings.presentation.language

import com.howlindev.appblocker.core.domain.model.AppLanguage

sealed interface LanguageAction {
    data object BackClick : LanguageAction
    data class LanguageChange(val language: AppLanguage) : LanguageAction
}

