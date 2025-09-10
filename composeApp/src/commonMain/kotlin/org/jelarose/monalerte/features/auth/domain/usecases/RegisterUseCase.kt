package org.jelarose.monalerte.features.auth.domain.usecases

import org.jelarose.monalerte.features.auth.domain.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
        firstName: String? = null,
        lastName: String? = null,
        phoneNumber: String? = null
    ): Result<String> {
        // Validate input
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }
        
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be empty"))
        }
        
        if (confirmPassword.isBlank()) {
            return Result.failure(IllegalArgumentException("Confirm password cannot be empty"))
        }
        
        if (password != confirmPassword) {
            return Result.failure(IllegalArgumentException("Passwords do not match"))
        }
        
        if (!isValidEmail(email)) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }
        
        if (!isValidPassword(password)) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }
        
        // Perform registration - exactly like original app
        return authRepository.register(
            email.trim(),
            password,
            firstName?.trim(),
            lastName?.trim(),
            phoneNumber?.trim(),
            acceptedPolicyVersion = 1 // Always set to 1 like in original
        )
    }
    
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
    
    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    
}