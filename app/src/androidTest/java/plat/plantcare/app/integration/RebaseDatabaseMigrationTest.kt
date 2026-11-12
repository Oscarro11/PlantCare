package plat.plantcare.app.integration

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import plat.plantcare.app.core.data.AppDatabase
import plat.plantcare.app.core.data.TASKS_AND_PLANTS_MIGRATIONS
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class RebaseDatabaseMigrationTest {
    @Test fun max3HistoryAndPendingWritesSurviveTheRebase() = verifyMigration(3)
    @Test fun oscarPlantsAndPhotoReferencesSurviveTheRebase() = verifyMigration(5)

    private fun verifyMigration(version: Int) = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "rebase-migration-" + UUID.randomUUID() + ".db"
        context.openOrCreateDatabase(name, 0, null).use { old ->
            old.execSQL("CREATE TABLE users (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, location TEXT NOT NULL, imageUrl TEXT NOT NULL, streakDays INTEGER NOT NULL, lastStreakDate TEXT NOT NULL)")
            old.execSQL("INSERT INTO users VALUES ('alice', 'Alice', '', '', 2, '')")
            if (version == 5) {
                old.execSQL("CREATE TABLE plants (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, imageUrl TEXT, location TEXT NOT NULL, birthDate TEXT, ownerId TEXT NOT NULL, referenceId TEXT, favorite INTEGER NOT NULL)")
                old.execSQL("INSERT INTO plants VALUES ('plant', 'Monstera', 'content://photos/test', 'Sala', NULL, 'alice', NULL, 1)")
            } else {
                old.execSQL("CREATE TABLE tasks (userId TEXT NOT NULL, id TEXT NOT NULL, plantId TEXT NOT NULL, dueDate TEXT NOT NULL, action TEXT NOT NULL, description TEXT NOT NULL, completed INTEGER NOT NULL, completedOn TEXT, recurrenceDays INTEGER, parentTaskId TEXT, plantName TEXT NOT NULL, plantImageUrl TEXT NOT NULL, plantLocation TEXT NOT NULL, pending INTEGER NOT NULL, PRIMARY KEY(userId, id))")
                old.execSQL("CREATE INDEX index_tasks_userId_dueDate ON tasks (userId, dueDate)")
                old.execSQL("CREATE TABLE task_writes (userId TEXT NOT NULL, operationId TEXT NOT NULL, taskId TEXT NOT NULL, kind TEXT NOT NULL, completedOn TEXT, PRIMARY KEY(userId, operationId))")
                old.execSQL("INSERT INTO tasks VALUES ('alice', 'watering', 'plant', '2026-11-12', 'WATER', 'Riego', 1, '2026-11-12', 7, NULL, 'Monstera', '', 'Sala', 1)")
                old.execSQL("INSERT INTO task_writes VALUES ('alice', 'COMPLETE:watering', 'watering', 'COMPLETE', '2026-11-12')")
            }
            old.version = version
        }
        val database = Room.databaseBuilder(context, AppDatabase::class.java, name)
            .addMigrations(*TASKS_AND_PLANTS_MIGRATIONS).build()
        try {
            assertEquals("Alice", database.userDAO().observeUser("alice").first()?.name)
            if (version == 3) {
                val task = database.taskDao().find("alice", "watering")!!
                assertTrue(task.completed)
                assertEquals("2026-11-12", task.completedOn)
                assertTrue(task.pending)
                assertEquals(1, database.taskDao().writes("alice").size)
            } else {
                database.openHelper.writableDatabase.query("SELECT imageUrl, favorite FROM plants WHERE id = 'plant'").use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("content://photos/test", cursor.getString(0))
                    assertEquals(1, cursor.getInt(1))
                }
                assertTrue(database.taskDao().observe("alice").first().isEmpty())
            }
        } finally { database.close(); context.deleteDatabase(name) }
    }
}
