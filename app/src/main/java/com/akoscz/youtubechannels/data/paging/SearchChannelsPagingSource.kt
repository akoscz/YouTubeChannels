package com.akoscz.youtubechannels.data.paging

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.akoscz.youtubechannels.BuildConfig
import com.akoscz.youtubechannels.data.db.FeatureToggleHelper
import com.akoscz.youtubechannels.data.models.api.SearchItem
import com.akoscz.youtubechannels.data.network.MockYoutubeApiService
import com.akoscz.youtubechannels.data.network.YoutubeApiService
import com.akoscz.youtubechannels.data.network.simulateNetworkDelay
import retrofit2.HttpException


class SearchChannelsPagingSource(
    context: Context, private val youtubeApiService: YoutubeApiService, private val query: String
) : PagingSource<String, SearchItem>() {
    private val featureToggleManager = FeatureToggleHelper.getInstance(context)
    private val isMockDataEnabled = featureToggleManager.isMockDataEnabled()


    override suspend fun load(params: LoadParams<String>): LoadResult<String, SearchItem> {
        val pageToken = params.key // Get the next page token
        println("SearchChannelsPagingSource load --> query: $query, pageToken: $pageToken, maxResults: ${params.loadSize}")

        if (isMockDataEnabled) {
            simulateNetworkDelay()
        }

        return try {
            println("SearchChannelsPagingSource.load() - Fetching data...") // Log loading start

            val response = youtubeApiService.searchChannels(
                query = query,
                apiKey = BuildConfig.API_KEY,
                pageToken = pageToken,
                maxResults = params.loadSize // Use loadSize for items per page
            )

            println("SearchChannelsPagingSource.load() - Data fetched successfully.") // Log loadingsuccess
            println("response: $response")

            LoadResult.Page(
                data = response.items,
                prevKey = null, // No previous page for initial load
                nextKey = response.nextPageToken // Pass the next page token
            )
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 403) {
                // Handle 403 Forbidden error (quota exceeded)
                println("SearchChannelsPagingSource.load() - Quota Exceeded: ${e.message()}")
                LoadResult.Error(e) // Return LoadResult.Error
            } else {
                println("SearchChannelsPagingSource.load() - Error loading data: ${e.message}") // Log loading error
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