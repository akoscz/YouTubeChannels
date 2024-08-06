package com.akoscz.youtubechannels.ui.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.akoscz.youtubechannels.ui.viewmodels.VideoPlayerViewModel

fun replaceWidth(embedHtml: String, width: Int): String {
    return embedHtml.replace("width=\"480\"", "width=\"$width\"")
}

fun enableAutoplay(embedHtml: String): String {
    val srcUrl = extractSrc(embedHtml)
    val srcAutoplay = srcUrl?.plus("?autoplay=1") ?: ""
    return embedHtml.replace(srcUrl ?: "", srcAutoplay)
}

fun removeWebShare(embedHtml: String): String {
    return embedHtml.replace("web-share", "")
}

fun extractSrc(embedHtml: String): String? {
    val regex = """src="([^"]*)"""".toRegex()
    val matchResult = regex.find(embedHtml)
    return matchResult?.groups?.get(1)?.value
}


fun createHtmlBody(embedHtml: String): String {
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Embedded Video</title>
            <style>
                iframe {
                    margin: 0px !important;
                    padding: 0px !important;
                    background: black;
                    border: 0px !important;
                    overflow: hidden;
                    display:block;
                }
                html, body {
                    margin: 0px !important;
                    padding: 0px !important;
                    border: 0px !important;
                    width: 100%;
                    height: 100%;
                }
            </style>
        </head>
        <body style="margin:0;padding:0;">
            $embedHtml
        </body>
        </html>
    """.trimIndent()
}

@SuppressLint("SetJavaScriptEnabled") // Suppress because video player requires JavaScript
@Composable
fun VideoPlayerScreen(
    videoId: String,
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = videoId) {
        viewModel.getVideo(videoId)
    }
    val video = viewModel.video.collectAsState().value ?: return

    val configuration = LocalConfiguration.current
    val isLandscape by remember { mutableStateOf(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) }
    var isFullscreen by remember { mutableStateOf(false) }
    var customView by remember { mutableStateOf<View?>(null) }
    val activity = LocalContext.current as ComponentActivity

    // handle the back button when the video is in fullscreen
    BackHandler(enabled = isFullscreen) {
        customView?.let {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            (activity.window.decorView as ViewGroup).removeView(it)
            customView = null
            isFullscreen = false
        }
    }

    val density = LocalDensity.current
    val screenWidthPx = LocalContext.current.resources.displayMetrics.widthPixels
    val densityWidth = with(density) { screenWidthPx.toDp().value.toInt() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(factory = { context ->
            WebView(context).apply {
                setBackgroundColor(Color.Black.toArgb())
                // override the WebViewClient to handle the video player fullscreen mode
                webChromeClient = object : WebChromeClient() {
                    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                        customView = view
                        activity.window.decorView.systemUiVisibility = (
                                View.SYSTEM_UI_FLAG_FULLSCREEN
                                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                )
                        (activity.window.decorView as ViewGroup).addView(
                            view,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                        isFullscreen = true
                    }

                    override fun onHideCustomView() {
                        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                        (activity.window.decorView as ViewGroup).removeView(customView)
                        customView = null
                        isFullscreen = false
                    }
                }

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadsImagesAutomatically = true
                settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                settings.mediaPlaybackRequiresUserGesture = false

                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                var embedHtml = video.embedHtml
                embedHtml = replaceWidth(embedHtml, densityWidth)
                embedHtml = enableAutoplay(embedHtml)
                embedHtml = removeWebShare(embedHtml)
                embedHtml = createHtmlBody(embedHtml)

                println("Embed HTML: $embedHtml")
                loadDataWithBaseURL("http://youtube.com", embedHtml, "text/html", "utf-8", null)
            }
        })
    }
}
