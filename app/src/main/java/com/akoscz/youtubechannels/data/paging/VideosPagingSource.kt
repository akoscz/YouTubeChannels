package com.akoscz.youtubechannels.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.repository.ChannelsRepository

class VideosPagingSource(
    private val repository: ChannelsRepository,
    private val playlistId: String
) : PagingSource<String, Video>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Video> {
        val pageToken = params.key
        return try {
            val (videos, nextPageToken) = repository.getPlaylistVideos(playlistId, pageToken)
            LoadResult.Page(
                data = videos,
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

    override fun getRefreshKey(state: PagingState<String, Video>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}