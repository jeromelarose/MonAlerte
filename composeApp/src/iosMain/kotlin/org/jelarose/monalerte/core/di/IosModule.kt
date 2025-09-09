package org.jelarose.monalerte.core.di

import org.koin.dsl.module
import org.jelarose.monalerte.core.utils.SharedDataStore
import org.jelarose.monalerte.core.utils.createDataStore

/**
 * Module Koin spécifique iOS
 */
val iosModule = module {
    
    // SharedDataStore - Singleton sans Context
    single<SharedDataStore> { 
        SharedDataStore(createDataStore()) 
    }
}