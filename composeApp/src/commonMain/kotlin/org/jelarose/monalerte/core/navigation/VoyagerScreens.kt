package org.jelarose.monalerte.core.navigation

import androidx.compose.runtime.Composable
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
            onNavigateBack = { navigator.pop() }
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
        
        // Placeholder pour l'écran de paramètres
        // À implémenter plus tard si nécessaire
        androidx.compose.material3.Text("Settings Screen - TODO")
    }
}