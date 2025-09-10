package org.jelarose.monalerte.features.auth.domain.usecases

import kotlinx.coroutines.flow.Flow
import org.jelarose.monalerte.features.auth.data.database.AuthTokenEntity
import org.jelarose.monalerte.features.auth.domain.repository.AuthRepository

class GetAuthTokenUseCase(private val authRepository: AuthRepository) {
    
    suspend fun getCurrentToken(): String? {
        return authRepository.getAuthToken()
    }
    
    suspend fun getCurrentUserEmail(): String? {
        return authRepository.getUserEmail()
    }
    
    suspend fun hasValidToken(): Boolean {
        return authRepository.hasValidToken()
    }
    
    fun getAuthTokenFlow(): Flow<AuthTokenEntity?> {
        return authRepository.getAuthTokenFlow()
    }
    
    suspend fun logout() {
        authRepository.clearAuthToken()
    }
}