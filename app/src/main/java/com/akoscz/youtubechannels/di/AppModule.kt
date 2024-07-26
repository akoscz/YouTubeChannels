package com.akoscz.youtubechannels.di

import android.content.Context
import androidx.room.Room
import com.akoscz.youtubechannels.data.db.AppDatabase
import com.akoscz.youtubechannels.data.db.ChannelDao
import com.akoscz.youtubechannels.data.db.ChannelDetailsDao
import com.akoscz.youtubechannels.data.db.AppSettingsManager
import com.akoscz.youtubechannels.data.network.YoutubeApiService
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
        ).fallbackToDestructiveMigrationFrom(1) // Wipe database when migrating from version 1
        .build()
    }

    @Provides
    fun provideChannelDao(database: AppDatabase): ChannelDao {
        return database.channelDao()
    }

    @Provides
    fun provideChannelDetailsDao(database: AppDatabase): ChannelDetailsDao {
        return database.channelDetailsDao()
    }

    @Provides
    fun provideChannelsRepository(
        youtubeApiService: YoutubeApiService,
        channelDao: ChannelDao,
        channelDetailsDao: ChannelDetailsDao): ChannelsRepository {
        return ChannelsRepository(youtubeApiService, channelDao, channelDetailsDao)
    }

    @Provides
    @Singleton
    fun provideAppSettingsManager(@ApplicationContext context: Context): AppSettingsManager {
        return AppSettingsManager(context)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AppSettingsEntryPoint {
        fun getAppSettingsManager(): AppSettingsManager
    }
}