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
import org.jelarose.monalerte.features.home.presentation.screens.InterfaceMenuScreen
import org.jelarose.monalerte.features.home.presentation.screens.WatchModeScreen
import org.jelarose.monalerte.features.settings.presentation.SettingsScreen
import org.jelarose.monalerte.features.auth.presentation.screens.AccountScreen
import org.jelarose.monalerte.features.auth.presentation.viewmodels.SharedAuthViewModel
import org.jelarose.monalerte.features.auth.presentation.viewmodels.AccountViewModel
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
        val authViewModel: SharedAuthViewModel = koinInject()
        
        val policyState by policyManager.policyState.collectAsState()
        var authChecked by remember { mutableStateOf(false) }
        var isAuthenticated by remember { mutableStateOf(false) }
        
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
                // Politique acceptée, vérifier l'authentification avant d'aller au login
                if (!authChecked) {
                    LaunchedEffect(Unit) {
                        println("🚀 StartupScreen: Policy accepted, checking authentication...")
                        authViewModel.checkExistingAuthentication { authenticated ->
                            println("🚀 StartupScreen: Authentication check result: $authenticated")
                            isAuthenticated = authenticated
                            authChecked = true
                        }
                    }
                    
                    // Afficher un écran de chargement pendant la vérification
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Vérification de la session...")
                        }
                    }
                } else {
                    // Une fois l'auth vérifiée, naviguer vers la bonne destination
                    LaunchedEffect(Unit) {
                        println("🚀 StartupScreen: Auth checked, navigating...")
                        if (isAuthenticated) {
                            println("🚀 StartupScreen: User authenticated, going to InterfaceMenu")
                            navigator.replace(InterfaceMenuVoyagerScreen)
                        } else {
                            println("🚀 StartupScreen: User not authenticated, going to Login")
                            navigator.replace(LoginScreen)
                        }
                    }
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
                navigator.replace(InterfaceMenuVoyagerScreen) 
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

object InterfaceMenuVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        InterfaceMenuScreen(
            onSuiviPositionClick = { 
                // TODO: Navigation vers écran de suivi de position
                navigator.push(TestScreen)
            },
            onVeilleClick = { 
                // TODO: Navigation vers ancien mode veille
                navigator.push(TestScreen)
            },
            onVeille2Click = { 
                // Navigation vers nouveau mode veille
                println("🚀 InterfaceMenu: Navigating to WatchModeVoyagerScreen")
                navigator.push(WatchModeVoyagerScreen)
            },
            onAddressSelectionClick = { 
                // TODO: Navigation vers gestion des lieux
                navigator.push(TestScreen)
            },
            onLocationHistoryClick = { 
                // TODO: Navigation vers historique de position
                navigator.push(TestScreen)
            },
            onSettingsClick = { 
                // Navigation vers les paramètres
                navigator.push(SettingsScreen)
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
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        SettingsScreen(
            onBackClick = { 
                navigator.pop()
            },
            onAccountClick = { 
                navigator.push(AccountVoyagerScreen)
            },
            onEmergencyContactsClick = { 
                // TODO: Navigation vers écran contacts d'urgence
                navigator.push(TestScreen)
            },
            onShortcutsClick = { 
                // TODO: Navigation vers écran raccourcis
                navigator.push(TestScreen)
            },
            onPermissionsClick = { 
                // TODO: Navigation vers écran permissions
                navigator.push(TestScreen)
            },
            onSmsAlertClick = { 
                // TODO: Navigation vers écran SMS
                navigator.push(TestScreen)
            },
            onWidgetConfigClick = { 
                // TODO: Navigation vers écran widget
                navigator.push(TestScreen)
            },
            onNotificationsClick = { 
                // TODO: Navigation vers écran notifications
                navigator.push(TestScreen)
            },
            onPrivacyPolicyClick = { 
                // Réutiliser l'écran de politique de confidentialité existant
                navigator.push(PrivacyPolicyScreen)
            }
        )
    }
}

object AccountVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val accountViewModel: AccountViewModel = koinInject()
        
        AccountScreen(
            viewModel = accountViewModel,
            onBackClick = { 
                navigator.pop()
            },
            onLogoutSuccess = {
                navigator.replaceAll(StartupScreen)
            }
        )
    }
}

object WatchModeVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        println("🚀 WatchModeVoyagerScreen: Content() called")
        val navigator = LocalNavigator.currentOrThrow
        
        WatchModeScreen(
            onBackClick = { 
                println("🚀 WatchModeVoyagerScreen: Back clicked")
                navigator.pop()
            },
            onSettingsClick = { 
                println("🚀 WatchModeVoyagerScreen: Settings clicked")
                navigator.push(SettingsScreen)
            }
        )
    }
}