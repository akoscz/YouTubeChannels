package com.akoscz.youtubechannels.data.repository

import com.akoscz.youtubechannels.data.db.ChannelDao
import com.akoscz.youtubechannels.data.db.ChannelDetailsDao
import com.akoscz.youtubechannels.data.models.api.ChannelDetailsItem
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
        channelDetailsDao.getChannelDetails(channel.id)?.let {
            channelDetailsDao.delete(it)
        }
    }

    fun getSubscribedChannels(): Flow<List<Channel>> = channelDao.getAllChannels()

    fun getAllVideos(): Flow<List<Video>> {
        // empty list
        return flowOf(emptyList())
    }

    private suspend fun insertChannelDetails(channelDetailsItem: ChannelDetailsItem): ChannelDetails {
        val channelDetails = ChannelDetails(
            id = channelDetailsItem.id,
            title = channelDetailsItem.snippet.title,
            description = channelDetailsItem.snippet.description,
            customUrl = channelDetailsItem.snippet.customUrl,
            publishedAt = channelDetailsItem.snippet.publishedAt,
            thumbnailDefaultUrl = channelDetailsItem.snippet.thumbnails.default.url,
            thumbnailDefaultWidth = channelDetailsItem.snippet.thumbnails.default.width,
            thumbnailDefaultHeight = channelDetailsItem.snippet.thumbnails.default.height,
            thumbnailMediumUrl = channelDetailsItem.snippet.thumbnails.medium.url,
            thumbnailMediumWidth = channelDetailsItem.snippet.thumbnails.medium.width,
            thumbnailMediumHeight = channelDetailsItem.snippet.thumbnails.medium.height,
            thumbnailHighUrl = channelDetailsItem.snippet.thumbnails.high.url,
            thumbnailHighWidth = channelDetailsItem.snippet.thumbnails.high.width,
            thumbnailHighHeight = channelDetailsItem.snippet.thumbnails.high.height,
            viewCount = channelDetailsItem.statistics.viewCount,
            subscriberCount = channelDetailsItem.statistics.subscriberCount,
            hiddenSubscriberCount = channelDetailsItem.statistics.hiddenSubscriberCount,
            videoCount = channelDetailsItem.statistics.videoCount,
            likesPlaylistId = channelDetailsItem.contentDetails.relatedPlaylists.likes,
            uploadsPlaylistId = channelDetailsItem.contentDetails.relatedPlaylists.uploads,
            bannerExternalUrl = channelDetailsItem.brandingSettings.image.bannerExternalUrl
        )
        channelDetailsDao.insert(channelDetails)
        return channelDetails
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
                val channelDetails = insertChannelDetails(channelDetailsItem)
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