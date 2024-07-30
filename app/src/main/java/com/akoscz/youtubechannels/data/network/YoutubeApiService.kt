package com.akoscz.youtubechannels.data.network

import android.content.Context
import com.akoscz.youtubechannels.BuildConfig
import com.akoscz.youtubechannels.data.models.api.ChannelDetailsResponse
import com.akoscz.youtubechannels.data.models.api.ChannelPlaylistItemsResponse
import com.akoscz.youtubechannels.data.models.api.ChannelPlaylistsResponse
import com.akoscz.youtubechannels.data.models.api.ChannelsSearchResponse
import com.akoscz.youtubechannels.data.models.api.VideosResponse
import com.akoscz.youtubechannels.data.models.room.Video
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import retrofit2.http.GET
import retrofit2.http.Query

// API endpoints
interface YoutubeApiService {

    @GET("search")
    suspend fun searchChannels(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "channel",
        @Query("pageToken") pageToken: String? = null,
        @Query("maxResults") maxResults: Int,
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): ChannelsSearchResponse

    @GET("channels")
    suspend fun getChannelDetails(
        @Query("part") part: String = "snippet,contentDetails,statistics,brandingSettings",
        @Query("id")id: String,
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): ChannelDetailsResponse

    @GET("playlists")
    suspend fun getChannelPlaylists(
        @Query("part") part: String = "snippet,contentDetails,player",
        @Query("channelId") channelId: String,
        @Query("pageToken") pageToken: String? = null,
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): ChannelPlaylistsResponse

    @GET("playlistItems")
    suspend fun getPlaylistItems(
        @Query("part") part: String = "contentDetails",
        @Query("playlistId") playlistId: String,
        @Query("pageToken") pageToken: String? = null,
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): ChannelPlaylistItemsResponse

    @GET("videos")
    suspend fun getVideos(
        @Query("part") part: String = "snippet,contentDetails,statistics,player",
        @Query("id") videoIds: String,
        @Query("maxResults") maxResults: Int,
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): VideosResponse

    // Add more endpoints as needed
}

suspend fun simulateNetworkDelay() {
    println("Simulating network delay...")
    delay(1500) // Introduce a 1.5-second delay (adjust as needed)
}

/**
 * Mock implementation of the YoutubeApiService
 */
class MockYoutubeApiService(@ApplicationContext private val context: Context) : YoutubeApiService {

    override suspend fun searchChannels(
        part: String,
        query: String,
        type: String,
        pageToken: String?,
        maxResults: Int,
        apiKey: String
    ): ChannelsSearchResponse {
        println("MockYoutubeApiService.searchChannels($query) called")
        simulateNetworkDelay()

        try {
            val jsonString = context.assets.open("mock_channels_searchListResponse.json").bufferedReader().use { it.readText() }
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(ChannelsSearchResponse::class.java)
            return adapter.fromJson(jsonString) ?: throw Exception("Failed to parse JSON")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getChannelDetails(
        part: String,
        id: String,
        apiKey: String
    ): ChannelDetailsResponse {
        println("MockYoutubeApiService.getChannelDetails($id) called")
        simulateNetworkDelay()

        try{
            val jsonString = context.assets.open("mock_channel_details_channelListResponse.json").bufferedReader().use { it.readText() }
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(ChannelDetailsResponse::class.java)
            return adapter.fromJson(jsonString) ?: throw Exception("Failed to parse JSON")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getChannelPlaylists(
        part: String,
        channelId: String,
        pageToken: String?,
        apiKey: String
    ): ChannelPlaylistsResponse {
        println("MockYoutubeApiService.getChannelPlaylists($channelId) called")
        simulateNetworkDelay()

        try{
            val jsonString = context.assets.open("mock_channel_playlistListResponse.json").bufferedReader().use { it.readText() }
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(ChannelPlaylistsResponse::class.java)
            return adapter.fromJson(jsonString) ?: throw Exception("Failed to parse JSON")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getPlaylistItems(
        part: String,
        playlistId: String,
        pageToken: String?,
        apiKey: String
    ): ChannelPlaylistItemsResponse {
        println("MockYoutubeApiService.getPlaylistItems($playlistId) called")
        simulateNetworkDelay()

        val mockFilename = if (playlistId.startsWith("UU")) {
            "mock_uploadsPlaylistId_playListItemListResponse.json"
        } else {
            "mock_playlistId_playlistItemListResponse.json"
        }

        try{
            val jsonString = context.assets.open(mockFilename).bufferedReader().use { it.readText() }
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(ChannelPlaylistItemsResponse::class.java)
            return adapter.fromJson(jsonString) ?: throw Exception("Failed to parse JSON")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

    }

    override suspend fun getVideos(
        part: String,
        videoIds: String,
        maxResults: Int,
        apiKey: String
    ): VideosResponse {
        println("MockYoutubeApiService.getVideos($videoIds) maxResults: $maxResults called")
        simulateNetworkDelay()

        try{
            val jsonString = context.assets.open("mock_uploads_videoListResponse.json").bufferedReader().use { it.readText() }
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(VideosResponse::class.java)
            return adapter.fromJson(jsonString) ?: throw Exception("Failed to parse JSON")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
