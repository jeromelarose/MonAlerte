package org.jelarose.monalerte.features.auth.presentation.viewmodels

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests simples pour les structures de données et logique UI
 */
class AuthViewModelTest : FunSpec({
    
    test("AuthUiState should have correct default values") {
        val defaultState = AuthUiState()
        
        defaultState.isLoading shouldBe false
        defaultState.isAuthenticated shouldBe false
        defaultState.currentScreen shouldBe AuthScreen.LOGIN
        defaultState.email shouldBe ""
        defaultState.password shouldBe ""
        defaultState.firstName shouldBe ""
        defaultState.lastName shouldBe ""
        defaultState.phoneNumber shouldBe ""
        defaultState.confirmPassword shouldBe ""
        defaultState.errorMessage shouldBe null
        defaultState.isEmailValid shouldBe true
        defaultState.isPasswordValid shouldBe true
        defaultState.showSuccessMessage shouldBe null
    }
    
    test("AuthScreen enum should have correct values") {
        val screens = AuthScreen.entries.toSet()
        
        screens shouldBe setOf(
            AuthScreen.LOGIN,
            AuthScreen.REGISTER,
            AuthScreen.FORGOT_PASSWORD
        )
    }
    
    test("AuthUiState copy should work correctly") {
        val originalState = AuthUiState()
        
        val modifiedState = originalState.copy(
            email = "test@example.com",
            password = "password123",
            isLoading = true,
            currentScreen = AuthScreen.REGISTER
        )
        
        modifiedState.email shouldBe "test@example.com"
        modifiedState.password shouldBe "password123"
        modifiedState.isLoading shouldBe true
        modifiedState.currentScreen shouldBe AuthScreen.REGISTER
        
        // Original should be unchanged
        originalState.email shouldBe ""
        originalState.password shouldBe ""
        originalState.isLoading shouldBe false
        originalState.currentScreen shouldBe AuthScreen.LOGIN
    }
    
    test("AuthUiState should preserve other fields when updating one field") {
        val state = AuthUiState(
            email = "test@example.com",
            password = "password123",
            firstName = "John",
            lastName = "Doe"
        )
        
        val updatedState = state.copy(phoneNumber = "123456789")
        
        updatedState.phoneNumber shouldBe "123456789"
        updatedState.email shouldBe "test@example.com"
        updatedState.password shouldBe "password123"
        updatedState.firstName shouldBe "John"
        updatedState.lastName shouldBe "Doe"
    }
})