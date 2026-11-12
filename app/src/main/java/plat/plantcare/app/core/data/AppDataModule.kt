package plat.plantcare.app.core.data

import plat.plantcare.app.core.data.tasks.USER_MIGRATION_1_2
import plat.plantcare.app.core.data.tasks.TASK_MIGRATION_2_3
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import plat.plantcare.app.core.preferences.DataStorePreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDataModule {

    @Provides @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ) : AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "plantcare.db"
        ).addMigrations(USER_MIGRATION_1_2, TASK_MIGRATION_2_3).build()

    @Provides @Singleton
    fun providePreferences(
        @ApplicationContext context: Context
    ) : DataStorePreferences =
        DataStorePreferences(context)
}
