package com.akoscz.youtubechannels.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.models.room.getFormattedSubscriberCount
import com.akoscz.youtubechannels.data.models.room.getFormattedVideoCount
import com.akoscz.youtubechannels.data.models.room.getFormattedViewCount
import com.akoscz.youtubechannels.data.models.room.getModifiedBannerUrl
import com.akoscz.youtubechannels.ui.components.BottomNavigationBar
import com.akoscz.youtubechannels.ui.components.PlaylistItemRow
import com.akoscz.youtubechannels.ui.viewmodels.ChannelDetailsViewModel

data class ChannelTab(val title: String)

val channelTabs = listOf(
    ChannelTab("Videos"),
    ChannelTab("Playlists")
)

@OptIn(ExperimentalMaterial3Api::class)
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
            var selectedTabIndex by remember { mutableIntStateOf(0) }

            Column(modifier = Modifier.fillMaxSize().padding(innerPadding),) {
                // Banner Image
                AsyncImage(
                    model = channelDetails?.getModifiedBannerUrl(LocalConfiguration.current.screenWidthDp),
                    contentDescription = "Channel Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(start = 16.dp, end = 16.dp)
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 12.dp, bottomEnd = 12.dp,
                                topStart = 12.dp, topEnd = 12.dp
                            )
                        ),
                    contentScale = ContentScale.Crop
                )

                // Channel Info Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Thumbnail
                    AsyncImage(
                        model = channelDetails?.thumbnailMediumUrl,
                        contentDescription = "Channel Thumbnail",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Channel Name, Custom URL, Subscriber Count
                    Column {
                        Text(
                            channelDetails?.title ?: "",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            channelDetails?.customUrl ?: "",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${channelDetails?.getFormattedSubscriberCount()} subscribers",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "${channelDetails?.getFormattedVideoCount()} videos",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "${channelDetails?.getFormattedViewCount()} views",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Expanding Description
                var expanded by remember { mutableStateOf(false) }
                val maxLines = if (expanded) Int.MAX_VALUE else 2
                Text(
                    text = channelDetails?.description ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = maxLines, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .clickable { expanded = !expanded }
                )

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

                // Content based on selected tab
                when (selectedTabIndex) {
                    0 -> {
                        // Display Videos ListView here
                        LazyColumn {
                            // ... Your video items ...
                        }
                    }
                    1 -> {
                        val lazyPlaylistItems: LazyPagingItems<Playlist> = viewModel.playlists.collectAsLazyPagingItems()

                        LazyColumn {
                            items(lazyPlaylistItems.itemCount) { i ->
                                val playlist = lazyPlaylistItems[i]
                                if (playlist != null) {
                                    PlaylistItemRow(playlist, channelTitle)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

