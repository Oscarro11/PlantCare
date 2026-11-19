package plat.plantcare.app.core.photos

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import plat.plantcare.app.core.photos.local.DeviceImageStorage
import plat.plantcare.app.core.photos.local.LocalImageStorage
import plat.plantcare.app.core.photos.remote.FirebasePhotoStorage
import plat.plantcare.app.core.photos.remote.OnlineImageStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PhotosBindingsModule {

    @Binds @Singleton
    abstract fun bindOnlineImageStorage(impl: FirebasePhotoStorage): OnlineImageStorage

    @Binds @Singleton
    abstract fun bindLocalImageStorage(impl: DeviceImageStorage): LocalImageStorage
}