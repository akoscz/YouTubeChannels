package com.akoscz.youtubechannels.data.repository

import com.akoscz.youtubechannels.data.local.ChannelDao
import com.akoscz.youtubechannels.data.models.Channel
import com.akoscz.youtubechannels.data.models.Video
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ChannelRepository @Inject constructor(private val channelDao: ChannelDao) {
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
}