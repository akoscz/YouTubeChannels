package com.akoscz.youtubechannels

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
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

class SearchChannelsPagingSource(
    private val youtubeApiService: YoutubeApiService,private val query: String
) : PagingSource<String, SearchItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SearchItem> {
        val pageToken = params.key // Get the next page token

        return try {
            println("SearchChannelsPagingSource load --> query: $query, pageToken: $pageToken, maxResults: ${params.loadSize}")
            val response = youtubeApiService.searchChannels(
                query = query,
                apiKey = BuildConfig.API_KEY,
                pageToken = pageToken,
                maxResults = params.loadSize // Use loadSize for items per page
            )

            println("response: $response")

            LoadResult.Page(
                data = response.items,
                prevKey = null, // No previous page for initial load
                nextKey = response.nextPageToken // Pass the next page token
            )
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 403) {
                // Handle 403 Forbidden error (quota exceeded)
                println("Quota Exceeded: ${e.message()}")
                LoadResult.Error(e) // Return LoadResult.Error
            } else {
                // Handle other exceptions
                LoadResult.Error(e)
            }
        }
    }

    // Allow key reuse when using the MockYoutubeApiService
    override val keyReuseSupported: Boolean
        get() = (youtubeApiService is MockYoutubeApiService)

    override fun getRefreshKey(state: PagingState<String, SearchItem>): String? {

        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}

open class YoutubeApiRepository(private val youtubeApiService: YoutubeApiService) {

    open fun searchChannels(query: String, coroutineScope: CoroutineScope): Flow<PagingData<SearchItem>> {
        println("YoutubeApiRepository searchChannels: $query")
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { SearchChannelsPagingSource(youtubeApiService, query) }
        ).flow.cachedIn(coroutineScope)
    }

    // Add more methods to interact with other API endpoints
}
