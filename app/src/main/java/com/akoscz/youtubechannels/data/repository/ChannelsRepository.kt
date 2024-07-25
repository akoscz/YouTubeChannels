package com.akoscz.youtubechannels.data.repository

import com.akoscz.youtubechannels.data.db.ChannelDao
import com.akoscz.youtubechannels.data.db.ChannelDetailsDao
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.network.YoutubeApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ChannelsRepository @Inject constructor(
    private val youtubeApiService: YoutubeApiService,
    private val channelDao: ChannelDao,
    private val channelDetailsDao: ChannelDetailsDao
) {
    suspend fun subscribeToChannel(channel: Channel) {
        channelDao.insert(channel)
    }

    suspend fun unsubscribeFromChannel(channel: Channel) {
        channelDao.delete(channel)
    }

    fun getSubscribedChannels(): Flow<List<Channel>> = channelDao.getAllChannels()

    fun getAllVideos(): Flow<List<Video>> {
        // empty list
        return flowOf(emptyList())
    }

    suspend fun getChannelDetails(channelId: String): ChannelDetails?{
        // 1. Try to fetch from database
        val channelDetailsFromDb = channelDetailsDao.getChannelDetails(channelId)
        if (channelDetailsFromDb != null) {
            return channelDetailsFromDb // Return if found in database
        }

        // 2. Fetch from API if not in database
        return try {
            val response = youtubeApiService.getChannelDetails(id = channelId)
            if (response.items.isNotEmpty()) {
                val channelDetailsItem = response.items[0]
                val channelDetails = ChannelDetails(
                    id = channelDetailsItem.id,
                    viewCount = channelDetailsItem.statistics.viewCount,
                    subscriberCount = channelDetailsItem.statistics.subscriberCount,
                    // ... map other properties
                )
                channelDetailsDao.insert(channelDetails) // Save to database
                channelDao.updateChannelDetailsId(channelId, channelDetails.id) // Update channel in database
                channelDetails // Return fetched details
            } else {
                null // Handle case where API returns no results
            }
        } catch (e: Exception) {
            // Handle network or API errors
            null
        }
    }
}