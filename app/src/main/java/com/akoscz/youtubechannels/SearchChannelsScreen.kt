package com.akoscz.youtubechannels

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

// Sample data class for search results
data class SearchResult(val iconResId: Int, val channelName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchChannelsScreen(snackbarHostState: SnackbarHostState, navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    val searchResults = getSearchResults(searchText)

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
                onSearchTextChanged = { searchText = it }
            )

            // Placeholder for search results
            Text(
                text = "Search results will appear here",
                modifier = Modifier.padding(16.dp)
            )

            // Search results list
            SearchResultsList(searchResults)
        }
    }
}

fun getSearchResults(searchText: String): List<SearchResult> {
    return if (searchText.isBlank()) {
        emptyList()
    } else {
        listOf(
            SearchResult(R.drawable.ic_launcher_background, "Channel 3"),
            SearchResult(R.drawable.ic_launcher_background, "Channel 4"),
            // Add more search results here
        )
    }
}

@Composable
fun SearchBar(searchText: String, onSearchTextChanged: (String) -> Unit) {
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
        IconButton(onClick = { /* Perform search */ }) {
            Icon(Icons.Filled.Search, contentDescription = "Search")
        }
    }
}

@Composable
fun SearchResultRow(result: SearchResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = result.iconResId),
            contentDescription = "Channel Icon",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = result.channelName, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = { /* Handle subscribe button click */ }) {
            Text("Subscribe")
        }
    }
}

@Composable
fun SearchResultsList(searchResults: List<SearchResult>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(searchResults.size) { index ->
            SearchResultRow(searchResults[index])
        }
    }
}

@Preview
@Composable
fun SearchChannelsScreenPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    SearchChannelsScreen(snackbarHostState, navController)
}
