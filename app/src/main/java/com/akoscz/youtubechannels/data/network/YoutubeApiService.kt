package com.akoscz.youtubechannels.data.network

import android.content.Context
import com.akoscz.youtubechannels.BuildConfig
import com.akoscz.youtubechannels.data.models.api.ChannelDetailsResponse
import com.akoscz.youtubechannels.data.models.api.ChannelsSearchResponse
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
        @Query("key") apiKey: String,
        @Query("pageToken") pageToken: String? = null,
        @Query("maxResults") maxResults: Int
    ): ChannelsSearchResponse

    @GET("channels")
    suspend fun getChannelDetails(
        @Query("part") part: String = "snippet,contentDetails,statistics,brandingSettings",
        @Query("id")id: String,
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): ChannelDetailsResponse

    // Add more endpoints as needed
}

suspend fun simulateNetworkDelay() {
    println("Simulating network delay...")
    delay(1500) // Introduce a 1.5-second delay (adjust as needed)
}

class MockYoutubeApiService(@ApplicationContext private val context: Context) : YoutubeApiService {

    override suspend fun searchChannels(
        part: String,
        query: String,
        type: String,
        apiKey: String,
        pageToken: String?,
        maxResults: Int
    ): ChannelsSearchResponse {
        println("MockYoutubeApiService.searchChannels($query) called")
        val jsonString = context.assets.open("mock_channels_searchListResponse.json").bufferedReader().use { it.readText() }
        try {
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(ChannelsSearchResponse::class.java)
            return adapter.fromJson(jsonString) ?: throw Exception("Failed to parse JSON")
        }
        catch (e: Exception) {
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
        val jsonString = context.assets.open("mock_channel_details_channelListResponse.json").bufferedReader().use { it.readText() }
        try{
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(ChannelDetailsResponse::class.java)
            return adapter.fromJson(jsonString) ?: throw Exception("Failed to parse JSON")
        }
        catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

}
