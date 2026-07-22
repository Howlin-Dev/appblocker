package com.howlindev.appblocker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.howlindev.appblocker.profiles.data.db.ProfileDao
import com.howlindev.appblocker.profiles.data.model.ProfileEntity
import com.howlindev.appblocker.schedule.data.db.ScheduleEventDao
import com.howlindev.appblocker.schedule.data.db.ScheduleTypeConverters
import com.howlindev.appblocker.schedule.data.model.ScheduleEventEntity

@Database(entities = [ProfileEntity::class, ScheduleEventEntity::class], version = 4)
@TypeConverters(ScheduleTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun scheduleEventDao(): ScheduleEventDao
}
