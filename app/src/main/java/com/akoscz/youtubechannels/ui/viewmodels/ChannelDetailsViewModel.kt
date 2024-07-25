package com.akoscz.youtubechannels.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
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

    fun fetchChannelDetails(channelId: String) {
        viewModelScope.launch {
            _channelDetails.value = repository.getChannelDetails(channelId)
        }
    }
}
