package org.jelarose.monalerte.core.storage

import com.liftric.kvault.KVault

/**
 * Implémentation iOS utilisant KVault avec iOS Keychain
 * Stockage sécurisé utilisant le Keychain natif d'iOS avec chiffrement hardware
 */
actual class SecureStorage actual constructor() {
    private val kVault = KVault()
    
    actual suspend fun store(key: String, value: String) {
        kVault.set(key, value)
    }
    
    actual suspend fun retrieve(key: String): String? {
        return kVault.string(key)
    }
    
    actual suspend fun remove(key: String) {
        kVault.deleteObject(key)
    }
    
    actual suspend fun clear() {
        kVault.clear()
    }
}