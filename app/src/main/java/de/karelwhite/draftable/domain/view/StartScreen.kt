package de.karelwhite.draftable.domain.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.karelwhite.draftable.AppDestinations
import de.karelwhite.draftable.R
import de.karelwhite.draftable.domain.ui.theme.DraftableTheme
import de.karelwhite.draftable.domain.viewmodel.start.StartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    viewModel: StartViewModel = viewModel(),
    navController: NavController,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Draftable") },
                actions = {
                    IconButton(onClick = {navController.navigate(AppDestinations.SETTINGS_ROUTE)}) {
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
                onClick = {navController.navigate(AppDestinations.CREATE_TOURNAMENT_ROUTE)},
                modifier = Modifier.fillMaxWidth(0.8f) // Make buttons a bit wider
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActionButton(
                text = "Deine Turniere",
                icon = Icons.Filled.Share,
                onClick = {navController.navigate(AppDestinations.MY_TOURNAMENTS)},
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActionButton(
                text = "Geteilte Turniere",
                icon = Icons.Filled.Search,
                onClick = {navController.navigate(AppDestinations.SHARED_TOURNAMENTS)},
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
        StartScreen(navController = NavController(LocalContext.current))
    }
}