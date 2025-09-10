package org.jelarose.monalerte.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Navigation stable pour KMP (compatible iOS + Android)
 * 
 * @deprecated Migration complète vers Voyager terminée. 
 * Ce code est conservé pour compatibilité mais n'est plus utilisé.
 * Voir VoyagerScreens.kt pour la nouvelle implémentation.
 */
enum class Screen {
    PrivacyPolicy, Home, Test, Settings, Login, Register, ForgotPassword
}

@Deprecated("Migration vers Voyager terminée", ReplaceWith("Navigator from Voyager"))
class StableNavController {
    var currentScreen by mutableStateOf(Screen.PrivacyPolicy)
        private set
    
    private val backStack = mutableListOf<Screen>()
    
    fun navigate(screen: Screen) {
        if (currentScreen != screen) {
            backStack.add(currentScreen)
            currentScreen = screen
        }
    }
    
    fun popBackStack(): Boolean {
        return if (backStack.isNotEmpty()) {
            currentScreen = backStack.removeLast()
            true
        } else {
            false
        }
    }
    
    fun canGoBack(): Boolean = backStack.isNotEmpty()
}

@Deprecated("Migration vers Voyager terminée", ReplaceWith("Navigator from Voyager"))
@Composable
fun rememberStableNavController(): StableNavController {
    return remember { StableNavController() }
}