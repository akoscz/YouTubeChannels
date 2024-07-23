package com.akoscz.youtubechannels.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.akoscz.youtubechannels.R
import com.akoscz.youtubechannels.data.local.ChannelDao
import com.akoscz.youtubechannels.data.models.Channel
import com.akoscz.youtubechannels.data.repository.ChannelRepository
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
                title = { Text("Subscribed Channels") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("search") }) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
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
                        ChannelRow(channel, onDelete = viewModel::deleteChannel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Color.Red
        SwipeToDismissBoxValue.Settled -> Color.Transparent
        SwipeToDismissBoxValue.EndToStart -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = "delete"
        )
        Spacer(modifier = Modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelRow(
    channel: Channel,
    onDelete: (Channel) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.StartToEnd) {
                onDelete(channel)
                return@rememberSwipeToDismissBoxState true
            }
            false
        },
        positionalThreshold = { it * .40f }
    )
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromEndToStart = false,
        enableDismissFromStartToEnd = true,
        backgroundContent = { DismissBackground(dismissState)},
        content = {
            ChanelRowItem(channel)
        }
    )
}

@Composable
fun ChanelRowItem(channel: Channel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = CenterVertically
    ) {
        AsyncImage(
            model = channel.thumbnailUrl,
            contentDescription = channel.title,
            modifier = Modifier.size(48.dp),
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
            error = painterResource(id = R.drawable.ic_launcher_background)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = channel.title,
            modifier = Modifier.weight(1f) // Occupy remaining space
        )
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = { /* Handle button click */ }) {
            Text("Notify")
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
                        thumbnailUrl = "https://example.com/channel1.jpg"
                    ),
                    Channel(
                        id = "2",
                        title = "Channel 2",
                        thumbnailUrl = "https://example.com/channel2.jpg"
                    )
                )
            )
        }
    }
    val viewModel = SubscribedChannelsViewModel(ChannelRepository(mockChannelDao))
    SubscribedChannelsScreen(snackbarHostState, navController, viewModel)
}
