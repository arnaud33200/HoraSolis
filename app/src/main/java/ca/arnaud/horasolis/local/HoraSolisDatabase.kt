package ca.arnaud.horasolis.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        AlarmRingingEntity::class,
        LocationEntity::class,
        AlarmEntity::class,
        CurrentLocationEntity::class,
        SolisDayEntity::class,
        AlarmScheduleEntity::class,
    ],
    version = 11, // add vibrate column to alarm table
)
@TypeConverters(Converters::class)
abstract class HoraSolisDatabase : RoomDatabase() {

    companion object {

        fun createDatabase(context: Context): HoraSolisDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = HoraSolisDatabase::class.java,
                name = "hora_solis.db",
            ).addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8,
                MIGRATION_8_9,
                MIGRATION_9_10,
                MIGRATION_10_11,
            ).build()
        }
    }

    abstract fun alarmDao(): AlarmDao
    abstract fun alarmScheduleDao(): AlarmScheduleDao
    abstract fun locationDao(): LocationDao
    abstract fun solisDayDao(): SolisDayDao
}
