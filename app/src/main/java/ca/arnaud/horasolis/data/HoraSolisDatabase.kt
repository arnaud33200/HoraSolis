package ca.arnaud.horasolis.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SelectedTimeEntity::class,
        ScheduleSettingsEntity::class
    ],
    version = 1
)
abstract class HoraSolisDatabase : RoomDatabase() {

    abstract fun selectedTimeDao(): SelectedTimeDao
    abstract fun scheduleSettingsDao(): ScheduleSettingsDao
}
