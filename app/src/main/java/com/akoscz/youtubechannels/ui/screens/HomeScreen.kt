package com.akoscz.youtubechannels.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import com.akoscz.youtubechannels.ui.components.BottomNavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.akoscz.youtubechannels.data.db.AppSettingsHelper
import com.akoscz.youtubechannels.ui.MainActivity
import com.akoscz.youtubechannels.ui.components.VideoRow
import com.akoscz.youtubechannels.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
) {
    val context = LocalContext.current
    val appSettingsManager = AppSettingsHelper.getInstance(context)
    var showMenu by remember { mutableStateOf(false) }
    var showThemeSelector by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(appSettingsManager.getTheme()) }

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
                                Text (
                                    if (appSettingsManager.isMockDataEnabled())
                                        "Disable Mock Data"
                                    else
                                        "Enable Mock Data"
                                )
                            },
                            onClick = {
                                appSettingsManager.setMockDataEnabled(
                                    !appSettingsManager.isMockDataEnabled())
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
                                style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedTheme == "light",
                                onClick = { selectedTheme = "light"  }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Light Theme",
                                style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedTheme == "dark",
                                onClick = { selectedTheme = "dark" }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dark Theme",
                                style = MaterialTheme.typography.bodyMedium)
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
        // Fetch videos from ViewModel and display in a LazyColumn
        val viewModel: HomeViewModel = hiltViewModel()
        val videos by viewModel.allVideos.collectAsState(emptyList())

        LazyColumn(
            modifier = Modifier.padding(
                innerPadding
            )
        ) {
            items(videos) { video ->
                VideoRow(video)
            }
        }
    }
}