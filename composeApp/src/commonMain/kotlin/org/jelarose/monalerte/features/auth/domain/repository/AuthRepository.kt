package org.jelarose.monalerte.features.auth.domain.repository

import kotlinx.coroutines.flow.Flow
import org.jelarose.monalerte.features.auth.data.database.AuthTokenEntity
import org.jelarose.monalerte.features.auth.domain.models.*

interface AuthRepository {
    
    // Network operations
    suspend fun login(email: String, password: String, acceptedPolicyVersion: Int? = null): Result<String>
    suspend fun register(
        email: String, 
        password: String, 
        firstName: String? = null, 
        lastName: String? = null, 
        phoneNumber: String? = null,
        acceptedPolicyVersion: Int? = null
    ): Result<String>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun changePassword(oldPassword: String, newPassword: String, confirmNewPassword: String): Result<Unit>
    suspend fun verifyToken(policyVersion: Int? = null): Result<Unit>
    
    // Local storage operations
    suspend fun saveAuthToken(token: String, userEmail: String)
    suspend fun getAuthToken(): String?
    suspend fun getUserEmail(): String?
    suspend fun clearAuthToken()
    suspend fun hasValidToken(): Boolean
    fun getAuthTokenFlow(): Flow<AuthTokenEntity?>
    
    // Settings operations (for compatibility with original app)
    suspend fun saveUserSettings(email: String, token: String)
    suspend fun getUserId(): String?
    suspend fun getAppLanguage(): String?
    suspend fun setAppLanguage(language: String)
    suspend fun getPolicyVersion(): Int?
    suspend fun setPolicyVersion(version: Int)
}