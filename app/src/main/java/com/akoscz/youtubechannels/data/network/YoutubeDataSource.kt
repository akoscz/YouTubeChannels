package com.akoscz.youtubechannels.data.network

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.akoscz.youtubechannels.data.models.SearchItem
import com.akoscz.youtubechannels.data.paging.SearchChannelsPagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

open class YoutubeDataSource(private val youtubeApiService: YoutubeApiService) {

    open fun searchChannels(query: String, coroutineScope: CoroutineScope): Flow<PagingData<SearchItem>> {
        println("YoutubeApiRepository searchChannels: $query")
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { SearchChannelsPagingSource(youtubeApiService, query) }
        ).flow.cachedIn(coroutineScope)
    }

    // Add more methods to interact with other API endpoints
}