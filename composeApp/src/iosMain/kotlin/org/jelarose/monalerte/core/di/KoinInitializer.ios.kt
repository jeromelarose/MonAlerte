package org.jelarose.monalerte.core.di

import org.koin.core.context.startKoin

/**
 * Initialisation de Koin pour iOS
 */
actual fun initializeKoin() {
    startKoin {
        modules(
            appModule,  // Module commun
            iosModule   // Module iOS spécifique  
        )
    }
}