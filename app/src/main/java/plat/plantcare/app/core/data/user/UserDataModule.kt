package plat.plantcare.app.core.data.user

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import plat.plantcare.app.core.data.AppDatabase
import plat.plantcare.app.core.data.user.local.UserDAO
import plat.plantcare.app.core.data.user.remote.FirestoreUserApi
import plat.plantcare.app.core.data.user.remote.UserApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserDataModule {

    @Provides @Singleton
    fun provideUserDao(database: AppDatabase): UserDAO =
        database.userDAO()

    @Provides @Singleton
    fun provideUserApi(firestore: FirebaseFirestore): UserApi = FirestoreUserApi(firestore)
}