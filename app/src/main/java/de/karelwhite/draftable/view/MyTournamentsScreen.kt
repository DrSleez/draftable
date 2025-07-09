package de.karelwhite.draftable.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.karelwhite.draftable.domain.model.Tournament
import de.karelwhite.draftable.viewmodel.mytournaments.MyTournamentsEvent
import de.karelwhite.draftable.viewmodel.mytournaments.MyTournamentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTournamentsScreen(viewModel: MyTournamentsViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.toastMessage) {
        uiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(MyTournamentsEvent.ToastMessageShown)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val hostName = uiState.host?.name ?: "Deine" // Annahme: host ist im uiState
                    Text("$hostName's Turniere")
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                uiState.isLoading || (uiState.host != null && uiState.isLoading && uiState.tournaments.isEmpty()) -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "Ein unbekannter Fehler ist aufgetreten.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.host == null && !uiState.isLoading -> {
                    Text(
                        text = "Kein Host gefunden.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.tournaments.isEmpty() && !uiState.isLoading -> {
                    Text(
                        text = "Du hast noch keine Turniere erstellt.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(items = uiState.tournaments, key = { tournament -> tournament.id }) { tournament ->
                            TournamentCard(
                                tournament = tournament,
                                onDeleteClicked = {
                                    viewModel.onEvent(MyTournamentsEvent.DeleteTournament(tournament.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentCard(
    tournament: Tournament,
    onDeleteClicked: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = tournament.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDeleteClicked) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Turnier löschen",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Turnierstatus basierend auf isStarted und isFinished
            val statusText = when {
                tournament.isFinished -> "Beendet"
                tournament.isStarted -> "Läuft"
                else -> "Noch nicht gestartet"
            }
            Text(statusText, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Spieler: ${tournament.players.size}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Runde nur anzeigen, wenn das Turnier gestartet und noch nicht beendet ist
            if (tournament.isStarted && !tournament.isFinished) {
                Text(
                    text = "Runde: ${tournament.currentRound} / ${tournament.numberOfRounds}",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else if (tournament.isFinished) {
                Text(
                    text = "Beendet nach ${tournament.numberOfRounds} Runden", // Oder nur "X Runden"
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}