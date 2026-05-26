package com.howlindev.appblocker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.howlindev.appblocker.profiles.data.db.ProfileDao
import com.howlindev.appblocker.profiles.data.model.ProfileEntity

@Database(entities = [ProfileEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}

