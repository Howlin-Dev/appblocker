package com.serhii.appblocker.settings.di

import com.serhii.appblocker.settings.data.repository.SettingsRepositoryImpl
import com.serhii.appblocker.settings.domain.repository.SettingsRepository
import com.serhii.appblocker.settings.domain.usecase.GetSettingsUseCase
import com.serhii.appblocker.settings.domain.usecase.SetAppLanguageUseCase
import com.serhii.appblocker.settings.domain.usecase.SetThemeModeUseCase
import com.serhii.appblocker.settings.presentation.language.LanguageViewModel
import com.serhii.appblocker.settings.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {

    viewModelOf(::SettingsViewModel)
    viewModelOf(::LanguageViewModel)

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    factory { GetSettingsUseCase(get()) }
    factory { SetThemeModeUseCase(get()) }
    factory { SetAppLanguageUseCase(get()) }
}