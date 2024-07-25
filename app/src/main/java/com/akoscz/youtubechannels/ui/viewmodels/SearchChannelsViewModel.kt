package com.akoscz.youtubechannels.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.akoscz.youtubechannels.data.models.api.SearchItem
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.network.SearchChannelsDataSource
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchChannelsViewModel @Inject constructor(
    private val searchChannelsDataSource: SearchChannelsDataSource,
    private val channelsRepository: ChannelsRepository,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<Flow<PagingData<SearchItem>>?>(null)
    var searchResults = _searchResults.asStateFlow()

    fun searchChannels(query: String) {
        viewModelScope.launch {
            delay(300)
            _searchQuery.value = query
            _searchResults.value = searchChannelsDataSource.searchChannels(_searchQuery.value, this)
            println("Search results updated: ${_searchResults.value}")
            searchResults = _searchResults.asStateFlow()
        }
    }

    fun subscribeToChannel(searchItem: SearchItem) {
        val channel = Channel(
            id = searchItem.id.channelId,
            title = searchItem.snippet.title,
            description = searchItem.snippet.description,
            thumbnailDefaultUrl = searchItem.snippet.thumbnails.default.url,
            thumbnailMediumUrl = searchItem.snippet.thumbnails.medium.url,
            thumbnailHighUrl = searchItem.snippet.thumbnails.high.url,
            channelDetailsId = ""
        )
        viewModelScope.launch {
            channelsRepository.subscribeToChannel(channel)
        }
    }
}
