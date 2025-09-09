package org.jelarose.monalerte

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.jelarose.monalerte.core.di.appModule
import org.jelarose.monalerte.core.di.androidModule

/**
 * Application class pour initialiser Koin
 */
class MonAlerteApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialisation de Koin
        startKoin {
            // Logger Android pour debug
            androidLogger()
            
            // Context Android disponible dans les modules
            androidContext(this@MonAlerteApplication)
            
            // Modules de l'application
            modules(
                appModule,      // Module commun
                androidModule   // Module Android spécifique
            )
        }
    }
}