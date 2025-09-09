package org.jelarose.monalerte.core.di

import org.jelarose.monalerte.features.test.presentation.TestViewModel

/**
 * AppContainer simple sans Koin pour compatibilité iOS
 */
expect object AppContainer {
    val testViewModel: TestViewModel
}