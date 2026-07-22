package com.howlindev.appblocker.schedule.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.howlindev.appblocker.schedule.data.model.ScheduleEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleEventDao {
    @Query("SELECT * FROM schedule_events WHERE profileId = :profileId")
    fun getEventsByProfileId(profileId: Long): Flow<List<ScheduleEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ScheduleEventEntity)

    @Delete
    suspend fun deleteEvent(event: ScheduleEventEntity)

    @Query("DELETE FROM schedule_events WHERE profileId = :profileId")
    suspend fun deleteEventsByProfileId(profileId: Long)
}
