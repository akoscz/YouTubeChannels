package com.akoscz.youtubechannels.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.akoscz.youtubechannels.ui.screens.VideoPlayerScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val videoId = intent.getStringExtra("videoId") ?: ""
            println("VideoPlayerActivity.onCreate() --> videoId: $videoId")
            VideoPlayerScreen(
                videoId = videoId
            )
        }
    }
}