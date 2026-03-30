package com.serhii.appblocker.profiles.di

import com.serhii.appblocker.profiles.data.repository.ProfilesRepositoryImpl
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository
import com.serhii.appblocker.profiles.presentation.create.CreateProfileViewModel
import com.serhii.appblocker.profiles.presentation.list.ProfileListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profilesModule = module {
    single<ProfilesRepository> { ProfilesRepositoryImpl(get()) }

    viewModelOf(::ProfileListViewModel)
    viewModelOf(::CreateProfileViewModel)
}
