package com.akoscz.youtubechannels.ui.screens

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.akoscz.youtubechannels.data.db.AppSettingsHelper
import com.akoscz.youtubechannels.data.db.ChannelDetailsDao
import com.akoscz.youtubechannels.data.db.ChannelsDao
import com.akoscz.youtubechannels.data.db.PlaylistsDao
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.network.MockYoutubeApiService
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import com.akoscz.youtubechannels.ui.components.BottomNavigationBar
import com.akoscz.youtubechannels.ui.components.ChannelSearchItemRow
import com.akoscz.youtubechannels.ui.components.SearchBar
import com.akoscz.youtubechannels.ui.viewmodels.SearchChannelsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun SearchChannelsScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SearchChannelsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val appSettingsManager = AppSettingsHelper.getInstance(context)

    // Use the default query value based on the mock data setting
    if (appSettingsManager.isMockDataEnabled()) {
        viewModel.updateSearchQuery("Ancient Aliens")
    }

    val query by viewModel.searchQuery.collectAsState()
    val lazySearchResults: LazyPagingItems<Channel> = viewModel.searchResults.collectAsLazyPagingItems()

    SearchChannelsScreen(
        snackbarHostState = snackbarHostState,
        navController = navController,
        query = query,
        searchOnClicked = viewModel::searchChannels,
        updateSearchQuery = viewModel::updateSearchQuery,
        subscribeToChannel = viewModel::subscribeToChannel,
        lazySearchResults = lazySearchResults
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchChannelsScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    query: String,
    searchOnClicked: (String) -> Unit,
    updateSearchQuery: (String) -> Unit,
    subscribeToChannel: (Channel) -> Unit,
    lazySearchResults: LazyPagingItems<Channel>
    ) {
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
                onSearchTextChanged = updateSearchQuery,
                searchOnClicked = searchOnClicked
            )

            // Search results list
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(lazySearchResults.itemCount) { index ->
                    val channel = lazySearchResults[index]
                    if (channel != null) {
                        ChannelSearchItemRow(channel, subscribeToChannel)
                    }
                }
                when (val loadState = lazySearchResults.loadState.refresh) {
                    is LoadState.Loading -> {
                        item {
                            Text("Loading...")
                        }
                    }
                    is LoadState.Error -> {
                        item {
                            Text("Error: ${loadState.error.message}")
                        }
                    }
                    else -> {}
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

    val mockChannels = listOf(
        Channel(
            id = "1",
            title = "Channel 1",
            description = "Description 1",
            thumbnailDefaultUrl = "https://example.com/channel1.jpg",
            thumbnailHighUrl = "https://example.com/channel1_high.jpg",
            thumbnailMediumUrl = "https://example.com/channel1_medium.jpg",
            channelDetailsId = "1"
        ),
        Channel(
            id = "2",
            title = "Channel 2",
            description = "Description 2",
            thumbnailDefaultUrl = "https://example.com/channel2.jpg",
            thumbnailHighUrl = "https://example.com/channel2_high.jpg",
            thumbnailMediumUrl = "https://example.com/channel2_medium.jpg",
            channelDetailsId = "2"
        )
    )
    // Create PagingData from the mock channels
    val mockPagingData = PagingData.from(mockChannels)

    // Create LazyPagingItems from the mock PagingData
    val lazySearchResults: LazyPagingItems<Channel> =
        flowOf(mockPagingData).collectAsLazyPagingItems()

    SearchChannelsScreen(
        snackbarHostState,
        navController,
        query = "Preview Query",
        searchOnClicked = {},
        updateSearchQuery = {},
        subscribeToChannel = {},
        lazySearchResults = lazySearchResults
    )
}
