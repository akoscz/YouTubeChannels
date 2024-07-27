package com.akoscz.youtubechannels.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akoscz.youtubechannels.data.models.room.ChannelDetails

@Dao
interface ChannelDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channelDetails: ChannelDetails)

    @Delete
    suspend fun delete(channelDetails: ChannelDetails)

    @Query("SELECT * FROM channel_details WHERE id = :channelId")
    suspend fun getChannelDetails(channelId: String): ChannelDetails?
}