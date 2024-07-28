package com.akoscz.youtubechannels.ui.screens

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.akoscz.youtubechannels.ui.components.BottomNavigationBar
import com.akoscz.youtubechannels.ui.components.ChannelDetailsHeader
import com.akoscz.youtubechannels.ui.components.PlaylistList
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
        val channelDetails by viewModel.channelDetails.collectAsState()

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
                ChannelDetailsHeader(channelDetails!!)

                var selectedTabIndex by remember { mutableIntStateOf(0) }

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
                        PlaylistList(
                            playlistItems = viewModel.playlists.collectAsLazyPagingItems(),
                            channelTitle = channelTitle
                        )
                    }
                }
            }
        }
    }
}

