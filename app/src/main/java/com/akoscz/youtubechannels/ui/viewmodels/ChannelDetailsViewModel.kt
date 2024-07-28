package com.akoscz.youtubechannels.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.paging.PlaylistsPagingSource
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelDetailsViewModel @Inject constructor(
    private val repository: ChannelsRepository
) : ViewModel() {

    private val _channelDetails = MutableStateFlow<ChannelDetails?>(null)
    val channelDetails: StateFlow<ChannelDetails?> = _channelDetails.asStateFlow()

    private val _playlists = MutableStateFlow<PagingData<Playlist>>(PagingData.empty()) // Use PagingData
    val playlists: StateFlow<PagingData<Playlist>> = _playlists.asStateFlow()
    private var playlistsNextPageToken: String? = null

    fun fetchChannelDetails(channelId: String) {
        viewModelScope.launch {
            _channelDetails.value = repository.getChannelDetails(channelId)
        }
    }

    fun fetchChannelPlaylists(channelId: String) {
        viewModelScope.launch{
            Pager(PagingConfig(pageSize = 20)) { // Use Pager for pagination
                PlaylistsPagingSource(repository, channelId)
            }.flow.cachedIn(viewModelScope).collect { pagingData ->
                _playlists.value = pagingData
            }
        }
    }

    // Function to load the next page of playlists
    fun loadMorePlaylists(channelId: String) {
        viewModelScope.launch {
            playlistsNextPageToken?.let { token ->
                val (playlists, newNextToken) = repository.getChannelPlaylists(channelId, token)
                // ... (Update your _playlists state with the new playlists) ...
                playlistsNextPageToken = newNextToken
            }
        }
    }
}
