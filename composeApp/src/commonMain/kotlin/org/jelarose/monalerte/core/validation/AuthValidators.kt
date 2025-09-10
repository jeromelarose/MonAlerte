package org.jelarose.monalerte.core.validation

import io.konform.validation.Validation
import io.konform.validation.constraints.*

/**
 * Structures de données pour la validation
 */
data class LoginData(val email: String, val password: String)

data class RegisterData(
    val email: String, 
    val password: String, 
    val confirmPassword: String, 
    val firstName: String, 
    val lastName: String, 
    val phoneNumber: String
)

/**
 * Validators utilisant Konform - remplace la validation manuelle
 */

val loginValidation = Validation<LoginData> {
    LoginData::email {
        pattern("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") hint "Format d'email invalide"
    }
    LoginData::password {
        minLength(1) hint "Le mot de passe est requis"
    }
}

val registerValidation = Validation<RegisterData> {
    RegisterData::email {
        pattern("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") hint "Format d'email invalide"
    }
    RegisterData::password {
        minLength(8) hint "Le mot de passe doit contenir au moins 8 caractères"
    }
    RegisterData::firstName {
        minLength(1) hint "Le prénom est requis"
    }
    RegisterData::lastName {
        minLength(1) hint "Le nom est requis"
    }
    RegisterData::phoneNumber {
        minLength(1) hint "Le numéro de téléphone est requis"
    }
    // Note: confirmPassword sera validé manuellement pour comparer avec password
}

/**
 * Validators utilitaires pour validation simple
 */
val emailValidation = Validation<String> {
    pattern("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") hint "Format d'email invalide"
}

val passwordValidation = Validation<String> {
    minLength(8) hint "Le mot de passe doit contenir au moins 8 caractères"
}

/**
 * Fonctions utilitaires pour validation rapide - compatibilité avec code existant
 */
object AuthValidationUtils {
    
    /**
     * Valide un email (compatible avec l'ancienne méthode isValidEmail)
     */
    fun isValidEmail(email: String): Boolean {
        val result = emailValidation(email.trim())
        return result.errors.isEmpty()
    }
    
    /**
     * Valide la longueur du mot de passe (compatible avec l'ancienne validation)
     */
    fun isValidPasswordLength(password: String): Boolean {
        return password.length >= 8
    }
    
    /**
     * Valide les données de login et retourne le premier message d'erreur
     */
    fun validateLogin(email: String, password: String): String? {
        val loginData = LoginData(email.trim(), password)
        val result = loginValidation(loginData)
        return if (result.errors.isNotEmpty()) {
            result.errors.first().message
        } else null
    }
    
    /**
     * Valide les données d'inscription et retourne le premier message d'erreur
     */
    fun validateRegister(
        email: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): String? {
        // Validation des mots de passe correspondants d'abord
        if (password != confirmPassword) {
            return "Les mots de passe ne correspondent pas"
        }
        
        val registerData = RegisterData(
            email = email.trim(),
            password = password,
            confirmPassword = confirmPassword,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            phoneNumber = phoneNumber.trim()
        )
        
        val result = registerValidation(registerData)
        return if (result.errors.isNotEmpty()) {
            result.errors.first().message
        } else null
    }
}