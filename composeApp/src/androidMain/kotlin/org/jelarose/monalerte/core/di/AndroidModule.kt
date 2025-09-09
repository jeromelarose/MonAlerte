package org.jelarose.monalerte.core.di

import org.koin.dsl.module
import org.jelarose.monalerte.core.utils.SharedDataStore
import org.jelarose.monalerte.core.utils.createDataStore

/**
 * Module Koin spécifique Android
 */
val androidModule = module {
    
    // SharedDataStore - Singleton avec Context Android
    single<SharedDataStore> { 
        SharedDataStore(createDataStore(get())) 
    }
}