package ca.arnaud.horasolis.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `solis_day` (
                `cacheKey` TEXT NOT NULL,
                `civilSunriseTime` TEXT NOT NULL,
                `civilSunsetTime` TEXT NOT NULL,
                PRIMARY KEY(`cacheKey`)
            )
            """.trimIndent()
        )
    }
}
