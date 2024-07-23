package com.akoscz.youtubechannels.di

import android.content.Context
import androidx.room.Room
import com.akoscz.youtubechannels.data.local.AppDatabase
import com.akoscz.youtubechannels.data.local.ChannelDao
import com.akoscz.youtubechannels.data.repository.ChannelRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// In your Hilt module (e.g., AppModule)
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideChannelDao(database: AppDatabase): ChannelDao {
        return database.channelDao()
    }

    @Provides
    fun provideChannelRepository(channelDao: ChannelDao): ChannelRepository {
        return ChannelRepository(channelDao)
    }
}