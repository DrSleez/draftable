package de.karelwhite.draftable


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import de.karelwhite.draftable.domain.ui.theme.DraftableTheme
import de.karelwhite.draftable.domain.view.CreateTournamentScreen
import de.karelwhite.draftable.domain.view.MyTournamentsScreen
import de.karelwhite.draftable.domain.view.SettingsScreen
import de.karelwhite.draftable.domain.view.SharedTournamentScreen
import de.karelwhite.draftable.domain.view.StartScreen
import de.karelwhite.draftable.domain.view.TournamentDetailsScreen
import de.karelwhite.draftable.domain.viewmodel.createtournament.CreateTournamentViewModel
import de.karelwhite.draftable.domain.viewmodel.mytournaments.MyTournamentsViewModel
import de.karelwhite.draftable.domain.viewmodel.settings.SettingsViewModel
import de.karelwhite.draftable.domain.viewmodel.start.StartViewModel
import de.karelwhite.draftable.domain.viewmodel.tournamentdetails.TournamentDetailsViewModel


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
        startDestination = AppDestinations.START_SCREEN_ROUTE
    ) {
        composable(route = AppDestinations.START_SCREEN_ROUTE) {
            val startViewModel: StartViewModel = hiltViewModel()
            StartScreen(startViewModel,navController)
        }
        composable(route = AppDestinations.CREATE_TOURNAMENT_ROUTE) {
            val createTournamentViewModel: CreateTournamentViewModel = hiltViewModel()
            CreateTournamentScreen(createTournamentViewModel ,navController)
        }
        composable(route = AppDestinations.MY_TOURNAMENTS) {
            val myTournamentsViewModel: MyTournamentsViewModel = hiltViewModel()
            MyTournamentsScreen(myTournamentsViewModel, navController)
        }
        composable(
            route = AppDestinations.TOURNAMENT_DETAILS_ROUTE+"/{tournamentId}",
            arguments = listOf(
                navArgument("tournamentId") {
                    type = NavType.StringType
                })
        ) {
            backStackEntry ->
            val tournamentViewModel : TournamentDetailsViewModel = hiltViewModel()
            TournamentDetailsScreen(
                navController = navController,
                tournamentViewModel
            )
        }
        composable(route = AppDestinations.SHARED_TOURNAMENTS) {
            SharedTournamentScreen(navController)
        }
        composable(route = AppDestinations.SETTINGS_ROUTE) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(settingsViewModel,navController)
        }
    }
}