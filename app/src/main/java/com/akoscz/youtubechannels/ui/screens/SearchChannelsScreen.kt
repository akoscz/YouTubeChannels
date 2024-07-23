package com.akoscz.youtubechannels.ui.screens

import com.akoscz.youtubechannels.ui.components.BottomNavigationBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
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
import com.akoscz.youtubechannels.BuildConfig
import com.akoscz.youtubechannels.data.local.ChannelDao
import com.akoscz.youtubechannels.data.models.Channel
import com.akoscz.youtubechannels.data.network.MockYoutubeApiService
import com.akoscz.youtubechannels.data.network.YoutubeDataSource
import com.akoscz.youtubechannels.data.repository.ChannelRepository
import com.akoscz.youtubechannels.ui.components.ChannelSearchItemRow
import com.akoscz.youtubechannels.ui.components.SearchBar
import com.akoscz.youtubechannels.ui.viewmodels.SearchChannelsViewModel
import kotlinx.coroutines.flow.Flow
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

            // if BuildConfig.DEBUG, show mock data toggle
            if (BuildConfig.DEBUG) {
                // Toggle for data source
                Row(modifier = Modifier.padding(16.dp)) {
                    Text("Use Mock Data: ")
                    Switch(
                        checked = viewModel.useMockData,
                        onCheckedChange = { viewModel.toggleDataSource() })
                }
            }

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
                                Text("No more items to load")
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
    val mockChannelDao =  object : ChannelDao {
        override suspend fun insert(channel: Channel) {
            // Do nothing
        }

        override suspend fun delete(channel: Channel) {
            // Do nothing
        }

        override fun getAllChannels(): Flow<List<Channel>> {
            return flowOf(emptyList())
        }
    }

    val viewModel = SearchChannelsViewModel(
        YoutubeDataSource(MockYoutubeApiService(context)),
        YoutubeDataSource(MockYoutubeApiService(context)),
        ChannelRepository(mockChannelDao)
    )
    SearchChannelsScreen(snackbarHostState, navController, viewModel)
}
