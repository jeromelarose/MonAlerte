package org.jelarose.monalerte.features.home.domain

import kotlinx.coroutines.flow.StateFlow
import org.jelarose.monalerte.features.home.presentation.viewmodels.WatchModeSecurityOption

/**
 * Interface pour la gestion des permissions média cross-platform
 * Reproduit le comportement exact du MediaPermissionManager natif Android
 */
interface MediaPermissionManager {
    
    /**
     * État des permissions média en temps réel
     */
    val hasAudioPermission: StateFlow<Boolean>
    val hasCameraPermission: StateFlow<Boolean>
    
    /**
     * Vérifie si une permission est accordée
     */
    suspend fun isPermissionGranted(permission: String): Boolean
    
    /**
     * Demande une permission système
     */
    suspend fun requestPermission(permission: String): Boolean
    
    /**
     * Rafraîchit l'état des permissions depuis le système
     */
    suspend fun refreshPermissions()
}

/**
 * Résultat de la gestion des permissions média
 */
sealed class MediaPermissionResult {
    object PermissionGranted : MediaPermissionResult()
    data class PermissionRequired(val permission: String, val reason: String) : MediaPermissionResult()
    object AudioRequiredForVideo : MediaPermissionResult()
    object NonMediaOption : MediaPermissionResult()
}

/**
 * Manager pour la logique de dépendance audio/vidéo
 * Reproduit exactement le comportement du code natif Android
 */
class WatchModeMediaLogic(
    private val permissionManager: MediaPermissionManager
) {
    
    companion object {
        const val AUDIO_PERMISSION = MokoMediaPermissionManager.AUDIO_PERMISSION
        const val CAMERA_PERMISSION = MokoMediaPermissionManager.CAMERA_PERMISSION
    }
    
    /**
     * Valide l'activation d'une option média selon les règles métier :
     * - Audio : Vérifier permission microphone
     * - Vidéo : Vérifier que l'audio est activé ET a les permissions + permission caméra
     */
    suspend fun validateMediaOption(
        option: WatchModeSecurityOption,
        isEnabled: Boolean,
        currentAudioEnabled: Boolean
    ): MediaPermissionResult {
        if (!isEnabled) {
            // Désactivation toujours autorisée
            return MediaPermissionResult.PermissionGranted
        }
        
        return when (option) {
            WatchModeSecurityOption.AUDIO_RECORDING -> {
                validateAudioActivation()
            }
            WatchModeSecurityOption.VIDEO_RECORDING -> {
                validateVideoActivation(currentAudioEnabled)
            }
            else -> MediaPermissionResult.NonMediaOption
        }
    }
    
    /**
     * Validation spécifique pour l'activation audio
     */
    private suspend fun validateAudioActivation(): MediaPermissionResult {
        val hasPermission = permissionManager.isPermissionGranted(AUDIO_PERMISSION)
        return if (hasPermission) {
            MediaPermissionResult.PermissionGranted
        } else {
            MediaPermissionResult.PermissionRequired(
                permission = AUDIO_PERMISSION,
                reason = "Microphone permission required for audio recording"
            )
        }
    }
    
    /**
     * Validation spécifique pour l'activation vidéo
     * Règle métier : La vidéo nécessite que l'audio soit activé ET ait les permissions
     */
    private suspend fun validateVideoActivation(currentAudioEnabled: Boolean): MediaPermissionResult {
        // Première règle : L'audio doit être activé
        if (!currentAudioEnabled) {
            return MediaPermissionResult.AudioRequiredForVideo
        }
        
        // Deuxième règle : L'audio doit avoir les permissions
        val hasAudioPermission = permissionManager.isPermissionGranted(AUDIO_PERMISSION)
        if (!hasAudioPermission) {
            return MediaPermissionResult.AudioRequiredForVideo
        }
        
        // Troisième règle : La caméra doit avoir les permissions
        val hasCameraPermission = permissionManager.isPermissionGranted(CAMERA_PERMISSION)
        return if (hasCameraPermission) {
            MediaPermissionResult.PermissionGranted
        } else {
            MediaPermissionResult.PermissionRequired(
                permission = CAMERA_PERMISSION,
                reason = "Camera permission required for video recording"
            )
        }
    }
    
    /**
     * Applique la règle de désactivation en cascade
     * Si l'audio est désactivé, la vidéo doit aussi être désactivée
     */
    fun shouldDisableVideoWithAudio(
        disablingAudio: Boolean,
        currentVideoEnabled: Boolean
    ): Boolean {
        return disablingAudio && currentVideoEnabled
    }
}