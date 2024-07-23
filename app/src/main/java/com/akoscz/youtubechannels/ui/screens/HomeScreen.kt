package com.akoscz.youtubechannels.ui.screens

import com.akoscz.youtubechannels.ui.components.BottomNavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.akoscz.youtubechannels.ui.components.VideoCard
import com.akoscz.youtubechannels.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Home") }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        // Fetch videos from ViewModel and display in a LazyColumn
        val viewModel: HomeViewModel = hiltViewModel()
        val videos by viewModel.allVideos.collectAsState(emptyList())

        LazyColumn(
            modifier = Modifier.padding(
                innerPadding
            )
        ) {
            items(videos) { video ->
                VideoCard(video)
            }
        }
    }
}