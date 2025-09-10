package org.jelarose.monalerte.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Navigation stable pour KMP (compatible iOS + Android)
 */
enum class Screen {
    PrivacyPolicy, Home, Test, Settings, Login, Register, ForgotPassword
}

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

@Composable
fun rememberStableNavController(): StableNavController {
    return remember { StableNavController() }
}