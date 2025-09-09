package org.jelarose.monalerte.core.di

import org.koin.core.context.startKoin
import org.jelarose.monalerte.core.di.appModule

/**
 * Initialisation de Koin pour iOS
 */
fun initKoinIOS() {
    startKoin {
        modules(
            appModule,  // Module commun
            iosModule   // Module iOS spécifique  
        )
    }
}