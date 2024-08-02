package com.akoscz.youtubechannels.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.paging.SearchChannelsPagingSource
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _followingChannels = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val followingChannels: StateFlow<Map<String, Boolean>> = _followingChannels.asStateFlow()

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
        }
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun followChannel(channel: Channel) {
        viewModelScope.launch {
            channelsRepository.followChannel(channel)
            _followingChannels.update { currentMap ->
                currentMap.toMutableMap().apply { // Create a temporary mutable copy within update
                    this[channel.id] = true
                }
            }
        }
    }

    fun checkFollowingStatusForLazyItems(lazyPagingItems: LazyPagingItems<Channel>) {
        viewModelScope.launch {
            for (index in 0 until lazyPagingItems.itemCount) {
                val channel = lazyPagingItems[index]
                channel?.let {
                    _followingChannels.update { currentMap ->
                        currentMap.toMutableMap().apply { // Create a temporary mutable copy within update
                            this[it.id] = channelsRepository.isFollowing(it)
                        }
                    }
                }
            }
        }
    }
}
