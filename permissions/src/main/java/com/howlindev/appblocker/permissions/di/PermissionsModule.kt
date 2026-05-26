package com.howlindev.appblocker.permissions.di

import com.howlindev.appblocker.permissions.domain.usecase.GetMissingPermissionsUseCase
import com.howlindev.appblocker.permissions.domain.usecase.RequestPermissionUseCase
import com.howlindev.appblocker.permissions.presentation.PermissionsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val permissionsModule = module {
    viewModelOf(::PermissionsViewModel)

    factory { GetMissingPermissionsUseCase(get()) }
    factory { RequestPermissionUseCase(get()) }
}

