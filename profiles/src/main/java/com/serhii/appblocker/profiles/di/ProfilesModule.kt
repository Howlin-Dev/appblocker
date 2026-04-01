package com.serhii.appblocker.profiles.di

import com.serhii.appblocker.profiles.data.repository.ProfilesRepositoryImpl
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository
import com.serhii.appblocker.profiles.domain.usecase.ActivateProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.DeactivateProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.GetProfilesUseCase
import com.serhii.appblocker.profiles.domain.usecase.ObserveActiveProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.ObserveRemainingTimeUseCase
import com.serhii.appblocker.profiles.presentation.create.CreateProfileViewModel
import com.serhii.appblocker.profiles.presentation.list.ProfileListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profilesModule = module {
    single<ProfilesRepository> { ProfilesRepositoryImpl(get()) }

    viewModelOf(::ProfileListViewModel)
    viewModelOf(::CreateProfileViewModel)

    factory { GetProfilesUseCase(get()) }
    factory { ObserveActiveProfileUseCase(get()) }
    factory { ObserveRemainingTimeUseCase(get()) }
    factory { ActivateProfileUseCase(get(), get()) }
    factory { DeactivateProfileUseCase(get()) }
}
