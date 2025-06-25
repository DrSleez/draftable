package de.karelwhite.draftable.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun CreateTournamentScreen(navController: NavController) { // Pass NavController to navigate back or to other screens
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create New Tournament Screen")
        // Add your UI for creating a tournament here
        Button(onClick = { navController.popBackStack() }) { // Example: Navigate back
            Text("Go Back")
        }
    }
}

