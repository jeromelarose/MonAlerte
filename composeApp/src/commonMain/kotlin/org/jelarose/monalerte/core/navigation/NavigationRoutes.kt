package org.jelarose.monalerte.core.navigation

/**
 * Définition des routes de navigation de l'application
 */
object NavigationRoutes {
    const val TEST = "test"
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val LOGIN = "login"
    const val REGISTER = "register"
    
    // Route avec paramètres (exemple)
    const val ALERT_DETAIL = "alert/{alertId}"
    fun alertDetail(alertId: String) = "alert/$alertId"
}