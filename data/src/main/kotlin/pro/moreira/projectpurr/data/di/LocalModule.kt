package pro.moreira.projectpurr.data.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pro.moreira.projectpurr.data.local.AppDatabase
import pro.moreira.projectpurr.data.local.converter.ImageTypeConverter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideImageTypeConverter(moshi: Moshi) = ImageTypeConverter(moshi)

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        imageTypeConverter: ImageTypeConverter,
    ) = Room.databaseBuilder(context, AppDatabase::class.java, "db")
        .addTypeConverter(imageTypeConverter)
        .build()

    @Provides
    @Singleton
    fun provideBreedDao(db: AppDatabase) = db.breedDao()

    @Provides
    @Singleton
    fun provideRemoteKeyDao(db: AppDatabase) = db.remoteKeyDao()
}