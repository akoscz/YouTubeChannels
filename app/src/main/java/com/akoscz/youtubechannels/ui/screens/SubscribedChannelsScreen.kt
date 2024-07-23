package com.akoscz.youtubechannels.ui.screens

import com.akoscz.youtubechannels.ui.components.BottomNavigationBar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.akoscz.youtubechannels.data.local.ChannelDao
import com.akoscz.youtubechannels.data.models.Channel
import com.akoscz.youtubechannels.data.repository.ChannelRepository
import com.akoscz.youtubechannels.ui.components.SwipeableChannelRow
import com.akoscz.youtubechannels.ui.viewmodels.SubscribedChannelsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscribedChannelsScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SubscribedChannelsViewModel = hiltViewModel()
) {
    val channels by viewModel.subscribedChannels.collectAsState(initial = emptyList())

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar ={
            CenterAlignedTopAppBar(
                title = { Text("Subscribed Channels") },
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
    ) { innerPadding -> // Use innerPadding to avoid FAB overlapping content
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding) // Apply padding to avoid overlapping with
        ) {
            when {
                channels.isEmpty() -> item {
                    Text(
                        text = "No subscribed channels!",
                        modifier = Modifier.padding(16.dp))
                }
                else -> {
                    itemsIndexed(
                        items = channels,
                        // Provide a unique key based on the channel content
                        key = { _, item -> item.hashCode() }
                    ) { _, channel ->
                        SwipeableChannelRow(channel, onDelete = viewModel::deleteChannel)
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun ChannelsListScreenPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val mockChannelDao =  object : ChannelDao {
        override suspend fun insert(channel: Channel) {
            // Do nothing
        }

        override suspend fun delete(channel: Channel) {
            // Do nothing
        }

        override fun getAllChannels(): Flow<List<Channel>> {
            return flowOf(
                listOf(
                    Channel(
                        id = "1",
                        title = "Channel 1",
                        description = "Description 1",
                        thumbnailDefaultUrl = "https://example.com/channel1.jpg",
                        thumbnailHighUrl = "https://example.com/channel1_high.jpg",
                        thumbnailMediumUrl = "https://example.com/channel1_medium.jpg"
                    ),
                    Channel(
                        id = "2",
                        title = "Channel 2",
                        description = "Description 2",
                        thumbnailDefaultUrl = "https://example.com/channel2.jpg",
                        thumbnailHighUrl = "https://example.com/channel2_high.jpg",
                        thumbnailMediumUrl = "https://example.com/channel2_medium.jpg"
                    )
                )
            )
        }
    }
    val viewModel = SubscribedChannelsViewModel(ChannelRepository(mockChannelDao))
    SubscribedChannelsScreen(snackbarHostState, navController, viewModel)
}
