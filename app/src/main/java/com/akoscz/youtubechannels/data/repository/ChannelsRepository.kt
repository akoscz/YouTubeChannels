package com.akoscz.youtubechannels.data.repository

import com.akoscz.youtubechannels.BuildConfig
import com.akoscz.youtubechannels.data.db.ChannelsDao
import com.akoscz.youtubechannels.data.db.ChannelDetailsDao
import com.akoscz.youtubechannels.data.db.PlaylistsDao
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.models.room.mapToChannel
import com.akoscz.youtubechannels.data.models.room.mapToChannelDetails
import com.akoscz.youtubechannels.data.models.room.mapToPlaylist
import com.akoscz.youtubechannels.data.models.room.mapToVideo
import com.akoscz.youtubechannels.data.models.room.uploadsPlaylist
import com.akoscz.youtubechannels.data.network.MockYoutubeApiService
import com.akoscz.youtubechannels.data.network.YoutubeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository class for managing data operations related to channels.
 *
 * This class provides methods to interact with the local database and the remote API.
 */
class ChannelsRepository @Inject constructor(
    private val youtubeApiService: YoutubeApiService,
    private val channelsDao: ChannelsDao,
    private val channelDetailsDao: ChannelDetailsDao,
    private val playlistsDao: PlaylistsDao
) {
    fun isMockApi(): Boolean {
        // if youtubeApiService instance of MockYoutubeApiService return true
        return youtubeApiService is MockYoutubeApiService
    }

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

    suspend fun getPlaylistVideos(playlistId: String, pageToken: String? = null, maxResults: Int): Pair<List<Video>, String?> {
        println("getPlaylistVideos playlistId: $playlistId pageToken: $pageToken")
        return withContext(Dispatchers.IO) {
//            return@withContext emptyList<Video>() to null
            // 1. Try to fetch from database

            // 2. Fetch from API if not in database
            try {
                val playlistItemsResponse = youtubeApiService.getPlaylistItems(playlistId = playlistId, maxResults = maxResults)
                if (playlistItemsResponse.items.isNotEmpty()) {
                    val videos = playlistItemsResponse.items.map { playlistItem ->
                        println("video id: ${playlistItem.contentDetails.videoId}")
                        playlistItem.contentDetails.videoId
                    }

                    val videoIdsStr = videos.joinToString(",")
                    println("getPlaylistVideos videoIdsStr: $videoIdsStr")
                    val videosResponse = youtubeApiService.getVideos(
                        videoIds = videoIdsStr,
                        maxResults = playlistItemsResponse.pageInfo.resultsPerPage)

                    if (videosResponse.items.isNotEmpty()) {
                        val videosList = videosResponse.items.map { videoItem ->
                            mapToVideo(videoItem)
                        }
                        return@withContext videosList to playlistItemsResponse.nextPageToken
                    }
                    // Return empty list if no video items
                    return@withContext emptyList<Video>() to null
                }
                // Return empty list if no playlist items
                return@withContext emptyList<Video>() to null
            } catch (e: Exception) {
                println("getPlaylistVideos Error: ${e.message}")
                // Handle network or API errors
                return@withContext emptyList<Video>() to null
            }
        }
    }

    suspend fun getChannelDetails(channelId: String): ChannelDetails? {
        println("getChannelDetails channelId: $channelId")
        return withContext(Dispatchers.IO) { // Wrap in withContext
            // 1. Try to fetch from database
            val channelDetailsFromDb = channelDetailsDao.getChannelDetails(channelId)
            if (channelDetailsFromDb != null) {
                return@withContext channelDetailsFromDb // Return if found in database
            }

            // 2. Fetch from API if not in database
            try {
                val channelDetailsResponse = youtubeApiService.getChannelDetails(id = channelId)
                if (channelDetailsResponse.items.isNotEmpty()) {
                    val channelDetailsItem = channelDetailsResponse.items[0]
                    val channelDetails = mapToChannelDetails(channelDetailsItem)
                    channelDetailsDao.insert(channelDetails)
                    channelsDao.updateChannelDetailsId(channelId,channelDetails.id)
                    val uploadsPlaylistId = channelDetails.uploadsPlaylistId
                    val uploadsPlaylist = uploadsPlaylist(
                        id = uploadsPlaylistId,
                        channelId = channelId,
                        title = "Uploads",
                        description = "Videos uploaded to this channel"
                    )
                    playlistsDao.insertPlaylist(uploadsPlaylist)
                    return@withContext channelDetails
                } else {
                    return@withContext null
                }
            } catch (e: Exception) {
                println("getChannelDetails Error: ${e.message}")
                // Handle network or API errors
                return@withContext null
            }
        }
    }

    suspend fun getChannelPlaylists(channelId: String, pageToken: String? = null, maxResults: Int): Pair<List<Playlist>, String?> {
        println("getChannelPlaylists channelId: $channelId pageToken: $pageToken")
        return withContext(Dispatchers.IO) {
            // 1. Try to fetch from database
            if (pageToken == null) { // If it's the first page, check the database
                val playlistsFromDb = playlistsDao.getCustomPlaylists(channelId).firstOrNull()
                if (playlistsFromDb?.isNotEmpty() == true) {
                    return@withContext playlistsFromDb to null // Return if found in database
                }
            }

            // 2. Fetch from API if not in database
            try {
                val response = youtubeApiService.getChannelPlaylists(channelId = channelId, maxResults = maxResults)
                if (response.items.isNotEmpty()) {
                    val playlists = response.items.map { playlistItem ->
                        mapToPlaylist(playlistItem)
                    }
                    playlistsDao.insertAll(playlists)
                    return@withContext playlists to response.nextPageToken
                }
                // Return empty list if no items
                return@withContext emptyList<Playlist>() to null
            } catch (e: Exception) {
                println("getChannelPlaylists Error: ${e.message}")
                // Handle network or API errors
                return@withContext emptyList<Playlist>() to null
            }
        }
    }

    suspend fun searchChannels(query: String, pageToken: String?, maxResults: Int): Pair<List<Channel>, String?> {
        println("searchChannels query: $query, maxResults: $maxResults, pageToken: $pageToken")
        return withContext(Dispatchers.IO) {
            try {
                val response = youtubeApiService.searchChannels(
                    query = query,
                    apiKey = BuildConfig.API_KEY,
                    pageToken = pageToken,
                    maxResults = maxResults
                )
                // println("searchChannels response: $response")
                if (response.items.isNotEmpty()) {
                    val channels = response.items.map { channelItem ->
                        mapToChannel(channelItem)
                    }
                    return@withContext channels to response.nextPageToken
                }
                // Return empty list if no items
                return@withContext emptyList<Channel>() to null
            }
            catch (e: Exception) {
                println("searchChannels Error: ${e.message}")
                // Handle network or API errors
                return@withContext emptyList<Channel>() to null
            }
        }
    }
}