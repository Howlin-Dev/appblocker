package com.serhii.appblocker

import android.app.Application
import com.appblocker.permissions.di.permissionsModule
import com.serhii.appblocker.core.di.coreModule
import com.serhii.appblocker.di.appModule
import com.serhii.appblocker.di.dbModule
import com.serhii.appblocker.profiles.di.profilesModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppBlockerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AppBlockerApplication)
            modules(appModule, dbModule, coreModule, profilesModule, permissionsModule)
        }
    }
}
