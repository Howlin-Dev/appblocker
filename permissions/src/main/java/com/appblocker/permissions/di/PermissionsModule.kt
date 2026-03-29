package com.appblocker.permissions.di

import com.appblocker.permissions.presentation.PermissionsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val permissionsModule = module {
    viewModelOf(::PermissionsViewModel)
}