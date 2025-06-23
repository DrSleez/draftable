package de.karelwhite.draftable.view


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.karelwhite.draftable.R
import de.karelwhite.draftable.ui.theme.DraftableTheme

// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DraftableTheme {
                TournamentStartScreen(
                    onNavigateToCreateTournament = { /* TODO: Navigate to create screen */ },
                    onNavigateToViewTournaments = { /* TODO: Navigate to view/load screen */ },
                    onNavigateToTournamentHistory = { /* TODO: Navigate to history screen */ },
                    onNavigateToSettings = { /* TODO: Navigate to settings screen */ }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentStartScreen(
    onNavigateToCreateTournament: () -> Unit,
    onNavigateToViewTournaments: () -> Unit,
    onNavigateToTournamentHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Draftable") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp), // Overall padding for content
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.draftable),
                contentDescription = "Draftable App Logo", // Provide a meaningful description
                modifier = Modifier
                .size(120.dp)
            )
            // 1. App Title
            Text(
                text = "Draftable",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 2. Tagline (Optional)
            Text(
                text = "Drafting made easy",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 48.dp) // More space before buttons
            )

            // 3. Primary Actions
            ActionButton(
                text = "Neues Turnier erstellen",
                icon = Icons.Filled.AddCircle,
                onClick = onNavigateToCreateTournament,
                modifier = Modifier.fillMaxWidth(0.8f) // Make buttons a bit wider
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActionButton(
                text = "Trete einem Turnier bei",
                icon = Icons.Filled.Share,
                onClick = onNavigateToViewTournaments,
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActionButton(
                text = "Vergangene Turniere",
                icon = Icons.Filled.Search,
                onClick = onNavigateToTournamentHistory,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp), // Give buttons a good touch target size
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Decorative icon
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(text, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun TournamentStartScreenPreview() {
    DraftableTheme {
        TournamentStartScreen({}, {}, {} ,{})
    }
}