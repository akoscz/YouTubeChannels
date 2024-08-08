package com.akoscz.youtubechannels.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.ui.VideoPlayerActivity
import com.akoscz.youtubechannels.ui.components.BottomNavigationBar
import com.akoscz.youtubechannels.ui.components.ChannelDetailsHeader
import com.akoscz.youtubechannels.ui.components.PlaylistRow
import com.akoscz.youtubechannels.ui.components.VideoRow
import com.akoscz.youtubechannels.ui.viewmodels.ChannelDetailsViewModel
import kotlinx.coroutines.flow.flowOf

data class ChannelTab(val title: String)

val channelTabs = listOf(
    ChannelTab("Videos"),
    ChannelTab("Playlists")
)

@Composable
fun ChannelDetailsScreen(
    channelId: String,
    channelTitle: String,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    viewModel: ChannelDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(channelId) {
        viewModel.fetchChannelDetails(channelId)
        viewModel.fetchChannelPlaylists(channelId)
    }

    val channelDetails by viewModel.channelDetails.collectAsState()
    val videos = viewModel.videos.collectAsLazyPagingItems()
    val playlists = viewModel.playlists.collectAsLazyPagingItems()
    val selectedTabIndex = channelTabs.indexOf(ChannelTab("Videos"))

    ChannelDetailsScreen(
        channelTitle = channelTitle,
        snackbarHostState = snackbarHostState,
        navController = navController,
        channelDetails = channelDetails,
        videos = videos,
        playlists = playlists,
        initialSelectedTabIndex = selectedTabIndex
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelDetailsScreen(
    channelTitle: String,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    channelDetails: ChannelDetails?,
    videos: LazyPagingItems<Video>,
    playlists: LazyPagingItems<Playlist>,
    initialSelectedTabIndex: Int
){
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(channelTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->

        if (channelDetails == null) {
            // Show loading indicator
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                ChannelDetailsHeader(channelDetails)

                var selectedTabIndex by remember { mutableIntStateOf(initialSelectedTabIndex) }

                // Tab Row
                TabRow(selectedTabIndex = selectedTabIndex) {
                    channelTabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(tab.title) }
                        )
                    }
                }

                val context = LocalContext.current
                // Content based on selected tab
                when (selectedTabIndex) {
                    0 -> {
                        // Display Videos ListView
                        LazyColumn {
                            items(videos.itemCount) { video ->
                                val videoItem = videos[video]
                                VideoRow(
                                    videoItem!!,
                                    onPlayClicked = {
                                        val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                            putExtra("videoId", videoItem.id)
                                            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        }
                                        context.startActivity(intent)
                                    },
                                )
                            }
                        }
                        when (val loadState = videos.loadState.refresh) {
                            is LoadState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                        .padding(16.dp),
                                )
                            }
                            is LoadState.Error -> {
                                Text("Error: ${loadState.error.message}")
                            }
                            else -> {}
                        }
                    }
                    1 -> {
                        LazyColumn {
                            items(playlists.itemCount) { i ->
                                val playlist = playlists[i]
                                if (playlist != null) {
                                    PlaylistRow(playlist, channelTitle)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun ChannelDetailsScreenPreview1() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    val mockChannelDetails = ChannelDetails(
        id = "1",
        title = "Channel 1",
        description = "Description 1",
        customUrl = "https://example.com/channel1",
        publishedAt = "2023-08-01T00:00:00Z",
        thumbnailDefaultUrl = "https://example.com/channel1.jpg",
        thumbnailDefaultWidth = 16,
        thumbnailDefaultHeight = 16,
        thumbnailHighUrl = "https://example.com/channel1_high.jpg",
        thumbnailHighWidth = 32,
        thumbnailHighHeight = 32,
        thumbnailMediumUrl = "https://example.com/channel1_medium.jpg",
        thumbnailMediumWidth = 64,
        thumbnailMediumHeight = 64,
        viewCount = "1000",
        subscriberCount = "100",
        hiddenSubscriberCount = false,
        videoCount = "2",
        likesPlaylistId = "likes",
        uploadsPlaylistId = "uploads",
        bannerExternalUrl = "https://example.com/banner.jpg",
    )

    val mockVideos = listOf(
        Video(
            id = "1",
            publishedAt = "2022-08-01T00:00:00Z",
            channelId = "1",
            title = "Video 1",
            description = "Description 1",
            defaultThumbnailUrl = "https://example.com/video1.jpg",
            defaultThumbnailWidth = 16,
            defaultThumbnailHeight = 16,
            mediumThumbnailUrl = "https://example.com/video1_medium.jpg",
            mediumThumbnailWidth = 32,
            mediumThumbnailHeight = 32,
            highThumbnailUrl = "https://example.com/video1_high.jpg",
            highThumbnailWidth = 64,
            highThumbnailHeight = 64,
            standardThumbnailUrl = "https://example.com/video1_standard.jpg",
            standardThumbnailWidth = 128,
            standardThumbnailHeight = 128,
            maxresThumbnailUrl = "https://example.com/video1_maxres.jpg",
            maxresThumbnailWidth = 256,
            maxresThumbnailHeight = 256,
            channelTitle = "Channel 1",
            defaultLanguage = "en",
            defaultedAudioLanguage = "en",
            duration = "PT10M",
            contentYtRating = "safe",
            viewCount = 1000,
            likeCount = 100,
            favoriteCount = 10,
            commentCount = 100,
            embedHtml = "<iframe>"
        ),
        Video(
            id = "2",
            publishedAt = "2024-08-01T00:00:00Z",
            channelId = "1",
            title = "Video 2",
            description = "Description 2",
            defaultThumbnailUrl = "https://example.com/video2.jpg",
            defaultThumbnailWidth = 16,
            defaultThumbnailHeight = 16,
            mediumThumbnailUrl = "https://example.com/video2_medium.jpg",
            mediumThumbnailWidth = 32,
            mediumThumbnailHeight = 32,
            highThumbnailUrl = "https://example.com/video2_high.jpg",
            highThumbnailWidth = 64,
            highThumbnailHeight = 64,
            standardThumbnailUrl = "https://example.com/video2_standard.jpg",
            standardThumbnailWidth = 128,
            standardThumbnailHeight = 128,
            maxresThumbnailUrl = "https://example.com/video2_maxres.jpg",
            maxresThumbnailWidth = 256,
            maxresThumbnailHeight = 256,
            channelTitle = "Channel 1",
            defaultLanguage = "en",
            defaultedAudioLanguage = "en",
            duration = "PT10M",
            contentYtRating = "safe",
            viewCount = 1000,
            likeCount = 100,
            favoriteCount = 10,
            commentCount = 100,
            embedHtml = "<iframe>"
        )
    )

    // Create PagingData from the mock channels
    val mockPagingData = PagingData.from(mockVideos)

    // Create LazyPagingItems from the mock PagingData
    val lazySearchResults: LazyPagingItems<Video> =
        flowOf(mockPagingData).collectAsLazyPagingItems()

    val mockPlaylists = emptyList<Playlist>()
    val lazyPlaylists: LazyPagingItems<Playlist> =
        flowOf(PagingData.from(mockPlaylists)).collectAsLazyPagingItems()

    ChannelDetailsScreen(
        channelTitle = "Channel 1",
        snackbarHostState = snackbarHostState,
        navController = navController,
        channelDetails = mockChannelDetails,
        videos = lazySearchResults,
        playlists = lazyPlaylists,
        initialSelectedTabIndex = 0
    )
}

@Preview
@Composable
fun ChannelDetailsScreenPreview2() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    val mockChannelDetails = ChannelDetails(
        id = "2",
        title = "Channel 2",
        description = "Description 2",
        customUrl = "https://example.com/channel2",
        publishedAt = "2022-06-01T00:00:00Z",
        thumbnailDefaultUrl = "https://example.com/channel2.jpg",
        thumbnailDefaultWidth = 16,
        thumbnailDefaultHeight = 16,
        thumbnailHighUrl = "https://example.com/channel2_high.jpg",
        thumbnailHighWidth = 32,
        thumbnailHighHeight = 32,
        thumbnailMediumUrl = "https://example.com/channel2_medium.jpg",
        thumbnailMediumWidth = 64,
        thumbnailMediumHeight = 64,
        viewCount = "2499000",
        subscriberCount = "50000",
        hiddenSubscriberCount = false,
        videoCount = "20",
        likesPlaylistId = "likes",
        uploadsPlaylistId = "uploads",
        bannerExternalUrl = "https://example.com/banner.jpg",
    )

    val mockVideos = emptyList<Video>()
    // Create PagingData from the mock channels
    val mockPagingData = PagingData.from(mockVideos)

    // Create LazyPagingItems from the mock PagingData
    val lazySearchResults: LazyPagingItems<Video> =
        flowOf(mockPagingData).collectAsLazyPagingItems()

    val mockPlaylists = listOf(
        Playlist(
            id = "1",
            publishedAt = "2023-08-01T00:00:00Z",
            channelId = "1",
            title = "Playlist 1",
            description = "Description 1",
            defaultThumbnailUrl = "https://example.com/playlist1.jpg",
            defaultThumbnailWidth = 16,
            defaultThumbnailHeight = 16,
            mediumThumbnailUrl = "https://example.com/playlist1_medium.jpg",
            mediumThumbnailWidth = 32,
            mediumThumbnailHeight = 32,
            highThumbnailUrl = "https://example.com/playlist1_high.jpg",
            highThumbnailWidth = 64,
            highThumbnailHeight = 64,
            standardThumbnailUrl = "https://example.com/playlist1_standard.jpg",
            standardThumbnailWidth = 128,
            standardThumbnailHeight = 128,
            maxresThumbnailUrl = "https://example.com/playlist1_maxres.jpg",
            maxresThumbnailWidth = 256,
            maxresThumbnailHeight = 256,
            itemCount = 10,
            embedHtml = "<iframe>"
        ),
        Playlist(
            id = "2",
            publishedAt = "2024-08-01T00:00:00Z",
            channelId = "1",
            title = "Playlist 2",
            description = "Description 2",
            defaultThumbnailUrl = "https://example.com/playlist2.jpg",
            defaultThumbnailWidth = 16,
            defaultThumbnailHeight = 16,
            mediumThumbnailUrl = "https://example.com/playlist2_medium.jpg",
            mediumThumbnailWidth = 32,
            mediumThumbnailHeight = 32,
            highThumbnailUrl = "https://example.com/playlist2_high.jpg",
            highThumbnailWidth = 64,
            highThumbnailHeight = 64,
            standardThumbnailUrl = "https://example.com/playlist2_standard.jpg",
            standardThumbnailWidth = 128,
            standardThumbnailHeight = 128,
            maxresThumbnailUrl = "https://example.com/playlist2_maxres.jpg",
            maxresThumbnailWidth = 256,
            maxresThumbnailHeight = 256,
            itemCount = 10,
            embedHtml = "<iframe>"
        )
    )
    val lazyPlaylists: LazyPagingItems<Playlist> =
        flowOf(PagingData.from(mockPlaylists)).collectAsLazyPagingItems()

    ChannelDetailsScreen(
        channelTitle = "Channel 2",
        snackbarHostState = snackbarHostState,
        navController = navController,
        channelDetails = mockChannelDetails,
        videos = lazySearchResults,
        playlists = lazyPlaylists,
        initialSelectedTabIndex = 1
    )
}