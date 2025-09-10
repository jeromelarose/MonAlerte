package org.jelarose.monalerte.features.auth.data.store

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import co.touchlab.kermit.Logger
import org.jelarose.monalerte.core.storage.SecureStorage
import org.jelarose.monalerte.features.auth.data.database.AuthDao
import org.jelarose.monalerte.features.auth.data.database.AuthTokenEntity
import kotlinx.datetime.Clock

/**
 * Cache intelligent pour la gestion des tokens d'authentification
 * 
 * STRATÉGIE HYBRIDE SIMPLIFIÉE:
 * - Cache en mémoire avec synchronisation thread-safe
 * - Stratégie de lecture en cascade (Memory > SecureStorage > Room)
 * - Stratégie d'écriture multi-niveau pour redondance
 * - Compatible avec l'API AuthRepository existante
 * - Prépare le terrain pour une migration vers Store 5 plus tard
 */
class SmartAuthCache(
    private val secureStorage: SecureStorage,
    private val authDao: AuthDao
) {
    private val logger = Logger.withTag("SmartAuthCache")
    private val mutex = Mutex()
    
    // Cache en mémoire thread-safe
    private var memoryCache = mutableMapOf<String, String>()
    
    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_EMAIL = "user_email"
    }
    
    /**
     * Lire un token avec stratégie de cache intelligent
     * Ordre de priorité: Memory > SecureStorage > Room
     */
    suspend fun getToken(key: String): String? = mutex.withLock {
        logger.d { "Reading token: $key" }
        
        // 1. Vérifier cache mémoire d'abord (le plus rapide)
        memoryCache[key]?.let { cachedValue ->
            if (cachedValue.isNotEmpty()) {
                logger.d { "Token found in memory cache: $key" }
                return cachedValue
            }
        }
        
        // 2. Lire depuis SecureStorage (sécurisé)
        try {
            secureStorage.retrieve(key)?.takeIf { it.isNotEmpty() }?.let { secureValue ->
                logger.d { "Token found in secure storage: $key" }
                // Mettre en cache mémoire pour accès futurs
                memoryCache[key] = secureValue
                return secureValue
            }
        } catch (e: Exception) {
            logger.w(e) { "Failed to read from secure storage: $key" }
        }
        
        // 3. Fallback vers Room database (compatible)
        if (key == KEY_JWT_TOKEN) {
            authDao.getAuthToken()?.jwtToken?.takeIf { it.isNotEmpty() }?.let { roomValue ->
                logger.d { "Token found in Room database: $key" }
                // Mettre en cache mémoire et secure storage pour performance future
                memoryCache[key] = roomValue
                try {
                    secureStorage.store(key, roomValue)
                } catch (e: Exception) {
                    logger.w(e) { "Failed to upgrade token to secure storage: $key" }
                }
                return roomValue
            }
        }
        
        logger.d { "Token not found in any cache layer: $key" }
        return null
    }
    
    /**
     * Sauvegarder un token avec stratégie multi-niveau
     * Sauvegarde simultanée: Memory + SecureStorage + Room
     */
    suspend fun saveToken(key: String, value: String, userEmail: String = "") = mutex.withLock {
        logger.d { "Saving token: $key" }
        
        // 1. Sauvegarder en mémoire (immédiat)
        memoryCache[key] = value
        logger.d { "Token saved to memory cache: $key" }
        
        // 2. Sauvegarder en SecureStorage (sécurisé)
        try {
            secureStorage.store(key, value)
            logger.d { "Token saved to secure storage: $key" }
        } catch (e: Exception) {
            logger.e(e) { "Failed to save to secure storage: $key" }
        }
        
        // 3. Sauvegarder en Room (compatibilité)
        if (key == KEY_JWT_TOKEN) {
            try {
                val authEntity = AuthTokenEntity(
                    id = 1,
                    jwtToken = value,
                    userEmail = userEmail,
                    lastUpdated = Clock.System.now().toEpochMilliseconds()
                )
                authDao.insertAuthToken(authEntity)
                logger.d { "Token saved to Room database: $key" }
            } catch (e: Exception) {
                logger.e(e) { "Failed to save to Room database: $key" }
            }
        }
        
        // Sauvegarder email séparément si fourni
        if (userEmail.isNotEmpty()) {
            try {
                memoryCache[KEY_USER_EMAIL] = userEmail
                secureStorage.store(KEY_USER_EMAIL, userEmail)
                logger.d { "User email saved: $userEmail" }
            } catch (e: Exception) {
                logger.w(e) { "Failed to save user email" }
            }
        }
    }
    
    /**
     * Nettoyer tous les tokens et caches
     */
    suspend fun clearAllTokens() = mutex.withLock {
        logger.d { "Clearing all tokens from cache" }
        
        // 1. Vider cache mémoire
        memoryCache.clear()
        logger.d { "Memory cache cleared" }
        
        // 2. Nettoyer SecureStorage
        try {
            secureStorage.remove(KEY_JWT_TOKEN)
            secureStorage.remove(KEY_USER_EMAIL)
            logger.d { "Secure storage cleared" }
        } catch (e: Exception) {
            logger.w(e) { "Failed to clear secure storage" }
        }
        
        // 3. Nettoyer Room database
        try {
            authDao.clearAuthToken()
            logger.d { "Room database cleared" }
        } catch (e: Exception) {
            logger.w(e) { "Failed to clear Room database" }
        }
    }
    
    /**
     * Obtenir statistiques du cache pour monitoring
     */
    suspend fun getCacheStats(): Map<String, Any> = mutex.withLock {
        mapOf(
            "memoryCacheSize" to memoryCache.size,
            "memoryCacheKeys" to memoryCache.keys.toList(),
            "hasJwtTokenInMemory" to memoryCache.containsKey(KEY_JWT_TOKEN),
            "hasUserEmailInMemory" to memoryCache.containsKey(KEY_USER_EMAIL)
        )
    }
    
    /**
     * Pré-chauffer le cache en chargeant les données depuis les sources persistantes
     */
    suspend fun warmupCache() = mutex.withLock {
        logger.d { "Warming up cache..." }
        
        // Charger le token JWT si disponible
        getToken(KEY_JWT_TOKEN)
        
        // Charger l'email utilisateur si disponible  
        try {
            secureStorage.retrieve(KEY_USER_EMAIL)?.takeIf { it.isNotEmpty() }?.let { email ->
                memoryCache[KEY_USER_EMAIL] = email
            }
        } catch (e: Exception) {
            logger.w(e) { "Failed to warmup user email cache" }
        }
        
        val stats = getCacheStats()
        logger.d { "Cache warmed up: $stats" }
    }
}