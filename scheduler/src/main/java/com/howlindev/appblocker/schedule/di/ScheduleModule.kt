package com.howlindev.appblocker.schedule.di

import com.howlindev.appblocker.schedule.presentation.SchedulerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val scheduleModule = module {
    viewModelOf(::SchedulerViewModel)
}
