package com.akoscz.youtubechannels.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.repository.ChannelsRepository

class PlaylistsPagingSource(
    private val repository: ChannelsRepository,
    private val channelId: String
) : PagingSource<String, Playlist>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Playlist> {
        val pageToken = params.key
        return try {
            val (playlists, nextPageToken) = repository.getChannelPlaylists(channelId, pageToken)
            LoadResult.Page(
                data = playlists,
                prevKey = null, // No previous page in this case
                nextKey = nextPageToken
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Playlist>): String? {
        // Not needed for initial load, return null
        return null
    }
}