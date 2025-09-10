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
import kotlinx.datetime.Clock

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val authDao: AuthDao,
    private val sharedDataStore: SharedDataStore,
    private val secureStorage: SecureStorage
) : AuthRepository {
    
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
    
    // Local storage operations - Room Database + Secure Storage
    override suspend fun saveAuthToken(token: String, userEmail: String) {
        // Sauvegarder d'abord dans le stockage sécurisé (priorité)
        try {
            secureStorage.store(KEY_JWT_TOKEN, token)
            secureStorage.store(KEY_USER_EMAIL, userEmail)
        } catch (e: Exception) {
            // En cas d'erreur, continuer avec les anciens systèmes
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
        // Priorité: Stockage sécurisé > Room > DataStore
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
        // Nettoyer tous les stockages pour sécurité maximale
        try {
            secureStorage.remove(KEY_JWT_TOKEN)
            secureStorage.remove(KEY_USER_EMAIL)
        } catch (e: Exception) {
            // Continuer même si le stockage sécurisé échoue
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