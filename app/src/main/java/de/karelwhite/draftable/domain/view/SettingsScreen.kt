package de.karelwhite.draftable.domain.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.karelwhite.draftable.domain.viewmodel.settings.SettingsEvent
import de.karelwhite.draftable.domain.businesslogic.viewmodel.settings.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Einstellungen", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Zeige einen Ladeindikator, wenn isLoading true ist und kein Fehler vorliegt
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.error != null) {
            // Zeige eine Fehlermeldung an
            Text(
                text = "Fehler: ${uiState.error}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { viewModel.onEvent(SettingsEvent.LoadInitialSettings) }) {
                Text("Erneut versuchen")
            }
        } else if (uiState.host == null) {
            // Fall: Host existiert nicht (nachdem isLoading false ist und kein Fehler)
            Text("Kein Host konfiguriert. Ein neuer Host wurde versucht zu erstellen.")
            // Du könntest hier auch einen Button anbieten, um das Erstellen erneut zu versuchen,
            // falls der automatische Erstellungsversuch fehlschlägt und im 'error'-State landet.
        } else {
            // Host existiert und keine Fehler, zeige die Einstellungsfelder

            // Textfeld für den Host-Namen
            OutlinedTextField(
                value = uiState.name, // Verwende den 'name' aus dem uiState
                onValueChange = { newName ->
                    viewModel.onEvent(SettingsEvent.SetName(newName))
                },
                label = { Text("Host Name") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Anzeige des tatsächlichen Host-Namens (falls unterschiedlich vom Bearbeitungsfeld)
            // oder einfach zur Bestätigung:
            Text("Aktueller Host im System: ${uiState.host?.name ?: "N/A"}")
            Text("Host ID: ${uiState.host?.id ?: "N/A"}")


            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { viewModel.onEvent(SettingsEvent.SaveSettings) }) {
                Text("Einstellungen speichern")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Zurück")
        }
    }
}