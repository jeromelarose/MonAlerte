package org.jelarose.monalerte.features.auth.presentation.viewmodels

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests pour les utilitaires d'authentification
 */
class AuthValidationTest : FunSpec({
    
    test("should validate email format correctly") {
        // Tests de validation d'email basique
        val validEmails = listOf(
            "test@example.com",
            "user@domain.co.uk", 
            "name.surname@company.org",
            "user123@test-domain.com"
        )
        
        val invalidEmails = listOf(
            "invalid-email",
            "@domain.com",
            "user@",
            "",
            "   ",
            "user.domain.com"
        )
        
        validEmails.forEach { email ->
            isValidEmailFormat(email) shouldBe true
        }
        
        invalidEmails.forEach { email ->
            isValidEmailFormat(email) shouldBe false
        }
    }
    
    test("should validate password strength correctly") {
        // Tests de validation de mot de passe
        val validPasswords = listOf(
            "12345678", // 8 caractères minimum
            "password123",
            "verylongpassword",
            "P@ssw0rd!"
        )
        
        val invalidPasswords = listOf(
            "", // vide
            "1234567", // 7 caractères
            "short",
            "   "
        )
        
        validPasswords.forEach { password ->
            isValidPasswordLength(password) shouldBe true
        }
        
        invalidPasswords.forEach { password ->
            isValidPasswordLength(password) shouldBe false
        }
    }
    
    test("should trim whitespace from email") {
        val emailWithSpaces = "  test@example.com  "
        val trimmedEmail = emailWithSpaces.trim()
        
        trimmedEmail shouldBe "test@example.com"
        isValidEmailFormat(trimmedEmail) shouldBe true
    }
    
    test("should handle empty and null strings safely") {
        isValidEmailFormat("") shouldBe false
        isValidPasswordLength("") shouldBe false
        
        val nullString: String? = null
        isValidEmailFormat(nullString ?: "") shouldBe false
        isValidPasswordLength(nullString ?: "") shouldBe false
    }
})

/**
 * Fonctions utilitaires pour les tests (qui pourraient être extraites dans AuthViewModel)
 */
private fun isValidEmailFormat(email: String): Boolean {
    return email.trim().isNotEmpty() && email.contains("@") && email.contains(".")
}

private fun isValidPasswordLength(password: String): Boolean {
    return password.length >= 8
}