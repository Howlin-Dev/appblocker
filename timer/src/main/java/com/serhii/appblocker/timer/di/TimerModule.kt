package com.serhii.appblocker.timer.di

import com.serhii.appblocker.core.domain.repository.TimerRepository
import com.serhii.appblocker.timer.data.datastore.TimerDataStore
import com.serhii.appblocker.timer.data.repository.TimerRepositoryImpl
import com.serhii.appblocker.timer.data.scheduler.TimerScheduler
import com.serhii.appblocker.timer.data.scheduler.WorkManagerTimerScheduler
import com.serhii.appblocker.timer.data.worker.TimerWorker
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
            get(), // BlockRepository
            get()  // TimerDataStore
        )
    }
}