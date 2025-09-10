package org.jelarose.monalerte.core.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jelarose.monalerte.features.auth.presentation.screens.SharedPrivacyPolicyScreen
import org.jelarose.monalerte.features.auth.presentation.screens.SharedLoginScreen
import org.jelarose.monalerte.features.auth.presentation.screens.SharedRegisterScreen
import org.jelarose.monalerte.features.auth.presentation.screens.SharedForgotPasswordScreen
import org.jelarose.monalerte.features.test.presentation.TestScreen
import org.jelarose.monalerte.features.auth.presentation.viewmodels.SharedAuthViewModel
import org.jelarose.monalerte.features.test.presentation.TestViewModel
import org.jelarose.monalerte.core.policy.PolicyManager
import org.jelarose.monalerte.core.policy.PolicyState
import org.jelarose.monalerte.core.di.koinInject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Écrans Voyager qui utilisent les composables existants
 * Fonctionnent en parallèle avec le système de navigation StableNavigation existant
 * 
 * Stratégie: Réutiliser tous les composables UI existants, 
 * seule la navigation change (callbacks au lieu de NavController)
 */

/**
 * Écran de démarrage qui vérifie le statut des politiques
 */
object StartupScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val policyManager: PolicyManager = koinInject()
        
        val policyState by policyManager.policyState.collectAsState()
        
        LaunchedEffect(Unit) {
            policyManager.checkPolicyStatus()
        }
        
        // Timeout pour éviter de rester bloqué en Loading
        LaunchedEffect(policyState) {
            if (policyState is PolicyState.Loading) {
                kotlinx.coroutines.delay(3000) // Attendre 3 secondes max
                if (policyManager.policyState.value is PolicyState.Loading) {
                    // Si toujours en Loading après 3 secondes, aller aux politiques
                    navigator.replace(PrivacyPolicyScreen)
                }
            }
        }
        
        when (policyState) {
            is PolicyState.Loading -> {
                // Afficher un écran de chargement
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Vérification des politiques...")
                    }
                }
            }
            is PolicyState.Accepted -> {
                // Politique acceptée, aller au login
                LaunchedEffect(Unit) {
                    navigator.replace(LoginScreen)
                }
            }
            is PolicyState.Required -> {
                // Politique requise, aller à l'écran de politique
                LaunchedEffect(Unit) {
                    navigator.replace(PrivacyPolicyScreen)
                }
            }
        }
    }
}

object PrivacyPolicyScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val policyManager: PolicyManager = koinInject()
        
        SharedPrivacyPolicyScreen(
            policyManager = policyManager,
            onAccept = { 
                // Accept policy and navigate to login
                MainScope().launch {
                    policyManager.acceptPolicy()
                    // Navigate directement vers le login après acceptation
                    navigator.replace(LoginScreen)
                }
            },
            onDecline = { 
                // Pour l'instant, rester sur la même page
                // Plus tard, on pourrait fermer l'app ou afficher un message
            }
        )
    }
}

object LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authViewModel: SharedAuthViewModel = koinInject()
        
        SharedLoginScreen(
            viewModel = authViewModel,
            onLoginSuccess = { 
                // Remplacer par l'écran principal après login réussi
                navigator.replace(HomeScreen) 
            },
            onSwitchToRegister = { 
                // Pousser l'écran d'inscription
                navigator.push(RegisterScreen) 
            },
            onForgotPassword = { 
                // Pousser l'écran mot de passe oublié
                navigator.push(ForgotPasswordScreen) 
            }
        )
    }
}

object RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authViewModel: SharedAuthViewModel = koinInject()
        
        SharedRegisterScreen(
            viewModel = authViewModel,
            onRegistrationSuccess = { 
                // Registration success is handled by the dialog, then redirects to login
            },
            onSwitchToLogin = { 
                // Retour à l'écran de login
                navigator.pop() 
            }
        )
    }
}

object ForgotPasswordScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authViewModel: SharedAuthViewModel = koinInject()
        
        SharedForgotPasswordScreen(
            viewModel = authViewModel,
            onBack = { 
                // Retour à l'écran de login
                navigator.pop() 
            }
        )
    }
}

object HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val testViewModel: TestViewModel = koinInject()
        
        // Pour l'instant, utiliser TestScreen comme écran principal
        // Plus tard, on pourrait créer un vrai HomeScreen
        org.jelarose.monalerte.features.test.presentation.TestScreen(
            viewModel = testViewModel,
            onNavigateBack = { 
                // Navigation vers les paramètres depuis l'écran d'accueil
                navigator.push(SettingsScreen)
            },
            showBackButton = true,
            backButtonText = "☰" // Icône menu au lieu de flèche retour
        )
    }
}

object TestScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val testViewModel: TestViewModel = koinInject()
        
        // Réutiliser le TestScreen existant
        org.jelarose.monalerte.features.test.presentation.TestScreen(
            viewModel = testViewModel,
            onNavigateBack = { navigator.pop() }
        )
    }
}

object SettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        androidx.compose.material3.Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { androidx.compose.material3.Text("Paramètres") },
                    navigationIcon = {
                        androidx.compose.material3.IconButton(onClick = { navigator.pop() }) {
                            androidx.compose.material3.Text("←")
                        }
                    }
                )
            }
        ) { paddingValues ->
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                androidx.compose.material3.Text("Écran de paramètres")
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                androidx.compose.material3.Text("TODO: Ajouter les paramètres de l'application")
            }
        }
    }
}