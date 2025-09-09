package org.jelarose.monalerte.features.test.presentation

import org.jelarose.monalerte.core.viewmodel.SimpleViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.jelarose.monalerte.core.utils.SharedDataStore
import org.jelarose.monalerte.core.repository.ToggleRepository
import org.jelarose.monalerte.core.network.ApiService
import org.jelarose.monalerte.core.network.Post

data class TestUiState(
    val dataStoreToggleEnabled: Boolean = false,
    val roomToggleEnabled: Boolean = false,
    val showContent: Boolean = false,
    val apiTestResult: String = "",
    val isLoadingApi: Boolean = false
)

// ViewModel 100% partagé utilisant DataStore, Room KMP et API avec Ktor
class TestViewModel(
    private val dataStore: SharedDataStore,
    private val toggleRepository: ToggleRepository,
    private val apiService: ApiService
) : SimpleViewModel() {
    
    private val _uiState = MutableStateFlow(TestUiState())
    val uiState: StateFlow<TestUiState> = _uiState.asStateFlow()
    
    init {
        loadToggleStates()
    }
    
    fun onDataStoreToggleChanged(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(dataStoreToggleEnabled = enabled)
        viewModelScope.launch {
            dataStore.setToggleState(enabled)
        }
    }
    
    fun onRoomToggleChanged(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(roomToggleEnabled = enabled)
        viewModelScope.launch {
            toggleRepository.setRoomToggleState(enabled)
        }
    }
    
    fun onShowContentChanged(show: Boolean) {
        _uiState.value = _uiState.value.copy(showContent = show)
    }
    
    fun testApiCall() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingApi = true, apiTestResult = "")
            try {
                val posts = apiService.getPosts()
                val firstPost = posts.firstOrNull()
                val result = if (firstPost != null) {
                    "✅ API Success!\n" +
                    "Posts récupérés: ${posts.size}\n" +
                    "Premier post:\n" +
                    "ID: ${firstPost.id}\n" +
                    "Titre: ${firstPost.title}\n" +
                    "Contenu: ${firstPost.body.take(100)}..."
                } else {
                    "⚠️ API Success mais aucun post trouvé"
                }
                _uiState.value = _uiState.value.copy(apiTestResult = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    apiTestResult = "❌ API Error: ${e.message}"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoadingApi = false)
            }
        }
    }
    
    private fun loadToggleStates() {
        viewModelScope.launch {
            combine(
                dataStore.toggleState,
                toggleRepository.getRoomToggleState()
            ) { dataStoreToggle, roomToggle ->
                _uiState.value = _uiState.value.copy(
                    dataStoreToggleEnabled = dataStoreToggle,
                    roomToggleEnabled = roomToggle
                )
            }.collect { }
        }
    }
}