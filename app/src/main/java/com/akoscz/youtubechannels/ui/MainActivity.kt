package com.akoscz.youtubechannels.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.akoscz.youtubechannels.BuildConfig
import com.akoscz.youtubechannels.data.db.AppSettingsHelper
import com.akoscz.youtubechannels.ui.components.Navigation
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
    val appSettingsManager = AppSettingsHelper.getInstance(context)

    // Check if API key is empty
    LaunchedEffect(Unit) {
        if (BuildConfig.API_KEY.isEmpty()) {
            showErrorDialog = true
            appSettingsManager.forceMockData(true) // Force to use mock data since API key is missing
        }
        apiKeyChecked = true // Mark API key check as complete
    }

    if (apiKeyChecked) {
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Alert!") },
                text = {
                    Text("API key is missing.\n" +
                            "Please set \"api_key\" in local.properties.\n" +
                            "Mock data will be used.")
                },
                confirmButton = {
                    Button(onClick = {
                        showErrorDialog = false
                    }) {
                        Text("OK")
                    }
                }
            )
            return
        }

        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        val theme = appSettingsManager.getTheme()
        val colorScheme = when (theme) {
            "system" ->
                if (isSystemInDarkTheme())
                    dynamicDarkColorScheme(LocalContext.current)
                else
                    dynamicLightColorScheme(LocalContext.current)
            "dark" ->
                dynamicDarkColorScheme(LocalContext.current)
            else ->
                dynamicLightColorScheme(LocalContext.current) // Default to light theme
        }

        MaterialTheme(
            colorScheme = colorScheme
        ) {
            val isMockDataEnabled = appSettingsManager.isMockDataEnabled()

            if (isMockDataEnabled) {
                // show a yellow border when mock data is enabled
                Modifier.border(2.dp, Color(0xFFFFD700))
            } else {
                Modifier
            }.let { modifier ->
                Navigation(
                    navController,
                    PaddingValues(0.dp),
                    snackbarHostState,
                    modifier)
            }
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

@Preview(showBackground =true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        AppContent()
    }
}