package com.howlindev.appblocker.di

import androidx.room.Room
import com.howlindev.appblocker.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_blocker_db",
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single { get<AppDatabase>().profileDao() }
}

