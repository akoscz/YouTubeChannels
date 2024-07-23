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
    val items = listOf("home", "search", "subscriptions")
    var selectedItem by remember { mutableStateOf(items[0]) }
    val currentRoute = currentRoute(navController)

    LaunchedEffect(currentRoute) {
        selectedItem = currentRoute ?: items[0]
    }
    
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        "home" -> Icon(Icons.Filled.Home, contentDescription = "Home")
                        "search" -> Icon(Icons.Filled.Search, contentDescription = "Search")
                        "subscriptions" -> Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Subscriptions")
                        else -> {}
                    }
                },
                selected = selectedItem == screen,
                onClick = {
                    selectedItem = screen
                    // Navigate based on the selected item
                    when (screen) {
                        "home" -> navController.navigate("home") {
                            launchSingleTop = true
                        }
                        "search" -> navController.navigate("search") {
                            launchSingleTop = true
                        }
                        "subscriptions" -> navController.navigate("subscriptions") {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}