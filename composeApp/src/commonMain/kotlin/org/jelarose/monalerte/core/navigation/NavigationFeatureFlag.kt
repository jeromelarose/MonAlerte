package org.jelarose.monalerte.core.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition

/**
 * Feature flag pour choisir entre StableNavigation et Voyager
 * 
 * MIGRATION COMPLÉTÉE:
 * - Voyager maintenant utilisé par défaut (migration terminée)
 * - StableNavigation conservé comme fallback de sécurité
 * - Navigation moderne avec transitions fluides
 */
object NavigationFeatureFlag {
    // Feature flag - Voyager maintenant activé par défaut
    const val USE_VOYAGER: Boolean = true // Migration complète vers Voyager
}

@Composable
fun AppNavigationHost() {
    if (NavigationFeatureFlag.USE_VOYAGER) {
        // Navigation Voyager - moderne, avec transitions fluides
        Navigator(screen = StartupScreen) { navigator ->
            FadeTransition(navigator = navigator)
        }
    } else {
        // Navigation StableNavigation - existante, prouvée
        val stableNavController = rememberStableNavController()
        org.jelarose.monalerte.AppNavHost(stableNavController)
    }
}