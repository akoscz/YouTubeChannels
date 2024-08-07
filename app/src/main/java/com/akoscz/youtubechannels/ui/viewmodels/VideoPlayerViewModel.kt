package com.akoscz.youtubechannels.ui.viewmodels

import android.webkit.JavascriptInterface
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor() : ViewModel() {

    private val _videoPosition = MutableStateFlow(0.0)
    val videoPosition: StateFlow<Double> = _videoPosition

    fun saveVideoPosition(position: Double) {
        _videoPosition.value = position
    }
}

/**
 * This class provides an interface for the WebView to communicate with the VideoPlayerViewModel
 */
class JSViewModelInterface(private val viewModel: VideoPlayerViewModel) {
    @JavascriptInterface
    fun updateCurrentTime(time: Double) {
        viewModel.saveVideoPosition(time)
    }
}

fun createHtmlBody(videoId: String, videoPosition: Double, width: Int, height: Int): String {
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Embedded Video</title>
            <style>
                #player {
                    margin: 0px !important;
                    padding: 0px !important;
                    background: black;
                    border: 0px !important;
                    overflow: hidden;
                    display: block;
                }
                html, body {
                    margin: 0px !important;
                    padding: 0px !important;
                    border: 0px !important;
                    width: 100%;
                    height: 100%;
                }
            </style>
            <script src="https://www.youtube.com/iframe_api"></script>
            <script>
                var player;
                function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                        width: '$width',
                        height: '$height',
                        videoId: '$videoId',
                        playerVars: {
                            'autoplay': 1,
                            'controls': 1,
                            'start': '${videoPosition.toInt()}',
                            'enablejsapi': 1,
                            'frameborder': 0,
                            'allow': 'accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture;',
                            'referrerpolicy': 'strict-origin-when-cross-origin',
                            'allowfullscreen': 1
                        },
                        events: {
                            'onStateChange': onPlayerStateChange
                        }
                    });
                }
                function onPlayerStateChange(event) {
                    if (event.data == YT.PlayerState.PLAYING) {
                        updateTimeInterval = setInterval(function() {
                            var currentTime = player.getCurrentTime();
                            Android.updateCurrentTime(currentTime);
                        }, 1000);
                    } else if (event.data == YT.PlayerState.PAUSED || event.data == YT.PlayerState.ENDED) {
                        clearInterval(updateTimeInterval);
                    }
                }
                window.onYouTubeIframeAPIReady = onYouTubeIframeAPIReady;
            </script>
        </head>
        <body>
            <div id="player"></div>
        </body>
        </html>
    """.trimIndent()
}