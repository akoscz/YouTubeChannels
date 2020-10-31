package com.akoscz.youtubechannels

import android.util.Log
import androidx.lifecycle.ViewModel

class ChannelsViewModel : ViewModel() {

    init {
        Log.i("ChannelsViewModel", "created!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("ChannelsViewModel", "destroyed!")

    }
}