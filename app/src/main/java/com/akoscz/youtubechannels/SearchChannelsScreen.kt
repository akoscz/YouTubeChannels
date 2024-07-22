package com.akoscz.youtubechannels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchChannelsViewModel @Inject constructor(
    private val youtubeApiRepository: YoutubeApiRepository
) : ViewModel() {
    private val _searchResults = MutableStateFlow<List<SearchItem>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    fun searchChannels(queryStr: String) {
        viewModelScope.launch {
            _searchResults.value = youtubeApiRepository.searchChannels(queryStr) // Assign the list

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchChannelsScreen(snackbarHostState: SnackbarHostState,
                         navController: NavHostController,
                         viewModel: SearchChannelsViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf("") }
    val searchResults = viewModel.searchResults.collectAsState()

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
                searchText = searchText,
                onSearchTextChanged = { searchText = it },
                viewModel = viewModel
            )

            val searchResults by viewModel.searchResults.collectAsState()
            // Search results list
            SearchResultsList(searchResults)
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
            onValueChange = onSearchTextChanged,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Search for channels") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { viewModel.searchChannels(searchText)}) {
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
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = result.snippet.title, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = { /* Handle subscribe button click */ }) {
            Text("Subscribe")
        }
    }
}

@Composable
fun SearchResultsList(searchResults: List<SearchItem>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(searchResults.size) { index ->
            val result = searchResults[index]
            SearchResultRow(result)
        }
    }
}

@Preview
@Composable
fun SearchChannelsScreenPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val viewModel = SearchChannelsViewModel(PreviewYoutubeApiRepository())
    SearchChannelsScreen(snackbarHostState, navController, viewModel)
}
