package de.karelwhite.draftable.domain.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import de.karelwhite.draftable.domain.model.Match
import de.karelwhite.draftable.domain.model.Player
import de.karelwhite.draftable.domain.model.Tournament
import de.karelwhite.draftable.domain.businesslogic.viewmodel.tournamentdetails.TournamentDetailsEvent
import de.karelwhite.draftable.domain.viewmodel.tournamentdetails.TournamentDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailsScreen(
    navController: NavController,
    viewModel: TournamentDetailsViewModel,
){
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isMatchResultDialogVisible && uiState.selectedMatchForDialog != null && uiState.tournament != null) {
        val selectedMatch = uiState.selectedMatchForDialog!!
        val tournament = uiState.tournament!!

        val player1 = tournament.players.find { it.id == selectedMatch.player1Id }
        val player2 = tournament.players.find { it.id == selectedMatch.player2Id }

        // Verwende den neuen Bo3-spezifischen Dialog
        MatchResultDialogBo3(
            player1Name = player1?.name ?: "Spieler 1",
            player2Name = player2?.name ?: "Spieler 2",
            onDismissRequest = {
                viewModel.onEvent(TournamentDetailsEvent.HideMatchResultDialog)
            },
            onSubmitResult = { p1Wins, p2Wins, draws ->
                viewModel.onEvent(TournamentDetailsEvent.SubmitMatchResult(
                    player1Wins = p1Wins,
                    player2Wins = p2Wins,
                    draws = draws
                ))
            }
        )
    }

    Scaffold(
        topBar = {
            TournamentDetailsTopAppBar(
                tournamentName = uiState.tournament?.name,
                isLeaderboardVisible = uiState.isLeaderboardVisible,
                onToggleLeaderboard = {
                    if (uiState.isLeaderboardVisible) {
                        viewModel.onEvent(TournamentDetailsEvent.HideLeaderboard)
                    } else {
                        viewModel.onEvent(TournamentDetailsEvent.ShowLeaderboard)
                    }
                }
            )
        },
        bottomBar = { // HIER WIRD DIE BOTTOM BAR EINGEFÜGT
            uiState.tournament?.let { tournament ->
                if (!tournament.isFinished) {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.surface, // Passende Hintergrundfarbe
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        TournamentActionButton(
                            tournament = tournament,
                            onEvent = viewModel::onEvent,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) { // Box für das Leaderboard-Overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    uiState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                            CircularProgressIndicator()
                        }
                    }
                    uiState.error != null -> {
                        ErrorStateView(errorMessage = uiState.error!!)
                    }
                    uiState.tournament == null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                            Text(
                                "Keine Turnierdaten verfügbar.",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    else -> {
                        TournamentContent(
                            tournament = uiState.tournament!!, viewModel = viewModel
                        )
                    }
                }
            }
            LeaderboardPopup(
                isVisible = uiState.isLeaderboardVisible,
                players = uiState.tournament?.players?.sortedByDescending { it.score } ?: emptyList(),
                onDismiss = { viewModel.onEvent(TournamentDetailsEvent.HideLeaderboard) }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailsTopAppBar(
    tournamentName: String?,
    isLeaderboardVisible: Boolean,
    onToggleLeaderboard: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = tournamentName ?: "Turnierdetails",
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = onToggleLeaderboard) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = "Leaderboard",
                    tint = if (isLeaderboardVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
fun ErrorStateView(errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = "Fehler",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun TournamentContent(
    tournament: Tournament,
    viewModel: TournamentDetailsViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (!tournament.isStarted) {
            NotStartedInfoView()
        } else {
            Text(
                text = if (tournament.currentRound > 0) "Runde ${tournament.currentRound}" else "Turnier gestartet",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            if (tournament.isFinished) {
                Text(
                    "Letzte Runde",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )
            }
            // Die Matches nehmen den Rest des verfügbaren Platzes in dieser Column ein
            MatchesListView(
                matches = if (tournament.currentRound > 0)
                    tournament.matches?.filter { it.roundNumber == tournament.currentRound } ?: emptyList()
                else
                    emptyList(),
                players = tournament.players,
                onMatchClick = { selectedMatch -> //
                    viewModel.onEvent(TournamentDetailsEvent.ShowMatchResultDialog(selectedMatch))
                }
            )
        }
    }
}

@Composable
fun NotStartedInfoView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "Das Turnier wurde noch nicht gestartet.",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun TournamentActionButton(
    tournament: Tournament,
    onEvent: (TournamentDetailsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonText: String
    val buttonIcon = if (!tournament.isStarted) Icons.Filled.PlayArrow else Icons.Filled.PlayArrow
    val action: TournamentDetailsEvent

    when {
        !tournament.isStarted -> {
            buttonText = "Turnier starten & Runde 1 erstellen"
            action = TournamentDetailsEvent.StartTournament
        }
        tournament.currentRound == 0 && tournament.isStarted -> {
            buttonText = "Runde 1 erstellen"
            action = TournamentDetailsEvent.GoNextRound
        }
        tournament.currentRound < tournament.numberOfRounds -> {
            buttonText = "Nächste Runde (${tournament.currentRound + 1})"
            action = TournamentDetailsEvent.GoNextRound
        }
        else -> {
            return
        }
    }

    val isEnabled = if (!tournament.isStarted) {
        tournament.players.filterNot { it.isDropped }.size >= 2
    } else if (tournament.currentRound > 0) {
        val currentRoundMatches = tournament.matches?.filter { it.roundNumber == tournament.currentRound } ?: emptyList()
        currentRoundMatches.isNotEmpty() && currentRoundMatches.all { it.isFinished || it.player2Id == null }
    } else {
        true
    }

    Button(
        onClick = { onEvent(action) },
        modifier = modifier.heightIn(min = 48.dp), // Sorgt für eine gute Mindesthöhe in der BottomBar
        enabled = isEnabled
    ) {
        Icon(imageVector = buttonIcon, contentDescription = null, modifier = Modifier.size(
            ButtonDefaults.IconSize))
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(buttonText, style = MaterialTheme.typography.titleMedium)
    }
}


@Composable
fun MatchesListView(
    matches: List<Match>,
    players: List<Player>,
    onMatchClick: (Match) -> Unit
) {
    if (matches.isEmpty() && players.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Noch keine Matches für diese Runde vorhanden.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }
    if (players.isEmpty() && matches.isEmpty()){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Dem Turnier wurden noch keine Spieler hinzugefügt.", // Oder generischer Text
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
    ) {
        items(items = matches, key = { it.id }) { match ->
            MatchCard(
                match = match,
                player1Name = players.find { it.id == match.player1Id }?.name ?: "N/A",
                player2Name = if (match.player2Id != null) players.find { it.id == match.player2Id }?.name ?: "N/A" else "FREILOS",
                onClick = { onMatchClick(match) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchCard(
    match: Match,
    player1Name: String,
    player2Name: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = player1Name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                maxLines = 2
            )

            if (match.isFinished) {
                Text(
                    text = "${match.player1Wins} : ${match.player2Wins}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            } else {
                Text(
                    "vs",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Text(
                text = player2Name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                maxLines = 2
            )
        }
        if (match.isFinished && match.player2Id != null) {
            Text(
                text = "Beendet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            )
        }
    }
}


@Composable
fun LeaderboardPopup(
    isVisible: Boolean,
    players: List<Player>,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(dismissOnClickOutside = true, usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.75f),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Leaderboard",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp)
                    )
                    if (players.isEmpty()) {
                        Text("Noch keine Spielerdaten für das Leaderboard vorhanden.")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(items = players, key = { it.id }) { player ->
                                PlayerLeaderboardCard(player = player)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerLeaderboardCard(player: Player) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(player.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1.0f))
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text("P: ${player.score}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    Text("S: ${player.matchesWon}", style = MaterialTheme.typography.bodySmall, color = Color.Green.copy(alpha = 0.9f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("N: ${player.matchesLost}", style = MaterialTheme.typography.bodySmall, color = Color.Red.copy(alpha = 0.9f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("U: ${player.matchesDrawn}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// Enum oder Sealed Class für die möglichen Ergebnisse in einem Bo3
sealed class Bo3Result(val p1Wins: Int, val p2Wins: Int, val draws: Int, val displayText: String) {
    object P1_WINS_2_0 : Bo3Result(2, 0, 0, "2 - 0")
    object P1_WINS_2_1 : Bo3Result(2, 1, 0, "2 - 1")
    object P2_WINS_2_0 : Bo3Result(0, 2, 0, "0 - 2")
    object P2_WINS_2_1 : Bo3Result(1, 2, 0, "1 - 2")
    object DRAW_1_1 : Bo3Result(1, 1, 1, "1 - 1 (Unentschieden)") // Ein Draw ist immer 1-1 und setzt draws = 1
}

val bo3ResultsList = listOf(
    Bo3Result.P1_WINS_2_0,
    Bo3Result.P1_WINS_2_1,
    Bo3Result.P2_WINS_2_0,
    Bo3Result.P2_WINS_2_1,
    Bo3Result.DRAW_1_1
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchResultDialogBo3(
    player1Name: String,
    player2Name: String,
    onDismissRequest: () -> Unit,
    onSubmitResult: (player1Wins: Int, player2Wins: Int, draws: Int) -> Unit
) {
    var selectedResult by remember { mutableStateOf<Bo3Result?>(null) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .selectableGroup(), // Für RadioButton-Gruppierung
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ergebnis eingeben (Best of 3)",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "$player1Name vs $player2Name",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // RadioButtons für jedes mögliche Ergebnis
                bo3ResultsList.forEach { result ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (selectedResult == result),
                                onClick = { selectedResult = result },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedResult == result),
                            onClick = null // onClick wird vom Row-Handler übernommen
                        )
                        Text(
                            text = when (result) { // Ersetze "P1" und "P2" durch tatsächliche Namen
                                is Bo3Result.P1_WINS_2_0 -> "$player1Name gewinnt 2-0"
                                is Bo3Result.P1_WINS_2_1 -> "$player1Name gewinnt 2-1"
                                is Bo3Result.P2_WINS_2_0 -> "$player2Name gewinnt 2-0"
                                is Bo3Result.P2_WINS_2_1 -> "$player2Name gewinnt 2-1"
                                is Bo3Result.DRAW_1_1 -> "Unentschieden 1-1"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Abbrechen")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            selectedResult?.let {
                                onSubmitResult(it.p1Wins, it.p2Wins, it.draws)
                            }
                        },
                        enabled = selectedResult != null // Button nur aktiv, wenn ein Ergebnis gewählt wurde
                    ) {
                        Text("Speichern")
                    }
                }
            }
        }
    }
}
