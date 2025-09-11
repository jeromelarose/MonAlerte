package org.jelarose.monalerte.features.home.presentation.viewmodels

import org.jelarose.monalerte.core.viewmodel.SimpleViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jelarose.monalerte.core.utils.SharedDataStore
import org.jelarose.monalerte.features.home.domain.MediaPermissionManager
import org.jelarose.monalerte.features.home.domain.WatchModeMediaLogic
import org.jelarose.monalerte.features.home.domain.MokoMediaPermissionManager
import org.jelarose.monalerte.features.home.domain.MediaPermissionResult
import org.jelarose.monalerte.features.contacts.domain.repository.ContactRepository

/**
 * Security options available for watch mode configuration
 */
enum class WatchModeSecurityOption {
    AUDIO_RECORDING,
    VIDEO_RECORDING,
    ACCIDENT_DETECTION,
    SHAKE_DETECTION,
    ZONE_EXIT_DETECTION,
    SHORTCUT_DETECTION
}

/**
 * SMS Template types for different alert scenarios
 */
enum class SmsTemplateType {
    MANUAL_ALERT,
    ACCIDENT_ALERT,
    ZONE_EXIT_ALERT
}

/**
 * Events d'UI pour la gestion des permissions et des erreurs
 */
sealed class WatchModeUiEvent {
    data class ShowPermissionDialog(val permission: String, val reason: String) : WatchModeUiEvent()
    data class ShowErrorMessage(val message: String) : WatchModeUiEvent()
    data class ShowMessage(val messageKey: String) : WatchModeUiEvent()
    object AudioRequiredForVideo : WatchModeUiEvent()
}

/**
 * UI state for the Watch Mode screen
 */
data class WatchModeUiState(
    // Audio/Video recording toggles
    val isAudioRecordingEnabled: Boolean = false,
    val isVideoRecordingEnabled: Boolean = false,
    
    // Detection services toggles
    val isAccidentDetectionEnabled: Boolean = false,
    val isShakeDetectionEnabled: Boolean = false,
    val isZoneExitDetectionEnabled: Boolean = false,
    val isShortcutDetectionEnabled: Boolean = false,
    
    // SMS Configuration
    val smsTemplate: String = "",
    val emergencyContacts: List<String> = emptyList(),
    val selectedContactsCount: Int = 0,
    
    // Permission states (temps réel)
    val hasAudioPermission: Boolean = false,
    val hasVideoPermission: Boolean = false,
    val hasLocationPermission: Boolean = false,
    
    // Loading states
    val isLoadingState: Boolean = false,
    
    // UI interaction states
    val isVideoToggleEnabled: Boolean = true // Contrôle si le toggle vidéo est cliquable
)

/**
 * ViewModel for Watch Mode screen managing security settings and SMS configuration
 * Based on the original LiveMapViewModel security option handling avec gestion des permissions
 */
class WatchModeViewModel(
    private val dataStore: SharedDataStore,
    private val permissionManager: MediaPermissionManager,
    private val contactRepository: ContactRepository
) : SimpleViewModel() {
    
    private val _uiState = MutableStateFlow(WatchModeUiState())
    val uiState: StateFlow<WatchModeUiState> = _uiState.asStateFlow()
    
    // Events pour l'UI (permissions, erreurs)
    private val _uiEvents = MutableStateFlow<WatchModeUiEvent?>(null)
    val uiEvents: StateFlow<WatchModeUiEvent?> = _uiEvents.asStateFlow()
    
    // Logique de gestion des médias
    private val mediaLogic = WatchModeMediaLogic(permissionManager)
    
    init {
        viewModelScope.launch {
            // D'abord charger l'état, puis observer les permissions
            loadWatchModeState()
        }
        observePermissionChanges()
        observeContactsCount()
    }
    
    /**
     * Observer les changements de permissions en temps réel
     */
    private fun observePermissionChanges() {
        viewModelScope.launch {
            combine(
                permissionManager.hasAudioPermission,
                permissionManager.hasCameraPermission
            ) { hasAudio, hasCamera ->
                _uiState.value = _uiState.value.copy(
                    hasAudioPermission = hasAudio,
                    hasVideoPermission = hasCamera,
                    // Désactiver le toggle vidéo si pas d'audio OU pas de permissions
                    isVideoToggleEnabled = _uiState.value.isAudioRecordingEnabled && hasAudio
                )
            }.collect { }
        }
    }
    
    /**
     * Observer le nombre de contacts sélectionnés pour les alertes manuelles
     */
    private fun observeContactsCount() {
        viewModelScope.launch {
            contactRepository.getSelectedContactsByType("PARAMETER")
                .collect { contacts ->
                    _uiState.value = _uiState.value.copy(
                        selectedContactsCount = contacts.size
                    )
                }
        }
    }
    
    /**
     * Clear UI event after being handled
     */
    fun clearUiEvent() {
        _uiEvents.value = null
    }
    
    /**
     * Toggle security option avec logique de permissions et dépendances
     * Reproduit exactement le comportement du MediaPermissionManager natif
     */
    fun onSecurityOptionChanged(option: WatchModeSecurityOption, enabled: Boolean) {
        viewModelScope.launch {
            when (option) {
                WatchModeSecurityOption.AUDIO_RECORDING -> {
                    handleAudioToggle(enabled)
                }
                WatchModeSecurityOption.VIDEO_RECORDING -> {
                    handleVideoToggle(enabled)
                }
                WatchModeSecurityOption.ACCIDENT_DETECTION -> {
                    println("🚗 WatchModeViewModel: Accident detection toggle - was: ${_uiState.value.isAccidentDetectionEnabled}, now: $enabled")
                    _uiState.value = _uiState.value.copy(isAccidentDetectionEnabled = enabled)
                    // TODO: Implement accident detection service integration
                }
                WatchModeSecurityOption.SHAKE_DETECTION -> {
                    println("📱 WatchModeViewModel: Shake detection toggle - was: ${_uiState.value.isShakeDetectionEnabled}, now: $enabled")
                    _uiState.value = _uiState.value.copy(isShakeDetectionEnabled = enabled)
                    // TODO: Implement shake detection service integration
                }
                WatchModeSecurityOption.ZONE_EXIT_DETECTION -> {
                    println("🗺️ WatchModeViewModel: Zone exit detection toggle - was: ${_uiState.value.isZoneExitDetectionEnabled}, now: $enabled")
                    _uiState.value = _uiState.value.copy(isZoneExitDetectionEnabled = enabled)
                    // TODO: Implement zone exit detection service integration
                }
                WatchModeSecurityOption.SHORTCUT_DETECTION -> {
                    println("⌨️ WatchModeViewModel: Shortcut detection toggle - was: ${_uiState.value.isShortcutDetectionEnabled}, now: $enabled")
                    _uiState.value = _uiState.value.copy(isShortcutDetectionEnabled = enabled)
                    // TODO: Implement shortcut detection service integration
                }
            }
        }
    }
    
    /**
     * Gestion spécifique du toggle audio avec validation des permissions
     */
    private suspend fun handleAudioToggle(enabled: Boolean) {
        println("🔊 WatchModeViewModel: Audio toggle - was: ${_uiState.value.isAudioRecordingEnabled}, requested: $enabled")
        
        // Valider avec la logique métier
        val result = mediaLogic.validateMediaOption(
            option = WatchModeSecurityOption.AUDIO_RECORDING,
            isEnabled = enabled,
            currentAudioEnabled = _uiState.value.isAudioRecordingEnabled
        )
        
        when (result) {
            is MediaPermissionResult.PermissionGranted -> {
                // ✅ Autoriser le changement
                val shouldDisableVideo = mediaLogic.shouldDisableVideoWithAudio(
                    disablingAudio = !enabled,
                    currentVideoEnabled = _uiState.value.isVideoRecordingEnabled
                )
                
                _uiState.value = _uiState.value.copy(
                    isAudioRecordingEnabled = enabled,
                    isVideoRecordingEnabled = if (shouldDisableVideo) false else _uiState.value.isVideoRecordingEnabled,
                    isVideoToggleEnabled = enabled // Le toggle vidéo dépend de l'audio
                )
                
                // Sauvegarder dans le datastore
                dataStore.setWatchModeAudioEnabled(enabled)
                if (shouldDisableVideo) {
                    dataStore.setWatchModeVideoEnabled(false)
                }
                
                println("✅ WatchModeViewModel: Audio activated, video disabled cascade: $shouldDisableVideo")
            }
            is MediaPermissionResult.PermissionRequired -> {
                // ❌ Permission requise - montrer la demande
                println("🔐 WatchModeViewModel: Audio permission required: ${result.permission}")
                _uiEvents.value = WatchModeUiEvent.ShowPermissionDialog(result.permission, result.reason)
            }
            else -> {
                // Cas non-média, autoriser
                _uiState.value = _uiState.value.copy(isAudioRecordingEnabled = enabled)
                dataStore.setWatchModeAudioEnabled(enabled)
            }
        }
    }
    
    /**
     * Gestion spécifique du toggle vidéo avec validation des dépendances
     */
    private suspend fun handleVideoToggle(enabled: Boolean) {
        println("📹 WatchModeViewModel: Video toggle - was: ${_uiState.value.isVideoRecordingEnabled}, requested: $enabled")
        
        // Valider avec la logique métier
        val result = mediaLogic.validateMediaOption(
            option = WatchModeSecurityOption.VIDEO_RECORDING,
            isEnabled = enabled,
            currentAudioEnabled = _uiState.value.isAudioRecordingEnabled
        )
        
        when (result) {
            is MediaPermissionResult.PermissionGranted -> {
                // ✅ Autoriser le changement
                _uiState.value = _uiState.value.copy(isVideoRecordingEnabled = enabled)
                dataStore.setWatchModeVideoEnabled(enabled)
                println("✅ WatchModeViewModel: Video activated successfully")
            }
            is MediaPermissionResult.PermissionRequired -> {
                // ❌ Permission requise - montrer la demande
                println("🔐 WatchModeViewModel: Video permission required: ${result.permission}")
                _uiEvents.value = WatchModeUiEvent.ShowPermissionDialog(result.permission, result.reason)
            }
            is MediaPermissionResult.AudioRequiredForVideo -> {
                // ❌ L'audio doit être activé d'abord
                println("⚠️ WatchModeViewModel: Audio required for video activation")
                _uiEvents.value = WatchModeUiEvent.AudioRequiredForVideo
            }
            else -> {
                // Cas non-média, autoriser
                _uiState.value = _uiState.value.copy(isVideoRecordingEnabled = enabled)
                dataStore.setWatchModeVideoEnabled(enabled)
            }
        }
    }
    
    /**
     * Handle permission request result - utilise maintenant MOKO Permissions
     */
    fun onPermissionResult(permission: String, granted: Boolean) {
        viewModelScope.launch {
            if (granted) {
                // Réessayer l'activation après l'obtention de la permission
                when (permission) {
                    MokoMediaPermissionManager.AUDIO_PERMISSION -> {
                        handleAudioToggle(true)
                    }
                    MokoMediaPermissionManager.CAMERA_PERMISSION -> {
                        handleVideoToggle(true)
                    }
                }
            } else {
                // Permission refusée - montrer un message d'erreur
                val errorMessage = when (permission) {
                    MokoMediaPermissionManager.AUDIO_PERMISSION -> "Permission microphone requise pour l'enregistrement audio"
                    MokoMediaPermissionManager.CAMERA_PERMISSION -> "Permission caméra requise pour l'enregistrement vidéo"
                    else -> "Permission requise"
                }
                _uiEvents.value = WatchModeUiEvent.ShowErrorMessage(errorMessage)
            }
        }
    }
    
    /**
     * Demander une permission via MOKO - VRAIE demande système
     */
    fun requestPermission(permission: String) {
        viewModelScope.launch {
            println("🔐 WatchModeViewModel: Requesting REAL system permission: $permission")
            val granted = permissionManager.requestPermission(permission)
            onPermissionResult(permission, granted)
        }
    }
    
    /**
     * Update SMS template configuration
     */
    fun onSmsTemplateChanged(template: String) {
        println("💬 WatchModeViewModel: SMS template changed")
        _uiState.value = _uiState.value.copy(smsTemplate = template)
        viewModelScope.launch {
            dataStore.setWatchModeSmsTemplate(template)
        }
    }
    
    /**
     * Save SMS template and show confirmation message
     */
    fun saveSmsTemplate(template: String) {
        println("💬 WatchModeViewModel: Saving SMS template")
        _uiState.value = _uiState.value.copy(smsTemplate = template)
        viewModelScope.launch {
            dataStore.setWatchModeSmsTemplate(template)
            // Show success message via UI event
            _uiEvents.value = WatchModeUiEvent.ShowMessage("sms_template_saved_message")
        }
    }
    
    /**
     * Update emergency contacts list
     */
    fun onEmergencyContactsChanged(contacts: List<String>) {
        println("📱 WatchModeViewModel: Emergency contacts updated - count: ${contacts.size}")
        _uiState.value = _uiState.value.copy(emergencyContacts = contacts)
        // TODO: Implement contacts persistence
    }
    
    /**
     * Update permission states
     */
    fun onPermissionStateChanged(hasAudio: Boolean, hasVideo: Boolean, hasLocation: Boolean) {
        _uiState.value = _uiState.value.copy(
            hasAudioPermission = hasAudio,
            hasVideoPermission = hasVideo,
            hasLocationPermission = hasLocation
        )
    }
    
    /**
     * Load watch mode state from persistent storage (once at startup)
     */
    private suspend fun loadWatchModeState() {
        _uiState.value = _uiState.value.copy(isLoadingState = true)
        
        try {
            // Load initial state once (don't collect continuously to avoid conflicts)
            val audioEnabled = dataStore.watchModeAudioEnabled.first()
            val videoEnabled = dataStore.watchModeVideoEnabled.first()
            val smsTemplate = dataStore.watchModeSmsTemplate.first()
            
            // Charger aussi les permissions
            permissionManager.refreshPermissions()
            val hasAudioPermission = permissionManager.hasAudioPermission.value
            val hasCameraPermission = permissionManager.hasCameraPermission.value
            
            _uiState.value = _uiState.value.copy(
                isAudioRecordingEnabled = audioEnabled,
                isVideoRecordingEnabled = videoEnabled,
                smsTemplate = smsTemplate,
                hasAudioPermission = hasAudioPermission,
                hasVideoPermission = hasCameraPermission,
                // Calculer correctement l'état du toggle vidéo après chargement
                isVideoToggleEnabled = audioEnabled && hasAudioPermission,
                isLoadingState = false
            )
            
            println("✅ WatchModeViewModel: Initial state loaded - audio: $audioEnabled, video: $videoEnabled")
            println("✅ WatchModeViewModel: Permissions loaded - hasAudio: $hasAudioPermission, hasCamera: $hasCameraPermission")
            println("✅ WatchModeViewModel: Video toggle enabled: ${audioEnabled && hasAudioPermission}")
        } catch (e: Exception) {
            println("❌ WatchModeViewModel: Error loading state - ${e.message}")
            _uiState.value = _uiState.value.copy(isLoadingState = false)
        }
    }
}