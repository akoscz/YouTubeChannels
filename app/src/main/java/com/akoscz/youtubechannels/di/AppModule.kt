package com.akoscz.youtubechannels.di

import android.content.Context
import androidx.room.Room
import com.akoscz.youtubechannels.data.db.AppDatabase
import com.akoscz.youtubechannels.data.db.ChannelsDao
import com.akoscz.youtubechannels.data.db.ChannelDetailsDao
import com.akoscz.youtubechannels.data.db.AppSettingsManager
import com.akoscz.youtubechannels.data.db.PlaylistsDao
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
        )
        .fallbackToDestructiveMigrationFrom(1) // Wipe database when migrating from version 1
        .fallbackToDestructiveMigrationFrom(2) // Wipe database when migrating from version 2
        .build()
    }

    @Provides
    fun provideChannelDao(database: AppDatabase): ChannelsDao {
        return database.channelDao()
    }

    @Provides
    fun provideChannelDetailsDao(database: AppDatabase): ChannelDetailsDao {
        return database.channelDetailsDao()
    }

    @Provides
    fun providePlaylistsDao(database: AppDatabase): PlaylistsDao {
        return database.playlistsDao()
    }

    @Provides
    fun provideChannelsRepository(
        youtubeApiService: YoutubeApiService,
        channelsDao: ChannelsDao,
        channelDetailsDao: ChannelDetailsDao,
        playlistsDao: PlaylistsDao
    ): ChannelsRepository {
        return ChannelsRepository(youtubeApiService, channelsDao, channelDetailsDao, playlistsDao)
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