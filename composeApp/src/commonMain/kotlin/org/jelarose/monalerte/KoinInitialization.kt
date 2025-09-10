package org.jelarose.monalerte

import org.jelarose.monalerte.core.di.appModule
import org.koin.core.context.startKoin

// Global flag to track if Koin has been initialized
private var isKoinInitialized = false

/**
 * Initialize Koin for all platforms
 * Platform-specific modules are handled via expect/actual
 */
fun initKoinMultiplatform() {
    if (!isKoinInitialized) {
        startKoin {
            modules(appModule, getPlatformModule())
        }
        isKoinInitialized = true
    }
}

/**
 * Ensure Koin is initialized, initialize if not already done
 * Safe to call multiple times
 */
fun ensureKoinInitialized() {
    initKoinMultiplatform()
}

/**
 * Platform-specific module provider
 * Implementation is platform-specific
 */
expect fun getPlatformModule(): org.koin.core.module.Module