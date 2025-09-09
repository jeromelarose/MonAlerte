package org.jelarose.monalerte

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jelarose.monalerte.core.di.koinInject
import org.jelarose.monalerte.core.navigation.Screen
import org.jelarose.monalerte.core.navigation.StableNavController
import org.jelarose.monalerte.core.navigation.rememberStableNavController
import org.jelarose.monalerte.core.theme.MonAlerteTheme
import org.jelarose.monalerte.features.test.presentation.TestScreen
import org.jelarose.monalerte.features.test.presentation.TestViewModel
import org.jelarose.monalerte.core.di.initializeKoin

@Composable
@Preview
fun App() {
    // Initialise Koin pour iOS (ne fait rien sur Android)
    LaunchedEffect(Unit) {
        initializeKoin()
    }
    
    val navController = rememberStableNavController()
    
    MonAlerteTheme {
        AppNavHost(navController)
    }
}

@Composable
fun AppNavHost(
    navController: StableNavController
) {
    when (navController.currentScreen) {
        Screen.Home -> {
            HomeScreen(
                onNavigateToTest = {
                    navController.navigate(Screen.Test)
                }
            )
        }
        
        Screen.Test -> {
            val testViewModel: TestViewModel = koinInject()
            TestScreen(
                viewModel = testViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        Screen.Settings -> {
            PlaceholderScreen("Paramètres")
        }
        
        Screen.Login -> {
            PlaceholderScreen("Connexion")
        }
    }
}

@Composable
fun HomeScreen(
    onNavigateToTest: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onNavigateToTest) {
            Text("Aller à Test Screen")
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Écran: $name")
    }
}