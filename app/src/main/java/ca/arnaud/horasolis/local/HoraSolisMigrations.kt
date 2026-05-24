package ca.arnaud.horasolis.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `alarm_ringing` (
                `id` INTEGER NOT NULL,
                `number` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `location` (
                `id` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `latitude` REAL NOT NULL,
                `longitude` REAL NOT NULL,
                `zoneId` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `alarm` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `label` TEXT,
                `time` TEXT NOT NULL,
                `enabled` INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `alarm_ringing_new` (
                `id` INTEGER NOT NULL,
                `alarmId` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL("INSERT INTO `alarm_ringing_new` SELECT `id`, `number` FROM `alarm_ringing`")
        db.execSQL("DROP TABLE `alarm_ringing`")
        db.execSQL("ALTER TABLE `alarm_ringing_new` RENAME TO `alarm_ringing`")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `alarm` ADD COLUMN `onForWeekDays` TEXT")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `alarm` ADD COLUMN `onTimeDate` TEXT")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `current_location` (
                `id` INTEGER NOT NULL,
                `locationId` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT OR IGNORE INTO `current_location` (`id`, `locationId`)
            SELECT 1, `id` FROM `location` LIMIT 1
            """.trimIndent()
        )
    }
}

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

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `alarm_schedule` (
                `alarmId` INTEGER NOT NULL,
                `scheduledDateTime` TEXT NOT NULL,
                PRIMARY KEY(`alarmId`)
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `selected_times`")
        db.execSQL("DROP TABLE IF EXISTS `schedule_settings`")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `alarm` ADD COLUMN `soundUri` TEXT DEFAULT NULL")
    }
}
