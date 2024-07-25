package com.akoscz.youtubechannels.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.models.room.ChannelDetails

@Database(
    entities = [
        Channel::class,
        ChannelDetails::class
    ],
    version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun channelDetailsDao(): ChannelDetailsDao
}