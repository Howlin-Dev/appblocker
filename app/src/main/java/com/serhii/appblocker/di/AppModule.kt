package com.serhii.appblocker.di

import com.serhii.appblocker.data.repository.AndroidPermissionRepository
import com.serhii.appblocker.data.repository.InstalledAppsRepositoryImpl
import com.appblocker.permissions.domain.repository.PermissionRepository
import com.serhii.appblocker.navigation.entry.EntryViewModel
import com.appblocker.permissions.platform.PermissionNavigator
import com.appblocker.permissions.platform.requester.PermissionRequester
import com.serhii.appblocker.profiles.domain.repository.InstalledAppsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single { PermissionNavigator(androidContext()) }
    single { PermissionRequester(get()) }

    single<PermissionRepository> { AndroidPermissionRepository(androidContext()) }
    single<InstalledAppsRepository> { InstalledAppsRepositoryImpl(androidContext()) }

    viewModelOf(::EntryViewModel)

    factory { KoinWorkerFactory() }

}
