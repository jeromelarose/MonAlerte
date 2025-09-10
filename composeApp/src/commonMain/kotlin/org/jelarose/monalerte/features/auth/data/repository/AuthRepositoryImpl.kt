package org.jelarose.monalerte.features.auth.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.jelarose.monalerte.core.utils.SharedDataStore
import org.jelarose.monalerte.core.storage.SecureStorage
import org.jelarose.monalerte.features.auth.data.api.AuthApiService
import org.jelarose.monalerte.features.auth.data.database.AuthDao
import org.jelarose.monalerte.features.auth.data.database.AuthTokenEntity
import org.jelarose.monalerte.features.auth.domain.models.*
import org.jelarose.monalerte.features.auth.domain.repository.AuthRepository
import org.jelarose.monalerte.features.auth.data.api.AuthException
import org.jelarose.monalerte.features.auth.data.store.SmartAuthCache
import kotlinx.datetime.Clock
import co.touchlab.kermit.Logger

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val authDao: AuthDao,
    private val sharedDataStore: SharedDataStore,
    private val secureStorage: SecureStorage,
    private val smartAuthCache: SmartAuthCache? = null // Cache intelligent optionnel
) : AuthRepository {
    
    private val logger = Logger.withTag("AuthRepositoryImpl")
    
    companion object {
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_APP_LANGUAGE = "app_language"
        private const val KEY_POLICY_VERSION = "policy_version"
    }
    
    // Network operations
    override suspend fun login(email: String, password: String, acceptedPolicyVersion: Int?): Result<String> {
        val request = LoginRequest(email, password, acceptedPolicyVersion)
        return authApiService.login(request).map { response ->
            // Save token to both storage systems
            saveAuthToken(response.token, email)
            saveUserSettings(email, response.token)
            response.token
        }
    }
    
    override suspend fun register(
        email: String,
        password: String,
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        acceptedPolicyVersion: Int?
    ): Result<String> {
        // Match original Android app: only send email, password, and acceptedPolicyVersion
        // firstName, lastName, phoneNumber are ignored to match original bridge implementation
        val request = RegisterRequest(email, password, acceptedPolicyVersion)
        return authApiService.register(request).map { response ->
            // Save token to both storage systems
            saveAuthToken(response.token, email)
            saveUserSettings(email, response.token)
            response.token
        }
    }
    
    override suspend fun forgotPassword(email: String): Result<Unit> {
        val request = ResetPasswordRequest(email)
        return authApiService.forgotPassword(request)
    }
    
    override suspend fun changePassword(oldPassword: String, newPassword: String, confirmNewPassword: String): Result<Unit> {
        val token = getAuthToken() ?: return Result.failure(
            AuthException(
                AuthError(
                    code = ApiErrorCode.MISSING_TOKEN_IN_RESPONSE,
                    message = "No authentication token available"
                )
            )
        )
        
        val request = ChangePasswordRequest(oldPassword, newPassword, confirmNewPassword)
        return authApiService.changePassword(request, token)
    }
    
    override suspend fun verifyToken(policyVersion: Int?): Result<Unit> {
        val token = getAuthToken() ?: return Result.failure(
            AuthException(
                AuthError(
                    code = ApiErrorCode.MISSING_TOKEN_IN_RESPONSE,
                    message = "No authentication token available"
                )
            )
        )
        
        val request = policyVersion?.let { AcceptPolicyRequest(it) }
        return authApiService.verifyToken(request, token).onSuccess {
            policyVersion?.let { setPolicyVersion(it) }
        }
    }
    
    // Local storage operations - Store 5 + Fallbacks
    override suspend fun saveAuthToken(token: String, userEmail: String) {
        logger.d { "Saving auth token for user: $userEmail" }
        
        // Utiliser SmartAuthCache en priorité si disponible
        smartAuthCache?.let { cache ->
            try {
                logger.d { "Using SmartAuthCache for token storage" }
                // SmartAuthCache gère automatiquement Memory + SecureStorage + Room
                cache.saveToken(KEY_JWT_TOKEN, token, userEmail)
                return
            } catch (e: Exception) {
                logger.e(e) { "Failed to use SmartAuthCache, falling back to manual storage" }
            }
        }
        
        // Fallback: méthode manuelle existante
        logger.d { "Using manual token storage (SmartAuthCache not available)" }
        try {
            secureStorage.store(KEY_JWT_TOKEN, token)
            secureStorage.store(KEY_USER_EMAIL, userEmail)
        } catch (e: Exception) {
            logger.w(e) { "Failed to use secure storage, continuing with Room" }
        }
        
        // Conserver aussi l'ancienne méthode pour compatibilité
        val authEntity = AuthTokenEntity(
            jwtToken = token,
            userEmail = userEmail,
            lastUpdated = Clock.System.now().toEpochMilliseconds()
        )
        authDao.insertAuthToken(authEntity)
    }
    
    override suspend fun getAuthToken(): String? {
        // Utiliser SmartAuthCache en priorité si disponible
        smartAuthCache?.let { cache ->
            try {
                logger.d { "Reading auth token from SmartAuthCache" }
                return cache.getToken(KEY_JWT_TOKEN)
            } catch (e: Exception) {
                logger.e(e) { "Failed to read from SmartAuthCache, falling back to manual retrieval" }
            }
        }
        
        // Fallback: méthode manuelle existante
        logger.d { "Using manual token retrieval (SmartAuthCache not available)" }
        return try {
            secureStorage.retrieve(KEY_JWT_TOKEN)?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            null
        } ?: authDao.getAuthToken()?.jwtToken?.takeIf { it.isNotEmpty() }
          ?: sharedDataStore.getString(KEY_JWT_TOKEN)?.takeIf { it.isNotEmpty() }
    }
    
    override suspend fun getUserEmail(): String? {
        // Priorité: Stockage sécurisé > Room > DataStore
        return try {
            secureStorage.retrieve(KEY_USER_EMAIL)?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            null
        } ?: authDao.getAuthToken()?.userEmail?.takeIf { it.isNotEmpty() }
          ?: sharedDataStore.getString(KEY_USER_EMAIL)?.takeIf { it.isNotEmpty() }
    }
    
    override suspend fun clearAuthToken() {
        logger.d { "Clearing auth token from all sources" }
        
        // Utiliser SmartAuthCache en priorité si disponible
        smartAuthCache?.let { cache ->
            try {
                logger.d { "Clearing auth token using SmartAuthCache" }
                // SmartAuthCache gère automatiquement le nettoyage de tous les niveaux
                cache.clearAllTokens()
                
                // Nettoyer aussi DataStore pour compatibilité
                sharedDataStore.removeKey(KEY_JWT_TOKEN)
                sharedDataStore.removeKey(KEY_USER_EMAIL)
                sharedDataStore.removeKey(KEY_USER_ID)
                return
            } catch (e: Exception) {
                logger.e(e) { "Failed to clear using SmartAuthCache, falling back to manual clearing" }
            }
        }
        
        // Fallback: méthode manuelle existante
        logger.d { "Using manual token clearing (SmartAuthCache not available)" }
        try {
            secureStorage.remove(KEY_JWT_TOKEN)
            secureStorage.remove(KEY_USER_EMAIL)
        } catch (e: Exception) {
            logger.w(e) { "Failed to clear from secure storage" }
        }
        
        // Nettoyer aussi les anciens systèmes
        authDao.clearAuthToken()
        sharedDataStore.removeKey(KEY_JWT_TOKEN)
        sharedDataStore.removeKey(KEY_USER_EMAIL)
        sharedDataStore.removeKey(KEY_USER_ID)
    }
    
    override suspend fun hasValidToken(): Boolean {
        // Vérifier dans tous les stockages disponibles
        val hasSecureToken = try {
            !secureStorage.retrieve(KEY_JWT_TOKEN).isNullOrEmpty()
        } catch (e: Exception) {
            false
        }
        
        return hasSecureToken || authDao.hasValidToken() || !sharedDataStore.getString(KEY_JWT_TOKEN).isNullOrEmpty()
    }
    
    override fun getAuthTokenFlow(): Flow<AuthTokenEntity?> {
        return authDao.getAuthTokenFlow()
    }
    
    // Settings operations - SharedDataStore (fallback and compatibility)
    override suspend fun saveUserSettings(email: String, token: String) {
        sharedDataStore.putString(KEY_USER_EMAIL, email)
        sharedDataStore.putString(KEY_JWT_TOKEN, token)
    }
    
    override suspend fun getUserId(): String? {
        return sharedDataStore.getString(KEY_USER_ID)
    }
    
    override suspend fun getAppLanguage(): String? {
        return sharedDataStore.getString(KEY_APP_LANGUAGE)
    }
    
    override suspend fun setAppLanguage(language: String) {
        sharedDataStore.putString(KEY_APP_LANGUAGE, language)
    }
    
    override suspend fun getPolicyVersion(): Int? {
        return sharedDataStore.getInt(KEY_POLICY_VERSION).takeIf { it != 0 }
    }
    
    override suspend fun setPolicyVersion(version: Int) {
        sharedDataStore.putInt(KEY_POLICY_VERSION, version)
    }
}