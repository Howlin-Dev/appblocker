package com.serhii.appblocker.profiles.di

import com.serhii.appblocker.profiles.data.repository.ProfilesRepositoryImpl
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository
import com.serhii.appblocker.profiles.domain.usecase.ActivateProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.DeactivateProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.DeleteProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.GetProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.GetProfilesUseCase
import com.serhii.appblocker.profiles.domain.usecase.ObserveActiveBlockUseCase
import com.serhii.appblocker.core.domain.usecase.ObserveRemainingTimeUseCase
import com.serhii.appblocker.profiles.domain.usecase.UpdateProfileUseCase
import com.serhii.appblocker.profiles.presentation.create.CreateProfileViewModel
import com.serhii.appblocker.profiles.presentation.detail.ManageProfileAppListViewModel
import com.serhii.appblocker.profiles.presentation.detail.ProfileDetailViewModel
import com.serhii.appblocker.profiles.presentation.list.ProfileListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profilesModule = module {
    single<ProfilesRepository> { ProfilesRepositoryImpl(get()) }

    viewModelOf(::ProfileListViewModel)
    viewModelOf(::CreateProfileViewModel)
    viewModelOf(::ProfileDetailViewModel)
    viewModelOf(::ManageProfileAppListViewModel)

    factory { GetProfilesUseCase(get()) }
    factory { GetProfileUseCase(get()) }
    factory { UpdateProfileUseCase(get()) }
    factory { DeleteProfileUseCase(get()) }
    factory { ObserveActiveBlockUseCase(get()) }
    factory { ObserveRemainingTimeUseCase(get()) }
    factory { ActivateProfileUseCase(get(), get()) }
    factory { DeactivateProfileUseCase(get()) }
}
