package com.akoscz.youtubechannels.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.paging.SearchChannelsPagingSource
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchChannelsViewModel @Inject constructor(
    private val channelsRepository: ChannelsRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<PagingData<Channel>>(PagingData.empty())
    val searchResults: StateFlow<PagingData<Channel>> = _searchResults.asStateFlow()

    fun searchChannels(query: String) {
        viewModelScope.launch {
            delay(300)
            _searchQuery.value = query
            Pager(
                config = PagingConfig(pageSize = 10),
                pagingSourceFactory = { SearchChannelsPagingSource(channelsRepository, query) }
            ).flow.cachedIn(viewModelScope).collect { pagingData ->
                _searchResults.value = pagingData
            }
            println("Search results updated: ${_searchResults.value}")
        }
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun followChannel(channel: Channel) {
        viewModelScope.launch {
            channelsRepository.followChannel(channel)
        }
    }
}
