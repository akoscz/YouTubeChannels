package com.akoscz.youtubechannels.data.network

import android.content.Context
import com.akoscz.youtubechannels.data.models.api.ChannelDetailsResponse
import com.akoscz.youtubechannels.data.models.api.ChannelPlaylistItemsResponse
import com.akoscz.youtubechannels.data.models.api.ChannelPlaylistsResponse
import com.akoscz.youtubechannels.data.models.api.ChannelsSearchResponse
import com.akoscz.youtubechannels.data.models.api.VideosResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay


/**
 * Mock implementation of the YoutubeApiService
 * Returns mock responses from JSON files in the assets folder
 * Used to test the app without internet connection
 */
class MockYoutubeApiService(@ApplicationContext private val context: Context) : YoutubeApiService {
    // Create a single Moshi instance for reuse
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    override suspend fun searchChannels(
        part: String,
        query: String,
        type: String,
        pageToken: String?,
        maxResults: Int,
        apiKey: String
    ): ChannelsSearchResponse {
        println("MockYoutubeApiService.searchChannels($query, maxResults: $maxResults, pageToken: $pageToken) called")
        simulateNetworkDelay()

        return loadMockResponse(
            filename = "mock_channels_searchListResponse.json",
            responseClass = ChannelsSearchResponse::class.java)
    }

    override suspend fun getChannelDetails(
        part: String,
        id: String,
        apiKey: String
    ): ChannelDetailsResponse {
        println("MockYoutubeApiService.getChannelDetails($id) called")
        simulateNetworkDelay()

        return loadMockResponse(
            filename = "mock_channel_details_channelListResponse.json",
            responseClass = ChannelDetailsResponse::class.java)
    }

    override suspend fun getChannelPlaylists(part: String,
                                             channelId: String,
                                             pageToken: String?,
                                             maxResults: Int,
                                             apiKey: String
    ): ChannelPlaylistsResponse {
        println("MockYoutubeApiService.getChannelPlaylists($channelId, maxResults: $maxResults, pageToken: $pageToken) called")
        simulateNetworkDelay()

        return loadMockResponse(
            filename = "mock_channel_playlistListResponse.json",
            responseClass = ChannelPlaylistsResponse::class.java)
    }

    override suspend fun getPlaylistItems(
        part: String,
        playlistId: String,
        pageToken: String?,
        maxResults: Int,
        apiKey: String
    ): ChannelPlaylistItemsResponse {
        println("MockYoutubeApiService.getPlaylistItems($playlistId, maxResults: $maxResults, pageToken: $pageToken) called")
        simulateNetworkDelay()

        val mockFilename = if (playlistId.startsWith("UU")) {
            "mock_uploadsPlaylistId_playListItemListResponse.json"
        } else {
            "mock_playlistId_playlistItemListResponse.json"
        }

        return loadMockResponse(
            filename = mockFilename,
            responseClass = ChannelPlaylistItemsResponse::class.java)
    }

    override suspend fun getVideos(
        part: String,
        videoIds: String,
        maxResults: Int,
        apiKey: String
    ): VideosResponse {
        println("MockYoutubeApiService.getVideos($videoIds, maxResults: $maxResults) called")
        simulateNetworkDelay()

        return loadMockResponse(
            filename = "mock_uploads_videoListResponse.json",
            responseClass = VideosResponse::class.java)
    }

    // Helper function to load and parse mock responses
    private fun <T> loadMockResponse(filename: String, responseClass: Class<T>): T {
        return try {
            context.assets.open(filename).bufferedReader().use { it.readText() }.let { jsonString ->
                moshi.adapter(responseClass).fromJson(jsonString) ?: throw Exception("Failed to parse JSON")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private suspend fun simulateNetworkDelay() {
        println("Simulating network delay...")
        delay(1500) // Introduce a 1.5-second delay (adjust as needed)
    }
}