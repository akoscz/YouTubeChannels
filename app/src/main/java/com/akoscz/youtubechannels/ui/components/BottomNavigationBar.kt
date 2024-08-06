package com.akoscz.youtubechannels.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    var selectedItem by remember { mutableStateOf(NavigationScreens.Home) }
    val currentRoute = currentRoute(navController)

    LaunchedEffect(currentRoute) {
        selectedItem = currentRoute
    }
    
    NavigationBar {

        NavigationScreens.entries
            .filter { it != NavigationScreens.ChannelDetails } // There no navigation bar item for ChannelDetails
            .forEach { screen ->
            NavigationBarItem(
                icon = {
                    if (screen == NavigationScreens.Home) {
                        Icon(Icons.Filled.Home, contentDescription = screen.title)
                    } else if (screen == NavigationScreens.Search) {
                        Icon(Icons.Filled.Search, contentDescription = screen.title)
                    } else if (screen == NavigationScreens.Following) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = screen.title)
                    }
                },
                selected = selectedItem == screen,
                onClick = {
                    selectedItem = screen
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): NavigationScreens {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return when (navBackStackEntry?.destination?.route) {
        NavigationScreens.Home.route -> NavigationScreens.Home
        NavigationScreens.Search.route -> NavigationScreens.Search
        NavigationScreens.Following.route -> NavigationScreens.Following
        NavigationScreens.ChannelDetails.route -> NavigationScreens.Following // ChannelDetails is under the Following tab
        else -> NavigationScreens.Home
    }
}