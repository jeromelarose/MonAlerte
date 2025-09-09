package org.jelarose.monalerte.features.test.presentation

import org.jelarose.monalerte.core.viewmodel.SimpleViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jelarose.monalerte.core.utils.SharedDataStore

data class TestUiState(
    val toggleEnabled: Boolean = false,
    val showContent: Boolean = false
)

// ViewModel 100% partagé utilisant DataStore KMP
class TestViewModel(
    private val dataStore: SharedDataStore
) : SimpleViewModel() {
    
    private val _uiState = MutableStateFlow(TestUiState())
    val uiState: StateFlow<TestUiState> = _uiState.asStateFlow()
    
    init {
        loadToggleState()
    }
    
    fun onToggleChanged(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(toggleEnabled = enabled)
        viewModelScope.launch {
            dataStore.setToggleState(enabled)
        }
    }
    
    fun onShowContentChanged(show: Boolean) {
        _uiState.value = _uiState.value.copy(showContent = show)
    }
    
    fun loadToggleState() {
        viewModelScope.launch {
            dataStore.toggleState.collect { saved ->
                _uiState.value = _uiState.value.copy(toggleEnabled = saved)
            }
        }
    }
}