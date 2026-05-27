package com.howlindev.appblocker

import android.app.Application
import androidx.work.Configuration
import com.howlindev.appblocker.permissions.di.permissionsModule
import com.howlindev.appblocker.core.di.coreModule
import com.howlindev.appblocker.di.appModule
import com.howlindev.appblocker.di.dbModule
import com.howlindev.appblocker.profiles.di.profilesModule
import com.howlindev.appblocker.settings.di.settingsModule
import com.howlindev.appblocker.timer.di.timerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.core.context.startKoin
import org.koin.core.component.get
import org.koin.core.context.GlobalContext

class AppBlockerApplication : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(GlobalContext.get().get<KoinWorkerFactory>())
            .build()

    override fun onCreate() {
        startKoin {
            androidLogger()
            androidContext(this@AppBlockerApplication)
            modules(
                appModule,
                dbModule,
                coreModule,
                profilesModule,
                permissionsModule,
                timerModule,
                settingsModule,
            )
        }
        super.onCreate()
    }
}

