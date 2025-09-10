package org.jelarose.monalerte.features.home.domain

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.PermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implémentation MediaPermissionManager utilisant MOKO Permissions
 * Cette implémentation utilise la bibliothèque KMP officielle MOKO Permissions
 * recommandée dans KMP_LIBRARIES_REFERENCE.md
 */
class MokoMediaPermissionManager(
    private val permissionsController: PermissionsController
) : MediaPermissionManager {
    
    private val _hasAudioPermission = MutableStateFlow(false)
    override val hasAudioPermission: StateFlow<Boolean> = _hasAudioPermission.asStateFlow()
    
    private val _hasCameraPermission = MutableStateFlow(false)
    override val hasCameraPermission: StateFlow<Boolean> = _hasCameraPermission.asStateFlow()
    
    init {
        println("🔐 MokoMediaPermissionManager: Initialized")
    }
    
    override suspend fun isPermissionGranted(permission: String): Boolean {
        val mokoPermission = when (permission) {
            AUDIO_PERMISSION -> Permission.RECORD_AUDIO
            CAMERA_PERMISSION -> Permission.CAMERA
            else -> return false
        }
        
        val isGranted = permissionsController.getPermissionState(mokoPermission) == PermissionState.Granted
        println("🔐 MokoMediaPermissionManager: Permission $permission = $isGranted")
        return isGranted
    }
    
    override suspend fun requestPermission(permission: String): Boolean {
        val mokoPermission = when (permission) {
            AUDIO_PERMISSION -> Permission.RECORD_AUDIO
            CAMERA_PERMISSION -> Permission.CAMERA
            else -> {
                println("🚨 MokoMediaPermissionManager: Unknown permission: $permission")
                return false
            }
        }
        
        println("🔐 MokoMediaPermissionManager: Requesting permission $permission")
        
        return try {
            // Vérifier d'abord l'état actuel
            var granted = permissionsController.getPermissionState(mokoPermission) == PermissionState.Granted
            
            // Si pas accordé, demander la permission
            if (!granted) {
                println("🔐 MokoMediaPermissionManager: Permission not granted, requesting...")
                permissionsController.providePermission(mokoPermission)
                
                // Attendre un peu puis vérifier à nouveau l'état 
                kotlinx.coroutines.delay(100)
                granted = permissionsController.getPermissionState(mokoPermission) == PermissionState.Granted
                println("🔐 MokoMediaPermissionManager: Permission state after request: $granted")
            }
            
            // Mettre à jour les StateFlow avec l'état final
            when (permission) {
                AUDIO_PERMISSION -> {
                    _hasAudioPermission.value = granted
                    println("🔐 MokoMediaPermissionManager: Audio permission state updated to: $granted")
                }
                CAMERA_PERMISSION -> {
                    _hasCameraPermission.value = granted
                    println("🔐 MokoMediaPermissionManager: Camera permission state updated to: $granted")
                }
            }
            
            println("🔐 MokoMediaPermissionManager: Permission $permission final result: $granted")
            granted
        } catch (e: Exception) {
            println("🚨 MokoMediaPermissionManager: Permission request failed: ${e.message}")
            false
        }
    }
    
    override suspend fun refreshPermissions() {
        _hasAudioPermission.value = permissionsController.getPermissionState(Permission.RECORD_AUDIO) == PermissionState.Granted
        _hasCameraPermission.value = permissionsController.getPermissionState(Permission.CAMERA) == PermissionState.Granted
        println("🔐 MokoMediaPermissionManager: Permissions refreshed - Audio: ${_hasAudioPermission.value}, Camera: ${_hasCameraPermission.value}")
    }
    
    companion object {
        const val AUDIO_PERMISSION = "android.permission.RECORD_AUDIO"
        const val CAMERA_PERMISSION = "android.permission.CAMERA"
    }
}