package com.howlindev.appblocker.schedule.di

import com.howlindev.appblocker.schedule.data.repository.ScheduleRepositoryImpl
import com.howlindev.appblocker.schedule.domain.repository.ScheduleRepository
import com.howlindev.appblocker.schedule.domain.usecase.DeleteScheduleEventUseCase
import com.howlindev.appblocker.schedule.domain.usecase.GetAllScheduleEventsUseCase
import com.howlindev.appblocker.schedule.domain.usecase.GetScheduleEventsByProfileIdUseCase
import com.howlindev.appblocker.schedule.domain.usecase.SaveScheduleEventUseCase
import com.howlindev.appblocker.schedule.presentation.SchedulerViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val scheduleModule = module {
    viewModelOf(::SchedulerViewModel)

    single { ScheduleRepositoryImpl(get()) } bind ScheduleRepository::class

    factoryOf(::GetAllScheduleEventsUseCase)
    factoryOf(::GetScheduleEventsByProfileIdUseCase)
    factoryOf(::SaveScheduleEventUseCase)
    factoryOf(::DeleteScheduleEventUseCase)
}
