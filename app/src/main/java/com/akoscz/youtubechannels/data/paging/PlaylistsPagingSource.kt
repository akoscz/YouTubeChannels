package com.akoscz.youtubechannels.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.repository.ChannelsRepository

class PlaylistsPagingSource(
    private val repository: ChannelsRepository,
    private val channelId: String
) : PagingSource<String, Playlist>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Playlist> {
        val pageToken = params.key
        val maxResults = params.loadSize
        return try {
            val (playlists, nextPageToken) = repository.getChannelPlaylists(channelId, pageToken, maxResults)
            LoadResult.Page(
                data = playlists,
                prevKey = pageToken,
                nextKey = nextPageToken
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    // Allow key reuse when using the MockYoutubeApiService
    override val keyReuseSupported: Boolean
        get() = (repository.isMockApi())

    override fun getRefreshKey(state: PagingState<String, Playlist>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}