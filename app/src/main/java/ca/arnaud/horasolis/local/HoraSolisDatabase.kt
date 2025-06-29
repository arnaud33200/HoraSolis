package ca.arnaud.horasolis.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SelectedTimeEntity::class,
        ScheduleSettingsEntity::class,
        AlarmRingingEntity::class,
    ],
    version = 1
)
abstract class HoraSolisDatabase : RoomDatabase() {

    companion object {

        fun createDatabase(context: Context): HoraSolisDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = HoraSolisDatabase::class.java,
                name = "hora_solis.db",
            ).build()
        }
    }

    abstract fun settingsWithTimesDao(): SettingsWithTimesDao
    abstract fun alarmDao(): AlarmDao
}
