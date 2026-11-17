package plat.plantcare.app.core.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Keep the installed profiles, plants and task history from either branch. */
val TASKS_AND_PLANTS_MIGRATIONS: Array<Migration> = intArrayOf(1, 2, 3, 5).map { version ->
    object : Migration(version, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            if (version == 1) {
                db.execSQL("CREATE TABLE users_updated (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, location TEXT NOT NULL, imageUrl TEXT NOT NULL, streakDays INTEGER NOT NULL, lastStreakDate TEXT NOT NULL)")
                db.execSQL("INSERT INTO users_updated SELECT id, name, location, imageUrl, streakDays, '' FROM users")
                db.execSQL("DROP TABLE users")
                db.execSQL("ALTER TABLE users_updated RENAME TO users")
            }
            db.execSQL("CREATE TABLE IF NOT EXISTS plants (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, imageUrl TEXT, location TEXT NOT NULL, birthDate TEXT, ownerId TEXT NOT NULL, referenceId TEXT, favorite INTEGER NOT NULL)")
            db.execSQL("CREATE TABLE IF NOT EXISTS tasks (userId TEXT NOT NULL, id TEXT NOT NULL, plantId TEXT NOT NULL, dueDate TEXT NOT NULL, action TEXT NOT NULL, description TEXT NOT NULL, completed INTEGER NOT NULL, completedOn TEXT, recurrenceDays INTEGER, parentTaskId TEXT, plantName TEXT NOT NULL, plantImageUrl TEXT NOT NULL, plantLocation TEXT NOT NULL, pending INTEGER NOT NULL, PRIMARY KEY(userId, id))")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_userId_dueDate ON tasks (userId, dueDate)")
            db.execSQL("CREATE TABLE IF NOT EXISTS task_writes (userId TEXT NOT NULL, operationId TEXT NOT NULL, taskId TEXT NOT NULL, kind TEXT NOT NULL, completedOn TEXT, PRIMARY KEY(userId, operationId))")
        }
    }
}.toTypedArray<Migration>() + intArrayOf(6, 7, 8, 9, 10).map { version ->
    object : Migration(version, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS tasks (userId TEXT NOT NULL, id TEXT NOT NULL, plantId TEXT NOT NULL, dueDate TEXT NOT NULL, action TEXT NOT NULL, description TEXT NOT NULL, completed INTEGER NOT NULL, completedOn TEXT, recurrenceDays INTEGER, parentTaskId TEXT, plantName TEXT NOT NULL, plantImageUrl TEXT NOT NULL, plantLocation TEXT NOT NULL, pending INTEGER NOT NULL, PRIMARY KEY(userId, id))")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_userId_dueDate ON tasks (userId, dueDate)")
            db.execSQL("CREATE TABLE IF NOT EXISTS task_writes (userId TEXT NOT NULL, operationId TEXT NOT NULL, taskId TEXT NOT NULL, kind TEXT NOT NULL, completedOn TEXT, PRIMARY KEY(userId, operationId))")
            db.execSQL("CREATE TABLE IF NOT EXISTS plant_characteristics (plantId TEXT NOT NULL PRIMARY KEY, lightExposure INTEGER NOT NULL, wateringFrequencyDays INTEGER NOT NULL, waterAmount INTEGER NOT NULL, humidityLevel INTEGER NOT NULL, currentClimate INTEGER NOT NULL, potSize INTEGER NOT NULL, potType TEXT NOT NULL, substrate TEXT NOT NULL, attentionLevel INTEGER NOT NULL)")
            db.execSQL("CREATE TABLE IF NOT EXISTS plant_considerations (plantId TEXT NOT NULL PRIMARY KEY, wateringGuide TEXT NOT NULL, recommendedLocationAndLight TEXT NOT NULL, nutrition TEXT NOT NULL, sprayingAndCleaning TEXT NOT NULL)")
        }
    }
}.toTypedArray<Migration>()
