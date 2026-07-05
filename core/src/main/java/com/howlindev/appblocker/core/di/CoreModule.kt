package com.howlindev.appblocker.core.di

import com.howlindev.appblocker.core.data.repository.BlockRepositoryImpl
import com.howlindev.appblocker.core.data.repository.SuggestedWebsitesRepositoryImpl
import com.howlindev.appblocker.core.domain.repository.BlockRepository
import com.howlindev.appblocker.core.domain.repository.SuggestedWebsitesRepository
import com.howlindev.appblocker.core.domain.usecase.ObserveActiveBlockUseCase
import com.howlindev.appblocker.core.domain.usecase.ObserveRemainingTimeUseCase
import org.koin.dsl.module

val coreModule = module {
    single<BlockRepository> { BlockRepositoryImpl(get()) }
    single<SuggestedWebsitesRepository> { SuggestedWebsitesRepositoryImpl(get()) }

    factory { ObserveActiveBlockUseCase(get()) }
    factory { ObserveRemainingTimeUseCase(get()) }
}
