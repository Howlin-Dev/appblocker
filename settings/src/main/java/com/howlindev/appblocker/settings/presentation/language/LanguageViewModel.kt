package com.howlindev.appblocker.settings.presentation.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.core.domain.model.AppLanguage
import com.howlindev.appblocker.core.domain.model.SettingsData
import com.howlindev.appblocker.settings.domain.usecase.GetSettingsUseCase
import com.howlindev.appblocker.settings.domain.usecase.SetAppLanguageUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LanguageViewModel(
    getSettingsUseCase: GetSettingsUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase,
) : ViewModel() {

    val settings: StateFlow<SettingsData?> = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            setAppLanguageUseCase(language)
            val locales = when (language) {
                AppLanguage.SYSTEM -> LocaleListCompat.getEmptyLocaleList()
                else -> LocaleListCompat.forLanguageTags(language.tag!!)
            }
            AppCompatDelegate.setApplicationLocales(locales)
        }
    }
}
