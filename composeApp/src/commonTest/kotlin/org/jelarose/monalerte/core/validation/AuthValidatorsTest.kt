package org.jelarose.monalerte.core.validation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty

/**
 * Tests pour les nouveaux validators Konform
 */
class AuthValidatorsTest : FunSpec({
    
    test("loginValidation should validate correct login data") {
        val validLogin = LoginData("test@example.com", "password123")
        val result = loginValidation(validLogin)
        
        result.errors.shouldBeEmpty()
    }
    
    test("loginValidation should reject invalid email") {
        val invalidEmailLogins = listOf(
            LoginData("invalid-email", "password123"),
            LoginData("@domain.com", "password123"),
            LoginData("user@", "password123"),
            LoginData("", "password123")
        )
        
        invalidEmailLogins.forEach { loginData ->
            val result = loginValidation(loginData)
            result.errors.shouldNotBeEmpty()
            result.errors.first().message shouldBe "Format d'email invalide"
        }
    }
    
    test("loginValidation should reject empty password") {
        val emptyPasswordLogin = LoginData("test@example.com", "")
        val result = loginValidation(emptyPasswordLogin)
        
        result.errors.shouldNotBeEmpty()
        result.errors.first().message shouldBe "Le mot de passe est requis"
    }
    
    test("registerValidation should validate correct registration data") {
        val validRegister = RegisterData(
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "123456789"
        )
        val result = registerValidation(validRegister)
        
        result.errors.shouldBeEmpty()
    }
    
    test("registerValidation should reject short password") {
        val shortPasswordRegister = RegisterData(
            email = "test@example.com",
            password = "1234567", // 7 caractères
            confirmPassword = "1234567",
            firstName = "John",
            lastName = "Doe", 
            phoneNumber = "123456789"
        )
        val result = registerValidation(shortPasswordRegister)
        
        result.errors.shouldNotBeEmpty()
        result.errors.first().message shouldBe "Le mot de passe doit contenir au moins 8 caractères"
    }
    
    test("registerValidation should reject empty required fields") {
        val emptyFieldsRegister = RegisterData(
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123",
            firstName = "", // vide
            lastName = "Doe",
            phoneNumber = "123456789"
        )
        val result = registerValidation(emptyFieldsRegister)
        
        result.errors.shouldNotBeEmpty()
        result.errors.first().message shouldBe "Le prénom est requis"
    }
    
    test("emailValidation should work correctly") {
        val validEmails = listOf(
            "test@example.com",
            "user@domain.co.uk",
            "name.surname@company.org"
        )
        
        val invalidEmails = listOf(
            "invalid-email",
            "@domain.com", 
            "user@",
            ""
        )
        
        validEmails.forEach { email ->
            emailValidation(email).errors.shouldBeEmpty()
        }
        
        invalidEmails.forEach { email ->
            emailValidation(email).errors.shouldNotBeEmpty()
        }
    }
    
    test("passwordValidation should validate minimum length") {
        val validPasswords = listOf("12345678", "password123", "verylongpassword")
        val invalidPasswords = listOf("", "1234567", "short")
        
        validPasswords.forEach { password ->
            passwordValidation(password).errors.shouldBeEmpty()
        }
        
        invalidPasswords.forEach { password ->
            passwordValidation(password).errors.shouldNotBeEmpty()
        }
    }
})

/**
 * Tests pour AuthValidationUtils - compatibilité avec l'ancien code
 */
class AuthValidationUtilsTest : FunSpec({
    
    test("isValidEmail should work like the old implementation but better") {
        // Emails valides
        val validEmails = listOf(
            "test@example.com",
            "user@domain.co.uk",
            "name.surname@company.org",
            "user123@test-domain.com"
        )
        
        validEmails.forEach { email ->
            AuthValidationUtils.isValidEmail(email) shouldBe true
        }
        
        // Emails invalides
        val invalidEmails = listOf(
            "invalid-email",
            "@domain.com",
            "user@",
            "",
            "   ",
            "user.domain.com"
        )
        
        invalidEmails.forEach { email ->
            AuthValidationUtils.isValidEmail(email) shouldBe false
        }
    }
    
    test("isValidPasswordLength should work like the old validation") {
        AuthValidationUtils.isValidPasswordLength("12345678") shouldBe true
        AuthValidationUtils.isValidPasswordLength("password123") shouldBe true
        AuthValidationUtils.isValidPasswordLength("1234567") shouldBe false
        AuthValidationUtils.isValidPasswordLength("") shouldBe false
    }
    
    test("validateLogin should return null for valid data") {
        val result = AuthValidationUtils.validateLogin("test@example.com", "password123")
        result shouldBe null
    }
    
    test("validateLogin should return error message for invalid data") {
        val invalidEmailResult = AuthValidationUtils.validateLogin("invalid-email", "password123")
        invalidEmailResult shouldNotBe null
        invalidEmailResult shouldBe "Format d'email invalide"
        
        val emptyPasswordResult = AuthValidationUtils.validateLogin("test@example.com", "")
        emptyPasswordResult shouldNotBe null 
        emptyPasswordResult shouldBe "Le mot de passe est requis"
    }
    
    test("validateRegister should return null for valid data") {
        val result = AuthValidationUtils.validateRegister(
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "123456789"
        )
        result shouldBe null
    }
    
    test("validateRegister should return error for password mismatch") {
        val result = AuthValidationUtils.validateRegister(
            email = "test@example.com",
            password = "password123",
            confirmPassword = "different123",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "123456789"
        )
        result shouldBe "Les mots de passe ne correspondent pas"
    }
    
    test("validateRegister should return error for invalid fields") {
        val result = AuthValidationUtils.validateRegister(
            email = "invalid-email",
            password = "password123",
            confirmPassword = "password123",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "123456789"
        )
        result shouldBe "Format d'email invalide"
    }
    
    test("validateRegister should trim whitespace correctly") {
        val result = AuthValidationUtils.validateRegister(
            email = "  test@example.com  ",
            password = "password123",
            confirmPassword = "password123",
            firstName = "  John  ",
            lastName = "  Doe  ",
            phoneNumber = "  123456789  "
        )
        result shouldBe null
    }
})