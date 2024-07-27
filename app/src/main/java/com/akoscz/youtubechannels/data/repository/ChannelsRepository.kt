package com.akoscz.youtubechannels.data.repository

import com.akoscz.youtubechannels.data.db.ChannelsDao
import com.akoscz.youtubechannels.data.db.ChannelDetailsDao
import com.akoscz.youtubechannels.data.db.PlaylistsDao
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.models.room.mapChannelDetailsItem
import com.akoscz.youtubechannels.data.models.room.mapPlaylistsItem
import com.akoscz.youtubechannels.data.network.YoutubeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChannelsRepository @Inject constructor(
    private val youtubeApiService: YoutubeApiService,
    private val channelsDao: ChannelsDao,
    private val channelDetailsDao: ChannelDetailsDao,
    private val playlistsDao: PlaylistsDao
) {
    suspend fun subscribeToChannel(channel: Channel) {
        channelsDao.insert(channel)
    }

    suspend fun unsubscribeFromChannel(channel: Channel) {
        channelsDao.delete(channel)
        channelDetailsDao.getChannelDetails(channel.id)?.let {
            channelDetailsDao.delete(it)
        }
    }

    fun getSubscribedChannels(): Flow<List<Channel>> {
        return channelsDao.getAllChannels()
    }

    fun getAllVideos(): Flow<List<Video>> {
        // empty list
        return flowOf(emptyList())
    }

    suspend fun getChannelDetails(channelId: String): ChannelDetails? {
        return withContext(Dispatchers.IO) { // Wrap in withContext
            // 1. Try to fetch from database
            val channelDetailsFromDb = channelDetailsDao.getChannelDetails(channelId)
            if (channelDetailsFromDb != null) {
                return@withContext channelDetailsFromDb // Return if found in database
            }

            // 2. Fetch from API if not in database
            try {
                val response = youtubeApiService.getChannelDetails(id = channelId)
                if (response.items.isNotEmpty()) {
                    val channelDetailsItem = response.items[0]
                    val channelDetails = mapChannelDetailsItem(channelDetailsItem)
                    channelDetailsDao.insert(channelDetails)
                    channelsDao.updateChannelDetailsId(channelId,channelDetails.id)
                    return@withContext channelDetails
                } else {
                    return@withContext null
                }
            } catch (e: Exception) {
                // Handle network or API errors
                return@withContext null
            }
        }
    }

    suspend fun getChannelVideos(channelId: String): Flow<List<Video>> {
        // 1. Try to fetch from database

        // 2. Fetch from API if not in database
        return flowOf(emptyList())
    }



    suspend fun getChannelPlaylists(channelId: String, pageToken: String? = null): Pair<List<Playlist>, String?> {
        return withContext(Dispatchers.IO) {
            // 1. Try to fetch from database
            if (pageToken == null) { // If it's the first page, check the database
                val playlistsFromDb = playlistsDao.getAllPlaylists(channelId).firstOrNull()
                if (playlistsFromDb?.isNotEmpty() == true) {
                    return@withContext playlistsFromDb to null // Return if found in database
                }
            }


            // 2. Fetch from API if not in database
            try {
                val response = youtubeApiService.getChannelPlaylists(channelId = channelId)
                if (response.items.isNotEmpty()) {
                    val playlists = response.items.map { playlistItem ->
                        mapPlaylistsItem(playlistItem)
                    }
                    playlistsDao.insertAll(playlists)
                    return@withContext playlists to response.nextPageToken
                }
                // Return empty list if no items
                return@withContext emptyList<Playlist>() to null
            } catch (e: Exception) {
                // Handle network or API errors
                return@withContext emptyList<Playlist>() to null
            }
        }
    }
}