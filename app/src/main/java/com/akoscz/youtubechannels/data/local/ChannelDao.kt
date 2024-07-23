package com.akoscz.youtubechannels.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akoscz.youtubechannels.data.models.Channel
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channel: Channel)

    @Delete
    suspend fun delete(channel: Channel)

    @Query("SELECT * FROM Channel")
    fun getAllChannels(): Flow<List<Channel>>
}