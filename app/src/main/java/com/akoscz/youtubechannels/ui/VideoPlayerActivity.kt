package com.akoscz.youtubechannels.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.akoscz.youtubechannels.ui.screens.VideoPlayerScreen
import com.akoscz.youtubechannels.ui.viewmodels.VideoPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        println("VideoPlayerActivity.onCreate()")
        super.onCreate(savedInstanceState)

        setContent {
            val videoId = intent.getStringExtra("videoId") ?: ""
            println("VideoPlayerActivity.setContent{} --> videoId: $videoId")
            VideoPlayerScreen(
                videoId = videoId
            )
        }
    }
}