package com.akoscz.youtubechannels.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akoscz.youtubechannels.data.models.Channel

@Database(entities = [Channel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
}