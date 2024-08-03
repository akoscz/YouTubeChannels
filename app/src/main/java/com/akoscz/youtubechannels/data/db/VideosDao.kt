package com.akoscz.youtubechannels.data.db

import androidx.room.Dao
import androidx.room.Query
import com.akoscz.youtubechannels.data.models.room.Video

@Dao
interface VideosDao {

    @Query("SELECT * FROM videos WHERE channelId = :channelId ORDER BY publishedAt DESC LIMIT :limit")
    fun getNewestVideos(channelId: String, limit: Int): List<Video>

    @Query("SELECT * FROM videos WHERE channelId = :channelId ORDER BY viewCount DESC LIMIT :limit")
    fun getPopularVideos(channelId: String, limit: Int): List<Video>

    @Query("SELECT * FROM videos WHERE channelId = :channelId ORDER BY publishedAt ASC LIMIT :limit")
    fun getOldestVideos(channelId: String, limit: Int): List<Video>
}