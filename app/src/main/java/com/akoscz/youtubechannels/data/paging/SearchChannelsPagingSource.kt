package com.akoscz.youtubechannels.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.akoscz.youtubechannels.BuildConfig
import com.akoscz.youtubechannels.data.models.api.SearchItem
import com.akoscz.youtubechannels.data.network.MockYoutubeApiService
import com.akoscz.youtubechannels.data.network.YoutubeApiService
import retrofit2.HttpException


class SearchChannelsPagingSource(
    private val youtubeApiService: YoutubeApiService, private val query: String
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