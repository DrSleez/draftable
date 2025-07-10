package de.karelwhite.draftable.domain.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.karelwhite.draftable.domain.viewmodel.tournamentdetails.TournamentDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailsScreen(
    navController: NavController,
    viewModel: TournamentDetailsViewModel,
){
    val uiState by viewModel.uiState.collectAsState()

    // Dein UI-Code, der auf uiState.tournament, uiState.isLoading, uiState.error reagiert
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(uiState.tournament?.name ?: "Turnierdetails") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(
                        Alignment.Center
                    ))
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.tournament != null -> {
                    // Zeige hier die Turnierdetails an
                    // z.B. TournamentDetailsContent(tournament = uiState.tournament!!, onEvent = viewModel::onEvent)
                    Text("Turnier: ${uiState.tournament!!.name}")
                    // ... weitere Details und interaktive Elemente
                }
                else -> {
                    // Dieser Fall sollte idealerweise durch isLoading oder error abgedeckt sein,
                    // aber als Fallback:
                    Text("Keine Turnierdaten verfügbar.", modifier = Modifier.align(
                        Alignment.Center
                    ))
                }
            }
        }
    }
}