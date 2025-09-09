package org.jelarose.monalerte.features.test.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import monalerte.composeapp.generated.resources.Res
import monalerte.composeapp.generated.resources.compose_multiplatform
import org.jelarose.monalerte.core.components.Greeting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreenContent(
    uiState: TestUiState,
    onToggleChanged: (Boolean) -> Unit,
    onShowContentChanged: (Boolean) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        // TopAppBar avec bouton retour
        TopAppBar(
            title = { Text("Test Screen") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Text("←") // Simple arrow text for KMP compatibility
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        
        // Card pour le toggle DataStore
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "DataStore Test",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Toggle persistant")
                    Switch(
                        checked = uiState.toggleEnabled,
                        onCheckedChange = onToggleChanged
                    )
                }
                
                if (uiState.toggleEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✅ Toggle activé et sauvegardé !",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Section originale
        Button(onClick = { onShowContentChanged(!uiState.showContent) }) {
            Text("Click me!")
        }
        
        AnimatedVisibility(uiState.showContent) {
            val greeting = remember { Greeting().greet() }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Compose: $greeting")
            }
        }
        }
    }
}

@Composable
fun TestScreen(
    viewModel: TestViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Charger l'état au démarrage (équivalent à onResume)
    LaunchedEffect(Unit) {
        viewModel.loadToggleState()
    }
    
    TestScreenContent(
        uiState = uiState,
        onToggleChanged = { viewModel.onToggleChanged(it) },
        onShowContentChanged = { viewModel.onShowContentChanged(it) },
        onNavigateBack = onNavigateBack
    )
}

@Preview
@Composable
fun TestScreenPreview() {
    MaterialTheme {
        TestScreenContent(
            uiState = TestUiState(toggleEnabled = true, showContent = false),
            onToggleChanged = {},
            onShowContentChanged = {}
        )
    }
}