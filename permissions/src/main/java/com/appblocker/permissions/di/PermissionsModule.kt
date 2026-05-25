package com.appblocker.permissions.di

import com.appblocker.permissions.domain.usecase.GetMissingPermissionsUseCase
import com.appblocker.permissions.domain.usecase.RequestPermissionUseCase
import com.appblocker.permissions.presentation.PermissionsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val permissionsModule = module {
    viewModelOf(::PermissionsViewModel)

    factory { GetMissingPermissionsUseCase(get()) }
    factory { RequestPermissionUseCase(get()) }
}
