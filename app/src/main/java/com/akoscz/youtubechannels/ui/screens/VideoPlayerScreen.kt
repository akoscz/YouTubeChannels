package com.akoscz.youtubechannels.ui.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.akoscz.youtubechannels.ui.viewmodels.VideoPlayerViewModel
import com.akoscz.youtubechannels.ui.viewmodels.createHtmlBody
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import com.akoscz.youtubechannels.ui.viewmodels.JSViewModelInterface

@SuppressLint("SetJavaScriptEnabled") // Suppress because video player requires JavaScript
@Composable
fun VideoPlayerScreen(
    videoId: String,
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val videoResumePosition: Double by remember { mutableDoubleStateOf(viewModel.videoPosition.value) }
    println("NEW VideoPlayerScreen --> videoId: $videoId, resume position: $videoResumePosition")

    var isFullscreen by remember { mutableStateOf(false) }
    var customView by remember { mutableStateOf<View?>(null) }
    val activity = context as ComponentActivity
    val windowInsetsController = activity.window.insetsController

    // handle the back button when the video is in fullscreen
    BackHandler(enabled = isFullscreen) {
        customView?.let {
            windowInsetsController?.show(WindowInsets.Type.systemBars())
            (activity.window.decorView as ViewGroup).removeView(it)
            customView = null
            isFullscreen = false
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape by remember { mutableStateOf(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) }
    println("VideoPlayerScreen --> orientation: ${if (isLandscape) "Landscape" else "Portrait"}")

    val density = LocalDensity.current
    val screenWidthPx = LocalContext.current.resources.displayMetrics.widthPixels
    val screenHeightPx = LocalContext.current.resources.displayMetrics.heightPixels
    println("VideoPlayerScreen --> screenWidthPx: $screenWidthPx, screenHeightPx: $screenHeightPx")

    val videoHeight: Int
    val videoWidth: Int
    // calculate optimal video size based on screen size and orientation
    if (isLandscape) {
        val insets = ViewCompat.getRootWindowInsets(activity.window.decorView)
        val topInset = insets?.getInsets(WindowInsetsCompat.Type.systemBars())?.top ?: 0
        val bottomInset = insets?.getInsets(WindowInsetsCompat.Type.systemBars())?.bottom ?: 0
        val systemUIHeight = topInset + bottomInset
        videoWidth = with(density) { screenWidthPx.toDp().value.toInt() }
        // take into account the system UI height when in landscape
        videoHeight = calculateHeightFor16x9AspectRatio(videoWidth) - systemUIHeight

    } else { // Portrait
        videoWidth = with(density) { screenWidthPx.toDp().value.toInt() }
        videoHeight = calculateHeightFor16x9AspectRatio(videoWidth)
    }
    println("VideoPlayerScreen --> videoWidth: $videoWidth, videoHeight: $videoHeight")

    var webView: WebView? = remember { null }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(factory = { context ->
            WebView(context).apply {
                println("VideoPlayerScreen --> WebView created")
                webView = this
                setBackgroundColor(Color.Black.toArgb())

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadsImagesAutomatically = true
                settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                settings.mediaPlaybackRequiresUserGesture = false

                // set a javascript interface to have the video player communicate with the viewmodel to update the video position
                addJavascriptInterface(JSViewModelInterface(viewModel), "Android")

                webChromeClient = object : WebChromeClient() {
                    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                        if (view == null) return
                        customView = view
                        isFullscreen = true
                        windowInsetsController?.hide(WindowInsets.Type.systemBars())
                        (activity.window.decorView as ViewGroup).addView(view)
                    }

                    override fun onHideCustomView() {
                        customView?.let {
                            windowInsetsController?.show(WindowInsets.Type.systemBars())
                            (activity.window.decorView as ViewGroup).removeView(it)
                            customView = null
                            isFullscreen = false
                        }
                    }
                }

                val html = createHtmlBody(videoId, videoResumePosition, videoWidth, videoHeight)
                println("HTML: $html")
                loadDataWithBaseURL("http://youtube.com", html, "text/html", "utf-8", null)
            }
        },
        update = {
            println("VideoPlayerScreen --> WebView updated")
            webView = it
        })
    }
}

private fun calculateHeightFor16x9AspectRatio(width: Int): Int {
    val adjustedHeight = (width * 9) / 16
    return adjustedHeight
}