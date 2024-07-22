package com.akoscz.youtubechannels

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class PageInfo(
    val totalResults: Int,
    val resultsPerPage: Int
)
data class SearchItem(
    val kind: String,
    val etag: String,
    val id: Id,
    val snippet: Snippet
)

data class Id(
    val kind: String,
    val channelId: String
)

data class Snippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String,
    val liveBroadcastContent: String,
    val publishTime: String
)

data class Thumbnails(
    val default: Thumbnail,
    val medium: Thumbnail,
    val high: Thumbnail
)

data class Thumbnail(
    val url: String
)

data class ChannelsSearchResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String?, // Can be null if there's no next page
    val regionCode: String,
    val pageInfo: PageInfo,
    val items: List<SearchItem>
)

// API endpoints
interface YoutubeApiService {
    @GET("search")
    suspend fun searchChannels(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "channel",
        @Query("key") apiKey: String,
        @Query("pageToken") pageToken: String? = null,
        @Query("maxResults") maxResults: Int = 10
    ): ChannelsSearchResponse

    // Add more endpoints as needed
}


open class YoutubeApiRepository(private val youtubeApiService: YoutubeApiService) {

    open suspend fun searchChannels(queryStr: String): List<SearchItem> {
        val allResults = mutableListOf<SearchItem>()
        var nextPageToken: String? = null

        do {
            print("Query String: $queryStr, Next Page Token: $nextPageToken")
            val response = withContext(Dispatchers.IO) {
                try {
                    youtubeApiService.searchChannels(
                        query = queryStr,
                        apiKey = BuildConfig.API_KEY,
                        pageToken = nextPageToken,
                        maxResults = 10
                    )
                } catch (e: Exception) {
                    // Handle exceptions (e.g., log error, return empty list)
                    null
                }
            }

            response?.let {
                // print the pageInfo
                println("Page Info: ${it.pageInfo}")
                allResults.addAll(it.items)
                nextPageToken = it.nextPageToken
            }
        } while (nextPageToken != null)

        return allResults
    }

    // Add more methods to interact with other API endpoints
}

// Fake repository for preview
class PreviewYoutubeApiRepository : YoutubeApiRepository(object : YoutubeApiService {
    override suspend fun searchChannels(
        part: String,
        query: String,
        type: String,
        apiKey: String,
        pageToken: String?,
        maxResults: Int
    ): ChannelsSearchResponse {
        TODO("Not implemented")
    }
})