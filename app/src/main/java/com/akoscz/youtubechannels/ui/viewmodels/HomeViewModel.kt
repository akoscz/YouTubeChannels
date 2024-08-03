package com.akoscz.youtubechannels.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val channelsRepository: ChannelsRepository
) : ViewModel() {

    private val _homeVideos: MutableStateFlow<List<Video>> = MutableStateFlow(emptyList())
    val homeVideos: StateFlow<List<Video>> = _homeVideos

    private val _channelDetailsMap: MutableStateFlow<Map<String, ChannelDetails>> = MutableStateFlow(emptyMap())
    val channelDetailsMap: StateFlow<Map<String, ChannelDetails>> = _channelDetailsMap

    private val _sortType = MutableStateFlow(SortType.NEWEST)
    val sortType: StateFlow<SortType> = _sortType

    private val maxVideosPerChannel = 5

    fun fetchHomeVideos() {
        viewModelScope.launch {
            channelsRepository.getHomeVideos(_sortType.value, maxVideosPerChannel).collect { videos ->
                _homeVideos.value = videos
                videos.forEach { video ->
                    if (!_channelDetailsMap.value.containsKey(video.channelId)) {
                        val channelDetails = channelsRepository.getChannelDetails(video.channelId)
                        if (channelDetails != null) {
                            _channelDetailsMap.value += (video.channelId to channelDetails)
                        }
                    }
                }
            }
        }
    }

    fun updateSortType(sortType: SortType) {
        _sortType.value = sortType
        fetchHomeVideos()
    }
}

enum class SortType {
    NEWEST,
    POPULAR,
    OLDEST
}
