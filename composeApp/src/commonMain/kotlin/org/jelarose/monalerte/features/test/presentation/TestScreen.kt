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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import monalerte.composeapp.generated.resources.app_logo
import org.jelarose.monalerte.core.components.Greeting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreenContent(
    uiState: TestUiState,
    onDataStoreToggleChanged: (Boolean) -> Unit,
    onRoomToggleChanged: (Boolean) -> Unit,
    onShowContentChanged: (Boolean) -> Unit,
    onTestApiCall: () -> Unit,
    onNavigateBack: () -> Unit = {},
    showBackButton: Boolean = true,
    backButtonText: String = "←"
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        // TopAppBar avec bouton retour optionnel
        TopAppBar(
            title = { Text("Test Screen") },
            navigationIcon = if (showBackButton) {
                {
                    IconButton(onClick = onNavigateBack) {
                        Text(backButtonText) // Texte personnalisable 
                    }
                }
            } else {
                {}
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Logo de l'application
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MonAlerte - Application Logo",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = painterResource(Res.drawable.app_logo),
                        contentDescription = "Logo MonAlerte",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        
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
                    Text("DataStore Toggle")
                    Switch(
                        checked = uiState.dataStoreToggleEnabled,
                        onCheckedChange = onDataStoreToggleChanged
                    )
                }
                
                if (uiState.dataStoreToggleEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✅ DataStore Toggle activé !",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Card pour le toggle Room
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Room Database Test",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Room Toggle")
                    Switch(
                        checked = uiState.roomToggleEnabled,
                        onCheckedChange = onRoomToggleChanged
                    )
                }
                
                if (uiState.roomToggleEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✅ Room Toggle activé et sauvegardé en BDD !",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Card pour le test API
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "API Test (Ktor)",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onTestApiCall,
                    enabled = !uiState.isLoadingApi,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoadingApi) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                            Text("Loading...")
                        }
                    } else {
                        Text("Tester API (JSONPlaceholder)")
                    }
                }
                
                if (uiState.apiTestResult.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.apiTestResult,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
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
    onNavigateBack: () -> Unit = {},
    showBackButton: Boolean = true,
    backButtonText: String = "←" // Texte personnalisable pour le bouton
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    TestScreenContent(
        uiState = uiState,
        onDataStoreToggleChanged = { viewModel.onDataStoreToggleChanged(it) },
        onRoomToggleChanged = { viewModel.onRoomToggleChanged(it) },
        onShowContentChanged = { viewModel.onShowContentChanged(it) },
        onTestApiCall = { viewModel.testApiCall() },
        onNavigateBack = onNavigateBack,
        showBackButton = showBackButton,
        backButtonText = backButtonText
    )
}

@Preview
@Composable
fun TestScreenPreview() {
    MaterialTheme {
        TestScreenContent(
            uiState = TestUiState(
                dataStoreToggleEnabled = true, 
                roomToggleEnabled = false, 
                showContent = false,
                apiTestResult = "✅ Test API réussi!",
                isLoadingApi = false
            ),
            onDataStoreToggleChanged = {},
            onRoomToggleChanged = {},
            onShowContentChanged = {},
            onTestApiCall = {},
            onNavigateBack = {},
            showBackButton = true,
            backButtonText = "←"
        )
    }
}