package org.jelarose.monalerte.core.di

import org.jelarose.monalerte.core.utils.SharedDataStore
import org.jelarose.monalerte.core.utils.createDataStore
import org.koin.dsl.module

/**
 * iOS specific dependencies module
 */
fun iosPlatformModule() = module {
    single<SharedDataStore> { 
        SharedDataStore(createDataStore())
    }
}