package plat.plantcare.app.authentication

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import plat.plantcare.app.authentication.remote.AuthApi
import plat.plantcare.app.authentication.remote.FirebaseAuthApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationDataModule {

    @Provides @Singleton
    fun provideAuthApi(): AuthApi = FirebaseAuthApi(Firebase.auth)
}