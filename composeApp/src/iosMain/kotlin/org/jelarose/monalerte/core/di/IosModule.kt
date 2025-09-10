package org.jelarose.monalerte.core.di

import org.koin.dsl.module
import org.jelarose.monalerte.features.home.domain.MediaPermissionManager
import org.jelarose.monalerte.features.home.domain.MokoMediaPermissionManager
import org.jelarose.monalerte.core.utils.SharedDataStore
import org.jelarose.monalerte.core.utils.createDataStore
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState

/**
 * Module spécifique iOS pour les implémentations platform-specific
 */
val iosModule = module {
    
    // SharedDataStore - Singleton pour iOS
    single<SharedDataStore> { 
        SharedDataStore(createDataStore()) 
    }
    
    // PermissionsController - Implémentation iOS basique
    single<PermissionsController> {
        object : PermissionsController {
            override suspend fun getPermissionState(permission: Permission): PermissionState {
                // Implémentation basique - toujours retourner non-accordé pour l'instant
                return PermissionState.NotDetermined
            }
            
            override suspend fun isPermissionGranted(permission: Permission): Boolean {
                return false
            }
            
            override fun openAppSettings() {
                // Ouvrir les paramètres de l'app iOS
            }
            
            override suspend fun providePermission(permission: Permission) {
                // Demander la permission sur iOS
            }
        }
    }
    
    // MediaPermissionManager utilisant MOKO Permissions
    single<MediaPermissionManager> { 
        MokoMediaPermissionManager(get()) 
    }
}