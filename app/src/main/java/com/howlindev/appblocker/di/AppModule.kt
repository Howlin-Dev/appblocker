package com.howlindev.appblocker.di

import com.howlindev.appblocker.permissions.domain.repository.PermissionRepository
import com.howlindev.appblocker.permissions.platform.PermissionNavigator
import com.howlindev.appblocker.permissions.platform.requester.PermissionRequester
import com.howlindev.appblocker.core.domain.repository.InstalledAppsRepository
import com.howlindev.appblocker.data.repository.AndroidPermissionRepository
import com.howlindev.appblocker.data.repository.InstalledAppsRepositoryImpl
import com.howlindev.appblocker.navigation.entry.EntryViewModel
import com.howlindev.appblocker.presentation.block.BlockViewModel
import com.howlindev.appblocker.presentation.root.RootViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single { PermissionNavigator(androidContext()) }
    single { PermissionRequester(get()) }

    single<PermissionRepository> { AndroidPermissionRepository(androidContext()) }
    single<InstalledAppsRepository> { InstalledAppsRepositoryImpl(androidContext()) }

    viewModelOf(::RootViewModel)
    viewModelOf(::EntryViewModel)
    viewModelOf(::BlockViewModel)

    factory { KoinWorkerFactory() }
}

