package com.akoscz.youtubechannels

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


data class ChannelItem(val iconResId: Int, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsListScreen(snackbarHostState: SnackbarHostState, navController: NavHostController) {
    val channelItems = listOf(
        ChannelItem(R.drawable.ic_launcher_foreground, "Channel 1"),
        ChannelItem(R.drawable.ic_launcher_foreground, "Channel 2"),
        // Add more channel items here
    )
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
            items(channelItems.size) { index ->
                ChannelRow(channelItems[index])
            }
        }
    }
}

@Composable
fun ChannelRow(channel: ChannelItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = CenterVertically
    ) {
        Image(
            painter = painterResource(channel.iconResId),
            contentDescription = "Channel Icon",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = channel.name,
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
    ChannelsListScreen(snackbarHostState, navController)
}
