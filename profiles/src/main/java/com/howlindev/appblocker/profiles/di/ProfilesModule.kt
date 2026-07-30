package com.howlindev.appblocker.profiles.di

import com.howlindev.appblocker.core.domain.repository.ProfilesRepository
import com.howlindev.appblocker.profiles.data.repository.ProfilesRepositoryImpl
import com.howlindev.appblocker.profiles.domain.usecase.ActivateProfileUseCase
import com.howlindev.appblocker.profiles.domain.usecase.AddSuggestedWebsiteUseCase
import com.howlindev.appblocker.profiles.domain.usecase.CreateProfileUseCase
import com.howlindev.appblocker.profiles.domain.usecase.DeactivateProfileUseCase
import com.howlindev.appblocker.profiles.domain.usecase.DeleteProfileUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetInstalledAppsUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetProfileUiUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetProfileUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetProfilesUiUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetProfilesUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetSuggestedWebsitesUseCase
import com.howlindev.appblocker.profiles.domain.usecase.RemoveSuggestedWebsiteUseCase
import com.howlindev.appblocker.profiles.domain.usecase.UpdateProfileUseCase
import com.howlindev.appblocker.profiles.presentation.create.CreateProfileViewModel
import com.howlindev.appblocker.profiles.presentation.detail.ManageProfileAppListViewModel
import com.howlindev.appblocker.profiles.presentation.detail.ManageProfileWebsiteListViewModel
import com.howlindev.appblocker.profiles.presentation.detail.ProfileDetailViewModel
import com.howlindev.appblocker.profiles.presentation.list.ProfileListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profilesModule = module {
    single<ProfilesRepository> { ProfilesRepositoryImpl(get()) }

    viewModelOf(::ProfileListViewModel)
    viewModelOf(::CreateProfileViewModel)
    viewModelOf(::ProfileDetailViewModel)
    viewModelOf(::ManageProfileAppListViewModel)
    viewModelOf(::ManageProfileWebsiteListViewModel)

    factory { GetProfilesUseCase(get()) }
    factory { GetProfilesUiUseCase(get(), get()) }
    factory { GetProfileUseCase(get()) }
    factory { GetProfileUiUseCase(get(), get()) }
    factory { UpdateProfileUseCase(get()) }
    factory { DeleteProfileUseCase(get(), get()) }
    factory { ActivateProfileUseCase(get(), get()) }
    factory { DeactivateProfileUseCase(get()) }
    factory { GetInstalledAppsUseCase(get()) }
    factory { CreateProfileUseCase(get()) }
    factory { GetSuggestedWebsitesUseCase(get()) }
    factory { AddSuggestedWebsiteUseCase(get()) }
    factory { RemoveSuggestedWebsiteUseCase(get()) }
}
