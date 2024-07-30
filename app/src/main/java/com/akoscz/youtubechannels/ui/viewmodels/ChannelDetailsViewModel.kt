package com.akoscz.youtubechannels.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.paging.PlaylistsPagingSource
import com.akoscz.youtubechannels.data.paging.VideosPagingSource
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

    private val _videos = MutableStateFlow<PagingData<Video>>(PagingData.empty())
    val videos: StateFlow<PagingData<Video>> = _videos.asStateFlow()

    private val _playlists = MutableStateFlow<PagingData<Playlist>>(PagingData.empty())
    val playlists: StateFlow<PagingData<Playlist>> = _playlists.asStateFlow()

    fun fetchChannelDetails(channelId: String) {
        println("Fetching channel details for $channelId")
        viewModelScope.launch {
            _channelDetails.value = repository.getChannelDetails(channelId)
            val uploadsPlaylistId = _channelDetails.value?.uploadsPlaylistId
            if (uploadsPlaylistId != null) {
                fetchPlaylistVideos(uploadsPlaylistId)
            }
        }
    }

    private fun fetchPlaylistVideos(playlistId: String) {
        println("Fetching videos for playlist $playlistId")
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = 20)) { // Use Pager for pagination
                VideosPagingSource(repository, playlistId)
            }.flow.cachedIn(viewModelScope).collect { pagingData ->
                _videos.value = pagingData
            }
        }
    }

    fun fetchChannelPlaylists(channelId: String) {
        println("Fetching playlists for channel $channelId")
        viewModelScope.launch{
            Pager(PagingConfig(pageSize = 20)) { // Use Pager for pagination
                PlaylistsPagingSource(repository, channelId)
            }.flow.cachedIn(viewModelScope).collect { pagingData ->
                _playlists.value = pagingData
            }
        }
    }
}
