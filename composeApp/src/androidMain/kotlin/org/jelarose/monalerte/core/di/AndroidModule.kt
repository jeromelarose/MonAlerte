package org.jelarose.monalerte.core.di

import org.koin.dsl.module
import org.jelarose.monalerte.features.home.domain.MediaPermissionManager
import org.jelarose.monalerte.features.home.domain.MokoMediaPermissionManager
import org.koin.android.ext.koin.androidContext
import org.jelarose.monalerte.core.utils.SharedDataStore
import org.jelarose.monalerte.core.utils.createDataStore as createDataStoreAndroid
import dev.icerock.moko.permissions.PermissionsController

/**
 * Module spécifique Android pour les implémentations platform-specific
 */
val androidModule = module {
    
    // SharedDataStore - Singleton pour Android
    single<SharedDataStore> { 
        SharedDataStore(createDataStoreAndroid(androidContext())) 
    }
    
    // PermissionsController - Singleton MOKO
    single<PermissionsController> {
        PermissionsController(applicationContext = androidContext())
    }
    
    // MediaPermissionManager utilisant MOKO Permissions
    single<MediaPermissionManager> { 
        MokoMediaPermissionManager(get()) 
    }
}