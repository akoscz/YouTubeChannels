package com.akoscz.youtubechannels.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import com.akoscz.youtubechannels.ui.components.BottomNavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.akoscz.youtubechannels.data.db.AppSettingsHelper
import com.akoscz.youtubechannels.data.db.AppSettingsManager
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.ui.MainActivity
import com.akoscz.youtubechannels.ui.components.HomeVideoCard
import com.akoscz.youtubechannels.ui.components.SortTypeButtonRow
import com.akoscz.youtubechannels.ui.viewmodels.HomeViewModel
import com.akoscz.youtubechannels.ui.viewmodels.SortType

@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
) {
    val context = LocalContext.current
    val appSettingsManager = AppSettingsHelper.getInstance(context)
    // Fetch videos from ViewModel and display in a LazyColumn
    val viewModel: HomeViewModel = hiltViewModel()
    val videos: List<Video> by viewModel.homeVideos.collectAsState()
    val channelDetailsMap: Map<String, ChannelDetails> by viewModel.channelDetailsMap.collectAsState()
    val selectedSortType: SortType by viewModel.sortType.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchHomeVideos()
    }
    HomeScreen(
        snackbarHostState,
        navController,
        appSettingsManager,
        videos,
        channelDetailsMap,
        selectedSortType,
        viewModel::updateSortType)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    appSettingsManager: AppSettingsManager,
    videos: List<Video>,
    channelDetailsMap: Map<String, ChannelDetails>,
    selectedSortType: SortType,
    updateSortType: (SortType) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showThemeSelector by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(appSettingsManager.getTheme()) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            enabled = !appSettingsManager.isForceMockData(),
                            text = {
                                Text(
                                    if (appSettingsManager.isMockDataEnabled())
                                        "Disable Mock Data"
                                    else
                                        "Enable Mock Data"
                                )
                            },
                            onClick = {
                                appSettingsManager.setMockDataEnabled(
                                    !appSettingsManager.isMockDataEnabled()
                                )
                                // Restart the application process
                                val packageManager = context.packageManager
                                val intent =
                                    packageManager.getLaunchIntentForPackage(context.packageName)
                                val componentName = intent?.component
                                val restartIntent = Intent.makeRestartActivityTask(componentName)
                                context.startActivity(restartIntent)
                                Runtime.getRuntime().exit(0) // Terminate the current process
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("Change Theme")
                            },
                            onClick = {
                                showThemeSelector = true
                                showMenu = false // Close the main menu
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->

        // Theme Selector Dialog
        if (showThemeSelector) {
            AlertDialog(
                onDismissRequest = { showThemeSelector = false },
                title = { Text("Select Theme") },
                text = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedTheme == "system",
                                onClick = { selectedTheme = "system" }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("System Theme",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedTheme == "light",
                                onClick = { selectedTheme = "light" }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Light Theme",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedTheme == "dark",
                                onClick = { selectedTheme = "dark" }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Dark Theme",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showThemeSelector = false
                        appSettingsManager.setTheme(selectedTheme.toString())
                        // Restart the activity
                        context.startActivity(Intent(context, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        })
                    }) {
                        Text("OK")
                    }
                }
            )
        }
        HomeScreenContent(
            videos,
            channelDetailsMap,
            innerPadding,
            selectedSortType,
            updateSortType)
    }
}

@Composable
fun HomeScreenContent(
    videos: List<Video>,
    channelDetailsMap: Map<String, ChannelDetails>,
    innerPadding: PaddingValues,
    selectedSortType: SortType,
    updateSortType: (SortType) -> Unit
) {
    Column(modifier = Modifier.padding(innerPadding)) {
        SortTypeButtonRow(
            selectedSortType = selectedSortType,
            updateSortType = updateSortType
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(videos) { video ->
                val channelDetails = channelDetailsMap[video.channelId]
                if (channelDetails != null) {
                    HomeVideoCard(video, channelDetails)
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    val videos = listOf(
        Video(
            id = "1",
            publishedAt = "2021-01-01T00:00:00Z",
            channelId = "1",
            title = "Video Title 1",
            description = "Video Description 1",
            defaultThumbnailUrl = "https://via.placeholder.com/150",
            defaultThumbnailWidth = 150,
            defaultThumbnailHeight = 150,
            mediumThumbnailUrl = "https://via.placeholder.com/300",
            mediumThumbnailWidth = 300,
            mediumThumbnailHeight = 300,
            highThumbnailUrl = "https://via.placeholder.com/450",
            highThumbnailWidth = 450,
            highThumbnailHeight = 450,
            standardThumbnailUrl = null,
            standardThumbnailWidth = null,
            standardThumbnailHeight = null,
            maxresThumbnailUrl = null,
            maxresThumbnailWidth = null,
            maxresThumbnailHeight = null,
            channelTitle = "Channel Title 1",
            defaultLanguage = "en",
            defaultedAudioLanguage = "en",
            duration = "PT1H0M0S",
            contentYtRating = "ytRating",
            viewCount = 1000,
            likeCount = 100,
            favoriteCount = 10,
            commentCount = 5,
            embedHtml = "embedHtml",
        ),
        Video(
            id = "2",
            publishedAt = "2021-01-02T00:00:00Z",
            channelId = "2",
            title = "Video Title 2",
            description = "Video Description 2",
            defaultThumbnailUrl = "https://via.placeholder.com/150",
            defaultThumbnailWidth = 150,
            defaultThumbnailHeight = 150,
            mediumThumbnailUrl = "https://via.placeholder.com/300",
            mediumThumbnailWidth = 300,
            mediumThumbnailHeight = 300,
            highThumbnailUrl = "https://via.placeholder.com/450",
            highThumbnailWidth = 450,
            highThumbnailHeight = 450,
            standardThumbnailUrl = null,
            standardThumbnailWidth = null,
            standardThumbnailHeight = null,
            maxresThumbnailUrl = null,
            maxresThumbnailWidth = null,
            maxresThumbnailHeight = null,
            channelTitle = "Channel Title 2",
            defaultLanguage = "en",
            defaultedAudioLanguage = "en",
            duration = "PT1H0M0S",
            contentYtRating = "ytRating",
            viewCount = 1000,
            likeCount = 100,
            favoriteCount = 10,
            commentCount = 5,
            embedHtml = "embedHtml",
        ),
        Video(
            id = "3",
            publishedAt = "2021-01-03T00:00:00Z",
            channelId = "3",
            title = "Video Title 3",
            description = "Video Description 3",
            defaultThumbnailUrl = "https://via.placeholder.com/150",
            defaultThumbnailWidth = 150,
            defaultThumbnailHeight = 150,
            mediumThumbnailUrl = "https://via.placeholder.com/300",
            mediumThumbnailWidth = 300,
            mediumThumbnailHeight = 300,
            highThumbnailUrl = "https://via.placeholder.com/450",
            highThumbnailWidth = 450,
            highThumbnailHeight = 450,
            standardThumbnailUrl = null,
            standardThumbnailWidth = null,
            standardThumbnailHeight = null,
            maxresThumbnailUrl = null,
            maxresThumbnailWidth = null,
            maxresThumbnailHeight = null,
            channelTitle = "Channel Title 3",
            defaultLanguage = "en",
            defaultedAudioLanguage = "en",
            duration = "PT1H0M0S",
            contentYtRating = "ytRating",
            viewCount = 1000,
            likeCount = 100,
            favoriteCount = 10,
            commentCount = 5,
            embedHtml = "embedHtml",
        )
    )

    val channelDetailsMap = mapOf(
        "1" to ChannelDetails(
            id = "1",
            title = "Channel Title 1",
            description = "Channel Description 1",
            customUrl = "customUrl",
            publishedAt = "2021-01-01T00:00:00Z",
            thumbnailDefaultUrl = "https://via.placeholder.com/150",
            thumbnailDefaultWidth = 150,
            thumbnailDefaultHeight = 150,
            thumbnailMediumUrl = "https://via.placeholder.com/300",
            thumbnailMediumWidth = 300,
            thumbnailMediumHeight = 300,
            thumbnailHighUrl = "https://via.placeholder.com/450",
            thumbnailHighWidth = 450,
            thumbnailHighHeight = 450,
            viewCount = "1000",
            subscriberCount = "100",
            hiddenSubscriberCount = false,
            videoCount = "10",
            likesPlaylistId = "likesPlaylistId",
            uploadsPlaylistId = "uploadsPlaylistId",
            bannerExternalUrl = "bannerExternalUrl",
        ),
        "2" to ChannelDetails(
            id = "2",
            title = "Channel Title 2",
            description = "Channel Description 2",
            customUrl = "customUrl",
            publishedAt = "2021-01-02T00:00:00Z",
            thumbnailDefaultUrl = "https://via.placeholder.com/150",
            thumbnailDefaultWidth = 150,
            thumbnailDefaultHeight = 150,
            thumbnailMediumUrl = "https://via.placeholder.com/300",
            thumbnailMediumWidth = 300,
            thumbnailMediumHeight = 300,
            thumbnailHighUrl = "https://via.placeholder.com/450",
            thumbnailHighWidth = 450,
            thumbnailHighHeight = 450,
            viewCount = "1000",
            subscriberCount = "100",
            hiddenSubscriberCount = false,
            videoCount = "10",
            likesPlaylistId = "likesPlaylistId",
            uploadsPlaylistId = "uploadsPlaylistId",
            bannerExternalUrl = "bannerExternalUrl",
        )
    )

    HomeScreenContent(
        videos = videos,
        channelDetailsMap = channelDetailsMap,
        innerPadding = PaddingValues(0.dp),
        selectedSortType = SortType.NEWEST,
        updateSortType = {}
    )
}
