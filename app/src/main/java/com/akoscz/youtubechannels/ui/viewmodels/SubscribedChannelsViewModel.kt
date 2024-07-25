package com.akoscz.youtubechannels.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscribedChannelsViewModel @Inject constructor(
    private val channelsRepository: ChannelsRepository
) : ViewModel() {
    val subscribedChannels: StateFlow<List<Channel>> =
        channelsRepository.getSubscribedChannels().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Adjust as needed
            initialValue = emptyList()
        )

    fun deleteChannel(channel: Channel) {
        viewModelScope.launch {
            channelsRepository.unsubscribeFromChannel(channel)
        }
    }
}