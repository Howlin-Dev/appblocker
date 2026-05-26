package com.howlindev.appblocker.timer.di

import com.howlindev.appblocker.core.domain.repository.TimerRepository
import com.howlindev.appblocker.timer.data.datastore.TimerDataStore
import com.howlindev.appblocker.timer.data.repository.TimerRepositoryImpl
import com.howlindev.appblocker.timer.data.scheduler.TimerScheduler
import com.howlindev.appblocker.timer.data.scheduler.WorkManagerTimerScheduler
import com.howlindev.appblocker.timer.data.worker.TimerWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val timerModule = module {

    single { TimerDataStore(androidContext()) }

    single<TimerScheduler> {
        WorkManagerTimerScheduler(androidContext())
    }

    single<TimerRepository> {
        TimerRepositoryImpl(get(), get(), get())
    }

    worker {
        TimerWorker(
            get(),
            get(),
            get(),
            get(),
        )
    }
}

