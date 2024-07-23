package com.akoscz.youtubechannels.data.repository

import com.akoscz.youtubechannels.data.local.ChannelDao
import com.akoscz.youtubechannels.data.models.Channel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// In the data/repository package
class ChannelRepository @Inject constructor(private val channelDao: ChannelDao) {
    suspend fun subscribeToChannel(channel: Channel) {
        channelDao.insert(channel)
    }

    suspend fun unsubscribeFromChannel(channel: Channel) {
        channelDao.delete(channel)
    }

    fun getSubscribedChannels(): Flow<List<Channel>> = channelDao.getAllChannels()
}