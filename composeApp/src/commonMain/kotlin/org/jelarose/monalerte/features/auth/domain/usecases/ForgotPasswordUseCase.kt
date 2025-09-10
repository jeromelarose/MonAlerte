package org.jelarose.monalerte.features.auth.domain.usecases

import org.jelarose.monalerte.features.auth.domain.repository.AuthRepository

class ForgotPasswordUseCase(private val authRepository: AuthRepository) {
    
    suspend operator fun invoke(email: String): Result<Unit> {
        // Validate input
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }
        
        if (!isValidEmail(email)) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }
        
        // Perform password reset
        return authRepository.forgotPassword(email.trim())
    }
    
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}