package com.microbond.app.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun MicrobondNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationScreen.Charts.route,
        modifier = modifier
    ) {
        composable(NavigationScreen.Charts.route) {
            PlaceholderScreen("Chats screen", "")
        }
        composable(NavigationScreen.Status.route) {
            PlaceholderScreen("Updates screen", "")
        }
        composable(NavigationScreen.Communities.route) {
            PlaceholderScreen("Communities screen", "")
        }
        composable(NavigationScreen.Call.route) {
            PlaceholderScreen("Calls screen", "")
        }
        composable(NavigationScreen.Profile.route) {
            PlaceholderScreen("You screen", "")
        }
        composable(
            route = NavigationScreen.ChartRoom.route,
            arguments = listOf(
                navArgument("contactedId") { type = NavType.StringType },
                navArgument("contactedName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val contactedId = backStackEntry.arguments?.getString("contactedId") ?: ""
            val contactedName = backStackEntry.arguments?.getString("contactedName") ?: "unknown"

            PlaceholderScreen("chat room with $contactedName", contactedId)
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, id: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = if (id.isNotEmpty()) "$title, $id" else title)
    }
}
