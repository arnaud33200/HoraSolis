package ca.arnaud.horasolis.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        SelectedTimeEntity::class,
        ScheduleSettingsEntity::class,
        AlarmRingingEntity::class,
        LocationEntity::class,
        AlarmEntity::class,
    ],
    version = 2
)
@TypeConverters(Converters::class)
abstract class HoraSolisDatabase : RoomDatabase() {

    companion object {

        fun createDatabase(context: Context): HoraSolisDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = HoraSolisDatabase::class.java,
                name = "hora_solis.db",
            ).fallbackToDestructiveMigration().build()
        }
    }

    abstract fun settingsWithTimesDao(): SettingsWithTimesDao
    abstract fun alarmDao(): AlarmDao
    abstract fun locationDao(): LocationDao
}
