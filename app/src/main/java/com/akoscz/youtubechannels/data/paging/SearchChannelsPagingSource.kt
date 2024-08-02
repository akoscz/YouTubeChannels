package com.akoscz.youtubechannels.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import retrofit2.HttpException

/**
 * PagingSource for searching channels.
 *
 * Used to handle pagination of the search results.
 *
 * Uses the ChannelsRepository to fetch the channels data either from the database or the network.
 */
class SearchChannelsPagingSource(
    private val channelsRepository: ChannelsRepository,
    private val query: String
) : PagingSource<String, Channel>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Channel> {
        val pageToken = params.key // Get the next page token
        println("SearchChannelsPagingSource load --> query: $query, pageToken: $pageToken, maxResults: ${params.loadSize}")
        return try {
            println("SearchChannelsPagingSource.load() - Fetching data...") // Log loading start
            val (channelsListResponse, nextPageToken) = channelsRepository.searchChannels(query, pageToken, params.loadSize)
            println("SearchChannelsPagingSource.load() - Data fetched successfully.") // Log loading success
            LoadResult.Page(
                data = channelsListResponse,
                prevKey = pageToken,
                nextKey = nextPageToken
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
        get() = (channelsRepository.isMockApi())

    override fun getRefreshKey(state: PagingState<String, Channel>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}