package com.serhii.appblocker.core.di

import com.serhii.appblocker.core.data.repository.BlockRepositoryImpl
import com.serhii.appblocker.core.domain.repository.BlockRepository
import com.serhii.appblocker.core.domain.usecase.ObserveActiveBlockUseCase
import com.serhii.appblocker.core.domain.usecase.ObserveRemainingTimeUseCase
import org.koin.dsl.module

val coreModule = module {
    single<BlockRepository> { BlockRepositoryImpl(get()) }

    factory { ObserveActiveBlockUseCase(get()) }
    factory { ObserveRemainingTimeUseCase(get()) }
}
