package com.akoscz.youtubechannels.ui.screens

import android.content.Intent
import com.akoscz.youtubechannels.ui.components.BottomNavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.akoscz.youtubechannels.data.db.FeatureToggleHelper
import com.akoscz.youtubechannels.data.db.FeatureToggleManager
import com.akoscz.youtubechannels.di.AppModule
import com.akoscz.youtubechannels.ui.MainActivity
import com.akoscz.youtubechannels.ui.components.VideoCard
import com.akoscz.youtubechannels.ui.viewmodels.HomeViewModel
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val featureToggleManager = FeatureToggleHelper.getInstance(context)

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
                            text = {
                                Text(
                                    if (featureToggleManager.isMockDataEnabled())
                                        "Disable Mock Data"
                                    else
                                        "Enable Mock Data"
                                )
                            },
                            onClick = {
                                featureToggleManager.setMockDataEnabled(
                                    !featureToggleManager.isMockDataEnabled())
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
                    }
                }
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