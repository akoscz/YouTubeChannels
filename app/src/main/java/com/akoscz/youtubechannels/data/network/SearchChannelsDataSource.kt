package com.akoscz.youtubechannels.data.network

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.akoscz.youtubechannels.data.models.api.SearchItem
import com.akoscz.youtubechannels.data.paging.SearchChannelsPagingSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

open class SearchChannelsDataSource @Inject constructor(
    @ApplicationContext private var context: Context,
    private var youtubeApiService: YoutubeApiService
) {

    open fun searchChannels(query: String, coroutineScope: CoroutineScope): Flow<PagingData<SearchItem>> {
        println("SearchChannelsDataSource.searchChannels($query)")
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { SearchChannelsPagingSource(context, youtubeApiService, query) }
        ).flow
    }

    // Add more methods to interact with other API endpoints
}