package de.karelwhite.draftable.domain.view


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import de.karelwhite.draftable.AppDestinations
import de.karelwhite.draftable.domain.viewmodel.createtournament.CreateTournamentEvent
import de.karelwhite.draftable.domain.viewmodel.createtournament.CreateTournamentNavigationEvent
import de.karelwhite.draftable.domain.viewmodel.createtournament.CreateTournamentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentScreen(
    viewModel: CreateTournamentViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val lazyListState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    var currentPlayerInput by remember { mutableStateOf("") }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(CreateTournamentEvent.ToastMessageShown)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { target ->
            when (target) {
                CreateTournamentNavigationEvent.MyTournaments -> {
                    navController.navigate(AppDestinations.MY_TOURNAMENTS) {
                        popUpTo(AppDestinations.CREATE_TOURNAMENT_ROUTE) { // Die Route des aktuellen Screens
                            inclusive =
                                true // Wichtig: Setze inclusive = true, um den CreateTournamentScreen selbst zu entfernen
                        }
                        launchSingleTop = true

                    }
                }
            }
        }
    }

    LaunchedEffect(uiState.players.size) {
        if (uiState.players.isNotEmpty()) {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(index = uiState.players.size - 1)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(CreateTournamentEvent.LoadInitialCreateTournament)
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(title = { Text("Neues Turnier erstellen") })
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Berechnete Runden (Schweizer System): ${uiState.numberOfRounds}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.onEvent(CreateTournamentEvent.SaveTournament) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Turnier speichern")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = uiState.tournamentName,
                onValueChange = { viewModel.onEvent(CreateTournamentEvent.SetTournamentName(it)) },
                label = { Text("Turniername") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            uiState.createdTournamentId.let { id ->
                Text(
                    text = "ID: $id",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
            }


            Text("Spieler:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
Column(
    modifier = Modifier.weight(1f)
){
    // Liste der bereits hinzugefügten Spieler
    if (uiState.players.isNotEmpty()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .imePadding()

        ) {
            itemsIndexed(uiState.players,
                key = { index, player -> player.id })
            { index, player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}. ${player.name}", modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        viewModel.onEvent(CreateTournamentEvent.RemovePlayer(player))
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Spieler '${player.name}' entfernen")
                    }
                }
                if (index < uiState.players.size - 1) {
                    HorizontalDivider()
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    } else {
        // Optional: Platzhalter oder Nachricht, wenn keine Spieler da sind,
        // damit das Eingabefeld nicht ganz oben klebt.
        // Dieser Spacer sorgt dafür, dass das Eingabefeld unten bleibt, wenn die Liste leer ist.
        Spacer(modifier = Modifier.weight(1f))
    }


    // Eingabefeld für den nächsten Spieler
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = currentPlayerInput,
            onValueChange = { currentPlayerInput = it },
            label = { Text(if (uiState.players.isEmpty()) "Erster Spieler" else "Nächster Spieler") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (currentPlayerInput.isNotBlank()) {
                    viewModel.onEvent(CreateTournamentEvent.AddPlayerByName(currentPlayerInput))
                    currentPlayerInput = ""
                }
            })
        )
        IconButton(
            onClick = {
                if (currentPlayerInput.isNotBlank()) {
                    viewModel.onEvent(CreateTournamentEvent.AddPlayerByName(currentPlayerInput))
                    currentPlayerInput = "" // Feld zurücksetzen
                }
            },
            enabled = currentPlayerInput.isNotBlank()
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Spieler hinzufügen")
        }
    }
}

        }
    }
}