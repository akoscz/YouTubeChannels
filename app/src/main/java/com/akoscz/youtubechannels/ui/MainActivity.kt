package com.akoscz.youtubechannels.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akoscz.youtubechannels.BuildConfig
import com.akoscz.youtubechannels.ui.screens.HomeScreen
import com.akoscz.youtubechannels.ui.screens.SubscribedChannelsScreen
import com.akoscz.youtubechannels.ui.screens.SearchChannelsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppContent()
        }
    }
}

@Composable
fun AppContent() {
    var showErrorDialog by remember { mutableStateOf(false) }
    var apiKeyChecked by remember { mutableStateOf(false) } // Flag to track API key check
    val context = LocalContext.current

    // Check if API key is empty
    LaunchedEffect(Unit) {
        if (BuildConfig.API_KEY.isEmpty()) {
            showErrorDialog = true
        }
        apiKeyChecked = true // Mark API key check as complete
    }

    if (apiKeyChecked) {
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = {
                    Text("API key is missing. Please check your local.properties file 'api_key' value.")
                },
                confirmButton = {
                    Button(onClick = {
                        showErrorDialog = false
                        (context as? Activity)?.finish() // Close the Activity
                    }) {
                        Text("OK")
                    }
                }
            )
            return
        } else {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            Navigation(navController, PaddingValues(0.dp), snackbarHostState)
        }
    } else {
        // Show a circular progress indicator while waiting for API key check to complete
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun Navigation(
    navController: NavHostController,
    innerPadding: PaddingValues,
    snackbarHostState: SnackbarHostState
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(innerPadding)
    ) {
        composable("home") { HomeScreen(snackbarHostState, navController) }
        composable("search") { SearchChannelsScreen(snackbarHostState, navController) }
        composable("subscriptions") { SubscribedChannelsScreen(snackbarHostState, navController) }
    }
}

@Preview(showBackground =true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        AppContent()
    }
}