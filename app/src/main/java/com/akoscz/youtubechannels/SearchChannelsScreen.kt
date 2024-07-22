package com.akoscz.youtubechannels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchChannelsViewModel @Inject constructor(
    @RealYoutubeApi private val realYoutubeApiRepository: YoutubeApiRepository,
    @MockYoutubeApi private val mockYoutubeApiRepository: YoutubeApiRepository
) : ViewModel() {
    internal var useMockData by mutableStateOf(true)
    private val _searchQuery = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<Flow<PagingData<SearchItem>>?>(null)
    var searchResults = _searchResults.asStateFlow()

    fun searchChannels(query: String) {
        viewModelScope.launch {
            delay(300)
            _searchQuery.value = query
            val repository = if (useMockData) mockYoutubeApiRepository else realYoutubeApiRepository
            _searchResults.value = repository.searchChannels(_searchQuery.value, this)
            println("Search results updated: ${_searchResults.value}")
            searchResults = _searchResults.asStateFlow()
        }
    }

    fun toggleDataSource() {
        useMockData = !useMockData
    }
}


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

            // Toggle for data source
            Row(modifier = Modifier.padding(16.dp)) {
                Text("Use Mock Data: ")
                Switch(checked = viewModel.useMockData, onCheckedChange = { viewModel.toggleDataSource() })
            }

            // Search results list
            searchResults?.let { results ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(results.itemCount) {
                        val result = results[it]
                        if (result != null) {
                            SearchResultRow(result)
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


@Composable
fun SearchBar(searchText: String,
              onSearchTextChanged: (String) -> Unit,
              viewModel: SearchChannelsViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchText,
            onValueChange = { onSearchTextChanged (it) },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Search for channels") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = {
            viewModel.searchChannels(searchText)
        }) {
            Icon(Icons.Filled.Search, contentDescription = "Search")
        }
    }
}

@Composable
fun SearchResultRow(result: SearchItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Use result.snippet.thumbnails to display the channel thumbnail
        AsyncImage(
            model = result.snippet.thumbnails.default.url,
            contentDescription = "Channel Icon",
            modifier = Modifier.size(48.dp),
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your placeholder
            error = painterResource(id = R.drawable.ic_launcher_background) // Replace with your error image
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = result.snippet.title, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = { /* Handle subscribe button click */ }) {
            Text("Subscribe")
        }
    }
}

@Preview
@Composable
fun SearchChannelsScreenPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel = SearchChannelsViewModel(YoutubeApiRepository(MockYoutubeApiService(context)), YoutubeApiRepository(MockYoutubeApiService(context)))
    SearchChannelsScreen(snackbarHostState, navController, viewModel)
}
