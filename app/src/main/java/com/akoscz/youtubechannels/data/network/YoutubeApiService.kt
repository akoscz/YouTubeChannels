package com.akoscz.youtubechannels.data.network

import android.content.Context
import com.akoscz.youtubechannels.data.models.ChannelsSearchResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
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

    // Add more endpoints as needed
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
        val jsonString = context.assets.open("mock_channels_response.json").bufferedReader().use { it.readText() }
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(ChannelsSearchResponse::class.java)
        return adapter.fromJson(jsonString) ?: throw Exception("Failed to parse JSON")
    }
}
