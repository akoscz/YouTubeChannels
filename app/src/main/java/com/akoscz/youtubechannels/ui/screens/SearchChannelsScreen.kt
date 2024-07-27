package com.akoscz.youtubechannels.ui.screens

import com.akoscz.youtubechannels.ui.components.BottomNavigationBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.akoscz.youtubechannels.data.db.ChannelsDao
import com.akoscz.youtubechannels.data.db.ChannelDetailsDao
import com.akoscz.youtubechannels.data.db.PlaylistsDao
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.network.MockYoutubeApiService
import com.akoscz.youtubechannels.data.network.SearchChannelsDataSource
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import com.akoscz.youtubechannels.ui.components.ChannelSearchItemRow
import com.akoscz.youtubechannels.ui.components.SearchBar
import com.akoscz.youtubechannels.ui.viewmodels.SearchChannelsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchChannelsScreen(snackbarHostState: SnackbarHostState,
                         navController: NavHostController,
                         viewModel: SearchChannelsViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val searchResultsFlow by viewModel.searchResults.collectAsState()
    val searchResults = searchResultsFlow?.collectAsLazyPagingItems() // Collect here

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Search Channels") },
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
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            // Search bar
            SearchBar(
                searchText = query,
                onSearchTextChanged = { query = it },
                viewModel = viewModel
            )

            // Search results list
            searchResults?.let { results ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(results.itemCount) {
                        val result = results[it]
                        if (result != null) {
                            ChannelSearchItemRow(result)
                        }
                    }

                    // Handle load state
                    when (val refreshState = results.loadState.refresh) {
                        is LoadState.Loading -> {
                            // Handle initial refresh loading state
                            item {
                                Text("Refreshing data...")
                            }
                        }
                        is LoadState.Error -> {
                            // Handle initial refresh error state
                            item {
                                Text("Error refreshing data: ${refreshState.error.message}")
                            }
                        }
                        is LoadState.NotLoading -> {
                            // Handle initial refresh not loading state
                            item {
                                Text("Refresh No more items to load")
                            }
                        }
                    }
                    when (val prependState = results.loadState.prepend) {
                        is LoadState.Loading -> {
                            // Handle prepend loading state (adding items at the beginning)
                            item {
                                Text("Loading more items at the beginning...")
                            }
                        }
                        is LoadState.Error -> {
                            item {
                                Text("Prepend Error loading data: ${prependState.error.message}")
                            }
                        }
                        is LoadState.NotLoading -> {
                            item {
                                Text("Prepend No more items to load")
                            }
                        }
                    }
                    when (val loadState = results.loadState.append) {
                        is LoadState.Error -> {
                            item {
                                Text("Error loading data: ${loadState.error.message}")
                                Button(onClick = { results.retry() }) { // Retry button
                                    Text("Retry")
                                }
                            }
                        }

                        is LoadState.Loading -> { // Loading state
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        is LoadState.NotLoading -> { // Not loading state (optional)
                            // You can display a message or do nothing here
                            item {
                                Text("NotLoading No more items to load")
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
fun SearchChannelsScreenPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val context = LocalContext.current
    val mockYoutubeApiService = MockYoutubeApiService(context)

    val mockChannelsDao =  object : ChannelsDao {
        override suspend fun insert(channel: Channel) {
            // Do nothing
        }

        override suspend fun delete(channel: Channel) {
            // Do nothing
        }

        override fun getAllChannels(): Flow<List<Channel>> {
            return flowOf(emptyList())
        }

        override suspend fun updateChannelDetailsId(channelId: String, detailsId: String) {
            // Do nothing
        }

        fun updateChannelDetails(channelDetails: ChannelDetails) {
            // Do nothing
        }
    }

    val mockChannelDetailsDao = object : ChannelDetailsDao {
        override suspend fun insert(channelDetails: ChannelDetails) {
            // Do nothing
        }

        override suspend fun delete(channelDetails: ChannelDetails) {
            // Do nothing
        }

        override suspend fun getChannelDetails(channelId: String): ChannelDetails {
            return ChannelDetails(
                id = "1",
                title = "Channel 1",
                description = "Description 1",
                customUrl = "https://example.com/channel1",
                publishedAt = "2022-01-01T00:00:00Z",
                thumbnailDefaultUrl = "https://example.com/channel1.jpg",
                thumbnailDefaultWidth = 16,
                thumbnailDefaultHeight = 16,
                thumbnailMediumUrl = "https://example.com/channel1_medium.jpg",
                thumbnailMediumWidth = 32,
                thumbnailMediumHeight = 32,
                thumbnailHighUrl = "https://example.com/channel1_high.jpg",
                thumbnailHighWidth = 64,
                thumbnailHighHeight = 64,
                viewCount = "10",
                subscriberCount = "1000",
                hiddenSubscriberCount = false,
                videoCount = "100",
                likesPlaylistId = "1",
                uploadsPlaylistId = "2",
                bannerExternalUrl = "https://example.com/banner.jpg"
            )
        }

    }
    val mockPlaylistsDao = object : PlaylistsDao {
        override suspend fun insertPlaylist(playlist: Playlist) {
            // Do nothing
        }

        override fun getAllPlaylists(channelId: String): Flow<List<Playlist>> {
            // Return an empty list for simplicity
            return flowOf(emptyList())
        }
    }

    val viewModel = SearchChannelsViewModel(
        SearchChannelsDataSource(context, mockYoutubeApiService),
        ChannelsRepository(mockYoutubeApiService, mockChannelsDao, mockChannelDetailsDao, mockPlaylistsDao)
    )
    SearchChannelsScreen(snackbarHostState, navController, viewModel)
}
