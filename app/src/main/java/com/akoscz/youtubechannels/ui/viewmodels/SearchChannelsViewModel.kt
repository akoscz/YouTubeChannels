package com.akoscz.youtubechannels.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.akoscz.youtubechannels.data.models.Channel
import com.akoscz.youtubechannels.data.models.SearchItem
import com.akoscz.youtubechannels.di.MockYoutubeApi
import com.akoscz.youtubechannels.di.RealYoutubeApi
import com.akoscz.youtubechannels.data.network.YoutubeDataSource
import com.akoscz.youtubechannels.data.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchChannelsViewModel @Inject constructor(
    @RealYoutubeApi private val realYoutubeApiRepository: YoutubeDataSource,
    @MockYoutubeApi private val mockYoutubeApiRepository: YoutubeDataSource,
    private val channelRepository: ChannelRepository
) : ViewModel() {
    internal var useMockData by mutableStateOf(true)
    private val _searchQuery = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<Flow<PagingData<SearchItem>>?>(null)
    var searchResults = _searchResults.asStateFlow()

    fun searchChannels(query: String) {
        viewModelScope.launch {
            delay(300)
            _searchQuery.value = query
            val repository = if (useMockData) mockYoutubeApiRepository else realYoutubeApiRepository
            _searchResults.value = repository.searchChannels(_searchQuery.value, this)
            println("Search results updated: ${_searchResults.value}")
            searchResults = _searchResults.asStateFlow()
        }
    }

    fun toggleDataSource() {
        useMockData = !useMockData
    }

    fun subscribeToChannel(channel: Channel) {
        viewModelScope.launch {
            channelRepository.subscribeToChannel(channel)
        }
    }
}
