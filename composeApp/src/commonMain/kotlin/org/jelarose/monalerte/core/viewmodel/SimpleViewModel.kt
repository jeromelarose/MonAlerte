package org.jelarose.monalerte.core.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * ViewModel simple sans lifecycle-viewmodel-compose pour compatibilité iOS
 */
open class SimpleViewModel {
    protected val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    
    open fun onCleared() {
        viewModelScope.cancel()
    }
}