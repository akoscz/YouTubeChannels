package com.akoscz.youtubechannels.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val repository: ChannelsRepository
) : ViewModel() {

    private val _video = MutableStateFlow<Video?>(null)
    val video: StateFlow<Video?> get() = _video

    fun getVideo(videoId: String) {
        viewModelScope.launch {
            _video.value = repository.getVideoById(videoId)
        }
    }
}