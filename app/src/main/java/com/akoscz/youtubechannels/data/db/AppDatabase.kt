package com.akoscz.youtubechannels.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.models.room.PlaylistVideoCrossRef
import com.akoscz.youtubechannels.data.models.room.Video

/**
 * Room database definition for the app.
 */
@Database(
    entities = [
        Channel::class,
        ChannelDetails::class,
        Playlist::class,
        PlaylistVideoCrossRef::class,
        Video::class
    ],
    version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelsDao
    abstract fun channelDetailsDao(): ChannelDetailsDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun videosDao(): VideosDao
}