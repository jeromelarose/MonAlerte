package org.jelarose.monalerte.features.auth.domain.usecases

import org.jelarose.monalerte.features.auth.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    
    suspend operator fun invoke(
        email: String,
        password: String,
        acceptedPolicyVersion: Int? = null
    ): Result<String> {
        // Validate input
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }
        
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be empty"))
        }
        
        if (!isValidEmail(email)) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }
        
        // Perform login
        return authRepository.login(email.trim(), password, acceptedPolicyVersion)
    }
    
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}