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
 * iOS specific dependencies module
 */
fun iosPlatformModule() = module {
    
    // SharedDataStore - Singleton pour iOS
    single<SharedDataStore> { 
        SharedDataStore(createDataStore()) 
    }
    
    // PermissionsController - Implémentation iOS avec persistance
    single<PermissionsController> {
        object : PermissionsController {
            
            private val sharedDataStore: SharedDataStore = get()
            
            private suspend fun isPermissionStoredAsGranted(permission: String): Boolean {
                return try {
                    val stored = sharedDataStore.getString("permission_$permission")
                    val granted = stored == "true"
                    println("🔐 iOS: Reading permission $permission: $granted")
                    granted
                } catch (e: Exception) {
                    println("🚨 iOS: Error reading permission $permission: ${e.message}")
                    false
                }
            }
            
            private suspend fun storePermissionAsGranted(permission: String, granted: Boolean) {
                try {
                    sharedDataStore.putString("permission_$permission", granted.toString())
                    println("🔐 iOS: Permission $permission stored as: $granted")
                } catch (e: Exception) {
                    println("🚨 iOS: Error storing permission $permission: ${e.message}")
                }
            }
            
            override suspend fun getPermissionState(permission: Permission): PermissionState {
                return when (permission) {
                    Permission.RECORD_AUDIO -> {
                        val granted = isPermissionStoredAsGranted("RECORD_AUDIO")
                        if (granted) PermissionState.Granted else PermissionState.NotDetermined
                    }
                    Permission.CAMERA -> {
                        val granted = isPermissionStoredAsGranted("CAMERA")
                        if (granted) PermissionState.Granted else PermissionState.NotDetermined
                    }
                    else -> PermissionState.NotDetermined
                }
            }
            
            override suspend fun isPermissionGranted(permission: Permission): Boolean {
                return getPermissionState(permission) == PermissionState.Granted
            }
            
            override fun openAppSettings() {
                println("🔐 iOS: Opening app settings...")
            }
            
            override suspend fun providePermission(permission: Permission) {
                when (permission) {
                    Permission.RECORD_AUDIO -> {
                        println("🔐 iOS: Requesting audio permission (simulated)...")
                        // Simuler l'acceptation de la permission et la persister
                        storePermissionAsGranted("RECORD_AUDIO", true)
                        println("🔐 iOS: Audio permission GRANTED and STORED (simulated)")
                    }
                    Permission.CAMERA -> {
                        println("🔐 iOS: Requesting camera permission (simulated)...")
                        // Simuler l'acceptation de la permission et la persister
                        storePermissionAsGranted("CAMERA", true)
                        println("🔐 iOS: Camera permission GRANTED and STORED (simulated)")
                    }
                    else -> {
                        println("🔐 iOS: Permission $permission not implemented")
                    }
                }
            }
        }
    }
    
    // MediaPermissionManager utilisant MOKO Permissions
    single<MediaPermissionManager> { 
        MokoMediaPermissionManager(get()) 
    }
}