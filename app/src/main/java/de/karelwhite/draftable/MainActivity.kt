package de.karelwhite.draftable


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import de.karelwhite.draftable.ui.theme.DraftableTheme
import de.karelwhite.draftable.view.CreateTournamentScreen
import de.karelwhite.draftable.view.HostTournamentScreen
import de.karelwhite.draftable.view.MyTournamentsScreen
import de.karelwhite.draftable.view.SettingsScreen
import de.karelwhite.draftable.view.SharedTournamentScreen
import de.karelwhite.draftable.view.StartScreen
import de.karelwhite.draftable.viewmodel.createtournament.CreateTournamentViewModel
import de.karelwhite.draftable.viewmodel.mytournaments.MyTournamentsViewModel
import de.karelwhite.draftable.viewmodel.settings.SettingsViewModel
import de.karelwhite.draftable.viewmodel.start.StartViewModel


// MainActivity.kt
@AndroidEntryPoint
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
            val startViewModel: StartViewModel = hiltViewModel()
            StartScreen(startViewModel,navController)
        }
        composable(route = AppDestinations.CREATE_TOURNAMENT_ROUTE) {
            val createTournamentViewModel: CreateTournamentViewModel = hiltViewModel()
            CreateTournamentScreen(createTournamentViewModel ,navController)
        }
        composable(route = AppDestinations.HOST_TOURNAMENTS) {
            val myTournamentsViewModel: MyTournamentsViewModel = hiltViewModel()
            MyTournamentsScreen(myTournamentsViewModel, navController)
        }
        composable(route = AppDestinations.SHARED_TOURNAMENTS) {
            SharedTournamentScreen(navController)
        }
        composable(route = AppDestinations.SETTINGS_ROUTE) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(settingsViewModel,navController)
        }

        // Add more composable(route = ...) blocks for other screens
    }
}