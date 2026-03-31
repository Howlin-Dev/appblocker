package com.serhii.appblocker

import android.app.Application
import androidx.work.Configuration
import com.appblocker.permissions.di.permissionsModule
import com.serhii.appblocker.core.di.coreModule
import com.serhii.appblocker.di.appModule
import com.serhii.appblocker.di.dbModule
import com.serhii.appblocker.profiles.di.profilesModule
import com.serhii.appblocker.timer.di.timerModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.core.context.startKoin

class AppBlockerApplication : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(getKoin().get<KoinWorkerFactory>())
            .build()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AppBlockerApplication)
            modules(
                appModule,
                dbModule,
                coreModule,
                profilesModule,
                permissionsModule,
                timerModule
            )
        }
    }
}
