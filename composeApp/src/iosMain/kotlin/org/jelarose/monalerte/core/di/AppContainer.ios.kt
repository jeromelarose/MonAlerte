package org.jelarose.monalerte.core.di

import org.jelarose.monalerte.core.utils.SharedDataStore
import org.jelarose.monalerte.core.utils.createDataStore
import org.jelarose.monalerte.features.test.presentation.TestViewModel

/**
 * AppContainer iOS sans Context
 */
actual object AppContainer {
    private val dataStore by lazy { SharedDataStore(createDataStore()) }
    
    actual val testViewModel: TestViewModel by lazy {
        TestViewModel(dataStore)
    }
}