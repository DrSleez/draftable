package de.karelwhite.draftable.view


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.karelwhite.draftable.AppDestinations
import de.karelwhite.draftable.ui.theme.DraftableTheme

// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DraftableTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController() // Create a NavController instance

    NavHost(
        navController = navController,
        startDestination = AppDestinations.START_SCREEN_ROUTE // Set your initial screen
    ) {
        composable(route = AppDestinations.START_SCREEN_ROUTE) {
            StartScreen(navController)
        }
        composable(route = AppDestinations.CREATE_TOURNAMENT_ROUTE) {
            CreateTournamentScreen(navController) // Pass navController to this screen
        }
        composable(route = AppDestinations.FIND_TOURNAMENTS_ROUTE) {
            ViewTournamentsScreen(navController)
        }
        composable(route = AppDestinations.SETTINGS_ROUTE) {
            SettingsScreen(navController)
        }
        composable(route = AppDestinations.TOURNAMENT_HISTORY_ROUTE) {
            TournamentHistoryScreen(navController)
        }
        // Add more composable(route = ...) blocks for other screens
    }
}