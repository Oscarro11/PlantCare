package plat.plantcare.app.core.data.tasks

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val USER_MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE users_updated (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, location TEXT NOT NULL, imageUrl TEXT NOT NULL, streakDays INTEGER NOT NULL, lastStreakDate TEXT NOT NULL)")
        db.execSQL("INSERT INTO users_updated SELECT id, name, location, imageUrl, streakDays, '' FROM users")
        db.execSQL("DROP TABLE users")
        db.execSQL("ALTER TABLE users_updated RENAME TO users")
    }
}
val TASK_MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS tasks (userId TEXT NOT NULL, id TEXT NOT NULL, plantId TEXT NOT NULL, dueDate TEXT NOT NULL, action TEXT NOT NULL, description TEXT NOT NULL, completed INTEGER NOT NULL, completedOn TEXT, recurrenceDays INTEGER, parentTaskId TEXT, plantName TEXT NOT NULL, plantImageUrl TEXT NOT NULL, plantLocation TEXT NOT NULL, pending INTEGER NOT NULL, PRIMARY KEY(userId, id))")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_userId_dueDate ON tasks (userId, dueDate)")
        db.execSQL("CREATE TABLE IF NOT EXISTS task_writes (userId TEXT NOT NULL, operationId TEXT NOT NULL, taskId TEXT NOT NULL, kind TEXT NOT NULL, completedOn TEXT, PRIMARY KEY(userId, operationId))")
    }
}
