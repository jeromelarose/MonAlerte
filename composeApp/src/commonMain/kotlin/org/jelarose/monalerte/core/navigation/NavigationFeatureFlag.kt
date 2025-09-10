package org.jelarose.monalerte.core.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator

/**
 * Feature flag pour choisir entre StableNavigation et Voyager
 * 
 * STRATÉGIE HYBRIDE:
 * - Par défaut, utilise StableNavigation (existant, prouvé)
 * - Optionnellement, peut basculer vers Voyager
 * - Les deux systèmes coexistent sans conflit
 */
object NavigationFeatureFlag {
    // Feature flag - peut être changé pour tester Voyager
    const val USE_VOYAGER: Boolean = false // TODO: passer à true pour tester Voyager
}

@Composable
fun AppNavigationHost() {
    if (NavigationFeatureFlag.USE_VOYAGER) {
        // Navigation Voyager - moderne, avec transitions
        Navigator(screen = PrivacyPolicyScreen)
    } else {
        // Navigation StableNavigation - existante, prouvée
        val stableNavController = rememberStableNavController()
        org.jelarose.monalerte.AppNavHost(stableNavController)
    }
}