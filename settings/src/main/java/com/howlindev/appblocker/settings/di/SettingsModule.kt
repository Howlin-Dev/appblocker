package com.howlindev.appblocker.settings.di

import com.howlindev.appblocker.settings.data.repository.SettingsRepositoryImpl
import com.howlindev.appblocker.settings.domain.repository.SettingsRepository
import com.howlindev.appblocker.settings.domain.usecase.GetSettingsUseCase
import com.howlindev.appblocker.settings.domain.usecase.SetAppLanguageUseCase
import com.howlindev.appblocker.settings.domain.usecase.SetDynamicColorUseCase
import com.howlindev.appblocker.settings.domain.usecase.SetThemeModeUseCase
import com.howlindev.appblocker.settings.presentation.language.LanguageViewModel
import com.howlindev.appblocker.settings.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {

    viewModelOf(::SettingsViewModel)
    viewModelOf(::LanguageViewModel)

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    factory { GetSettingsUseCase(get()) }
    factory { SetThemeModeUseCase(get()) }
    factory { SetAppLanguageUseCase(get()) }
    factory { SetDynamicColorUseCase(get()) }
}

