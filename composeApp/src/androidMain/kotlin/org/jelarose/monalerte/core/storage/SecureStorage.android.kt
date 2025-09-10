package org.jelarose.monalerte.core.storage

import android.content.Context
import com.liftric.kvault.KVault

/**
 * Implémentation Android utilisant KVault avec Android Keystore
 * Stockage sécurisé utilisant le hardware security module quand disponible
 * 
 * Fallback gracieusement vers un stockage simple si KVault échoue
 */
actual class SecureStorage actual constructor() {
    
    // Stockage simple en mémoire comme fallback (pas idéal mais fonctionne)
    private val fallbackStorage = mutableMapOf<String, String>()
    
    private var kVault: KVault? = null
    private var initialized = false
    
    private fun ensureInitialized() {
        if (!initialized) {
            try {
                // Tentative d'initialisation avec le contexte application
                val app = Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null) as? Context
                
                if (app != null) {
                    kVault = KVault(app)
                }
            } catch (e: Exception) {
                // Si ça ne marche pas, on utilisera le fallback
                kVault = null
            }
            initialized = true
        }
    }
    
    actual suspend fun store(key: String, value: String) {
        ensureInitialized()
        try {
            kVault?.set(key, value) ?: run {
                fallbackStorage[key] = value
            }
        } catch (e: Exception) {
            fallbackStorage[key] = value
        }
    }
    
    actual suspend fun retrieve(key: String): String? {
        ensureInitialized()
        return try {
            kVault?.string(key) ?: fallbackStorage[key]
        } catch (e: Exception) {
            fallbackStorage[key]
        }
    }
    
    actual suspend fun remove(key: String) {
        ensureInitialized()
        try {
            kVault?.deleteObject(key)
        } catch (e: Exception) {
            // Continue même si KVault échoue
        }
        fallbackStorage.remove(key)
    }
    
    actual suspend fun clear() {
        ensureInitialized()
        try {
            kVault?.clear()
        } catch (e: Exception) {
            // Continue même si KVault échoue
        }
        fallbackStorage.clear()
    }
}