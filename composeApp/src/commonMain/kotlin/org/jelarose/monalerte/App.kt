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
import org.jelarose.monalerte.core.navigation.AppNavigationHost
import org.jelarose.monalerte.core.theme.MonAlerteTheme
import org.jelarose.monalerte.features.test.presentation.TestScreen
import org.jelarose.monalerte.features.test.presentation.TestViewModel
import org.jelarose.monalerte.features.auth.presentation.screens.SharedLoginScreen
import org.jelarose.monalerte.features.auth.presentation.screens.SharedRegisterScreen
import org.jelarose.monalerte.features.auth.presentation.screens.SharedForgotPasswordScreen
import org.jelarose.monalerte.features.auth.presentation.screens.SharedPrivacyPolicyScreen
import org.jelarose.monalerte.features.auth.presentation.viewmodels.SharedAuthViewModel
import org.jelarose.monalerte.core.policy.PolicyManager
import androidx.compose.runtime.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    MonAlerteTheme {
        // Navigation hybride avec feature flag
        AppNavigationHost()
    }
}

@Composable
fun AppNavHost(
    navController: StableNavController
) {
    val policyManager: PolicyManager = koinInject()
    
    // Check policy status on app start
    LaunchedEffect(Unit) {
        policyManager.checkPolicyStatus()
        val isPolicyAccepted = policyManager.isPolicyAccepted()
        if (isPolicyAccepted) {
            navController.navigate(Screen.Login)
        } else {
            navController.navigate(Screen.PrivacyPolicy)
        }
    }
    
    when (navController.currentScreen) {
        Screen.PrivacyPolicy -> {
            SharedPrivacyPolicyScreen(
                policyManager = policyManager,
                onAccept = {
                    // Accept policy and navigate to login
                    kotlinx.coroutines.MainScope().launch {
                        policyManager.acceptPolicy()
                        navController.navigate(Screen.Login)
                    }
                },
                onDecline = {
                    // User declined policy - should exit app
                    // For now, just stay on policy screen
                }
            )
        }
        Screen.Login -> {
            val authViewModel: SharedAuthViewModel = koinInject()
            SharedLoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home)
                },
                onSwitchToRegister = {
                    navController.navigate(Screen.Register)
                },
                onForgotPassword = {
                    navController.navigate(Screen.ForgotPassword)
                }
            )
        }
        
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
        
        Screen.Register -> {
            val authViewModel: SharedAuthViewModel = koinInject()
            SharedRegisterScreen(
                viewModel = authViewModel,
                onRegistrationSuccess = {
                    // Registration success is handled by the dialog, then redirects to login
                },
                onSwitchToLogin = {
                    navController.navigate(Screen.Login)
                }
            )
        }
        
        Screen.ForgotPassword -> {
            val authViewModel: SharedAuthViewModel = koinInject()
            SharedForgotPasswordScreen(
                viewModel = authViewModel,
                onBack = {
                    navController.navigate(Screen.Login)
                }
            )
        }
        
        Screen.Settings -> {
            PlaceholderScreen("Paramètres")
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