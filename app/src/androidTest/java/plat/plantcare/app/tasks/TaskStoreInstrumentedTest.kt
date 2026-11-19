package plat.plantcare.app.tasks

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
import plat.plantcare.app.core.data.models.Task
import plat.plantcare.app.core.data.models.TaskAction
import plat.plantcare.app.core.data.tasks.*
import java.time.LocalDate
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class TaskStoreInstrumentedTest {
    @Test fun offlineCompletionSurvivesRestartAndDoesNotDuplicateTheNextWatering() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "task-test-" + UUID.randomUUID() + ".db"
        var db = Room.databaseBuilder(context, AppDatabase::class.java, name).build()
        try {
            val store = LocalTaskStore(db)
            val day = LocalDate.of(2026, 11, 12)
            store.create("alice", Task("first", "plant", day.minusDays(4), TaskAction.WATER, "Riego", false, null, 7))
            store.complete("alice", "first", day.minusDays(1), today = day)
            store.complete("alice", "first", day, today = day)
            val rows = store.dao.observe("alice").first()
            assertEquals(2, rows.size)
            assertEquals(day.minusDays(1).toString(), rows.first { it.completed }.completedOn)
            assertEquals(day.plusDays(6).toString(), rows.first { !it.completed }.dueDate)
            assertTrue(rows.all { it.pending })
            assertTrue(store.dao.observe("bob").first().isEmpty())
            assertEquals(0, store.dao.pendingWaterings("alice", day.toString()))
            db.close()
            db = Room.databaseBuilder(context, AppDatabase::class.java, name).build()
            assertEquals(2, db.taskDao().observe("alice").first().size)
            assertEquals(2, db.taskDao().writes("alice").size)
        } finally { db.close(); context.deleteDatabase(name) }
    }
    @Test fun dailyCountIncludesOnlyDueWateringsForTheCurrentUser() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val store = LocalTaskStore(db)
            val today = LocalDate.now()
            val task = Task("today", "plant", today, TaskAction.WATER, "Riego", false, null)
            store.create("alice", task)
            store.create("alice", task.copy(id = "overdue", dueDate = today.minusDays(1)))
            store.create("alice", task.copy(id = "future", dueDate = today.plusDays(1)))
            store.create("alice", task.copy(id = "prune", action = TaskAction.PRUNE))
            store.create("bob", task)
            store.complete("alice", "today", today)
            assertEquals(1, store.dao.pendingWaterings("alice", today.toString()))
            assertEquals(1, store.dao.pendingWaterings("bob", today.toString()))
            try { store.complete("bob", "today", today.plusDays(1)); fail("Future dates must be rejected") }
            catch (_: IllegalArgumentException) { }
        } finally { db.close() }
    }
    @Test fun migrationKeepsTheExistingUserProfile() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "task-migration-" + UUID.randomUUID() + ".db"
        context.openOrCreateDatabase(name, 0, null).use { old ->
            old.execSQL("CREATE TABLE users (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, location TEXT NOT NULL, imageUrl TEXT NOT NULL, streakDays INTEGER NOT NULL, lastStreakDate TEXT NOT NULL)")
            old.execSQL("INSERT INTO users VALUES ('alice', 'Alice', '', '', 2, '')")
            old.version = 2
        }
        val db = Room.databaseBuilder(context, AppDatabase::class.java, name).addMigrations(*TASKS_AND_PLANTS_MIGRATIONS).build()
        try {
            assertEquals("Alice", db.userDAO().observeUser("alice").first()?.name)
            assertTrue(db.taskDao().observe("alice").first().isEmpty())
        } finally { db.close(); context.deleteDatabase(name) }
    }
}
