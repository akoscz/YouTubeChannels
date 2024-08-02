package com.akoscz.youtubechannels.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akoscz.youtubechannels.data.models.room.Channel
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channel: Channel)

    @Delete
    suspend fun delete(channel: Channel)

    @Query("SELECT * FROM channels")
    fun getAllChannels(): Flow<List<Channel>>

    @Query("UPDATE channels SET channelDetailsId = :detailsId WHERE id = :channelId")
    suspend fun updateChannelDetailsId(channelId: String, detailsId: String)

    @Query("SELECT EXISTS(SELECT * FROM channels WHERE id = :id)")
    fun isChannelFollowed(id: String): Boolean
}