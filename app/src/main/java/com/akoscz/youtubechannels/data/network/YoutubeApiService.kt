package com.akoscz.youtubechannels.data.network

import com.akoscz.youtubechannels.BuildConfig
import com.akoscz.youtubechannels.data.models.api.ChannelDetailsResponse
import com.akoscz.youtubechannels.data.models.api.ChannelPlaylistItemsResponse
import com.akoscz.youtubechannels.data.models.api.ChannelPlaylistsResponse
import com.akoscz.youtubechannels.data.models.api.ChannelsSearchResponse
import com.akoscz.youtubechannels.data.models.api.VideosResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for the YouTube Data API v3.
 * API Endpoints are defined here.
 */
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
        @Query("maxResults") maxResults: Int,
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): ChannelPlaylistsResponse

    @GET("playlistItems")
    suspend fun getPlaylistItems(
        @Query("part") part: String = "contentDetails",
        @Query("playlistId") playlistId: String,
        @Query("pageToken") pageToken: String? = null,
        @Query("maxResults") maxResults: Int,
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


