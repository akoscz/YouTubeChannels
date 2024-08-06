package com.akoscz.youtubechannels.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.akoscz.youtubechannels.ui.screens.ChannelDetailsScreen
import com.akoscz.youtubechannels.ui.screens.FollowedChannelsScreen
import com.akoscz.youtubechannels.ui.screens.HomeScreen
import com.akoscz.youtubechannels.ui.screens.SearchChannelsScreen

/**
 * Enum class for navigation screen routes and screen titles
 */
enum class NavigationScreens(val route: String, val title: String) {
    Home("home", "Home"),
    Search("search", "Search Channels"),
    Following("following", "Following Channels"),
    ChannelDetails("channel_details/{channelId} {channelTitle}", "Channel Details")
}

@Composable
fun Navigation(
    navController: NavHostController,
    innerPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationScreens.Home.route,
        modifier = modifier.padding(innerPadding),
    ) {
        composable(NavigationScreens.Home.route) { HomeScreen(snackbarHostState, navController) }
        composable(NavigationScreens.Search.route) { SearchChannelsScreen(snackbarHostState, navController) }
        composable(NavigationScreens.Following.route) { FollowedChannelsScreen(snackbarHostState, navController) }
        composable(
            route = NavigationScreens.ChannelDetails.route,
            arguments = listOf(
                navArgument("channelId") { type = NavType.StringType },
                navArgument("channelTitle") { type = NavType.StringType })
        ) { backStackEntry ->
            val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
            val channelTitle = backStackEntry.arguments?.getString("channelTitle") ?: ""
            ChannelDetailsScreen(channelId, channelTitle, snackbarHostState, navController)
        }
    }
}