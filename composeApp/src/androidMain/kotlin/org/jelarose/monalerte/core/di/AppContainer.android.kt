package org.jelarose.monalerte.core.di

import android.content.Context
import org.jelarose.monalerte.core.utils.SharedDataStore
import org.jelarose.monalerte.core.utils.createDataStore
import org.jelarose.monalerte.features.test.presentation.TestViewModel

/**
 * AppContainer Android avec Context
 */
actual object AppContainer {
    private lateinit var context: Context
    
    private val dataStore by lazy { 
        SharedDataStore(createDataStore(context)) 
    }
    
    actual val testViewModel: TestViewModel by lazy {
        TestViewModel(dataStore)
    }
    
    fun init(context: Context) {
        this.context = context.applicationContext
    }
}