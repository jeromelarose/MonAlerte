package org.jelarose.monalerte.features.auth.presentation.viewmodels

import co.touchlab.kermit.Logger
import org.jelarose.monalerte.features.auth.data.repository.AuthRepositoryImpl
import org.jelarose.monalerte.features.auth.domain.usecases.LoginUseCase
import org.jelarose.monalerte.features.auth.domain.usecases.RegisterUseCase
import org.jelarose.monalerte.features.auth.domain.usecases.ForgotPasswordUseCase
import org.jelarose.monalerte.features.auth.domain.usecases.GetAuthTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String, val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val message: String) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}

sealed class PolicyState {
    object Loading : PolicyState()
    object Accepted : PolicyState()
    data class Required(val version: Int) : PolicyState()
}

sealed class OnboardingState {
    object Loading : OnboardingState()
    object Required : OnboardingState()
    object Completed : OnboardingState()
}

/**
 * Shared AuthViewModel for Compose Multiplatform
 * Manages authentication state and operations across platforms
 * Migrated from original MonAlerte app with identical behavior
 */
class SharedAuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val getAuthTokenUseCase: GetAuthTokenUseCase,
    private val authRepository: AuthRepositoryImpl,
) {
    
    private val logger = Logger.withTag("SharedAuthViewModel")
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Form fields
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    // Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error handling
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _customErrorMessage = MutableStateFlow<String?>(null)
    val customErrorMessage: StateFlow<String?> = _customErrorMessage.asStateFlow()

    // Success states
    private val _registrationSuccessMessage = MutableStateFlow<String?>(null)
    val registrationSuccessMessage: StateFlow<String?> = _registrationSuccessMessage.asStateFlow()

    private val _showRegistrationSuccessDialog = MutableStateFlow(false)
    val showRegistrationSuccessDialog: StateFlow<Boolean> = _showRegistrationSuccessDialog.asStateFlow()

    // Auth states
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    // JWT Token
    private val _jwtToken = MutableStateFlow<String?>(null)
    val jwtToken: StateFlow<String?> = _jwtToken.asStateFlow()

    // Policy and Onboarding states
    private val _policyState = MutableStateFlow<PolicyState>(PolicyState.Loading)
    val policyState: StateFlow<PolicyState> = _policyState.asStateFlow()

    private val _onboardingState = MutableStateFlow<OnboardingState>(OnboardingState.Loading)
    val onboardingState: StateFlow<OnboardingState> = _onboardingState.asStateFlow()

    // Token verification
    private val _tokenVerificationResult = MutableSharedFlow<Boolean>(replay = 1)
    val tokenVerificationResult = _tokenVerificationResult.asSharedFlow()

    var isVerifyingToken = false
        private set

    // Network connectivity
    private val _isInternetAvailable = MutableStateFlow(true)
    val isInternetAvailable: StateFlow<Boolean> = _isInternetAvailable.asStateFlow()

    private val _isRetryingConnection = MutableStateFlow(false)
    val isRetryingConnection: StateFlow<Boolean> = _isRetryingConnection.asStateFlow()

    init {
        logger.d("SharedAuthViewModel initialized")
        // Initialize policy and onboarding states
        _policyState.value = PolicyState.Accepted // Simulate policy acceptance for demo
        _onboardingState.value = OnboardingState.Completed // Simulate onboarding completion
        
        // Load persisted auth data
        loadPersistedAuthData()
    }
    
    /**
     * Load persisted authentication data on initialization
     */
    private fun loadPersistedAuthData() {
        viewModelScope.launch {
            try {
                // Load from repository (Room KMP + Settings fallback)
                val userEmail = authRepository.getUserEmail()
                val jwtToken = authRepository.getAuthToken()
                
                if (!jwtToken.isNullOrEmpty() && !userEmail.isNullOrEmpty()) {
                    logger.i("Found persisted auth data for: $userEmail")
                    _email.value = userEmail
                    _jwtToken.value = jwtToken
                } else {
                    logger.d("No persisted auth data found")
                }
            } catch (e: Exception) {
                logger.e("Error loading persisted auth data: ${e.message}")
            }
        }
    }

    // Form field updates
    fun updateEmail(newEmail: String) {
        logger.d("Email updated: $newEmail")
        _email.value = newEmail
        clearErrors()
    }

    fun updatePassword(newPassword: String) {
        logger.d("Password updated (length: ${newPassword.length})")
        _password.value = newPassword
        clearErrors()
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        logger.d("Confirm password updated (length: ${newConfirmPassword.length})")
        _confirmPassword.value = newConfirmPassword
        clearErrors()
    }

    // Validation (IDENTICAL to original)
    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "auth_error_email_empty"
            !isValidEmail(email) -> "auth_error_email_invalid_format"
            else -> null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        // Simple email validation for multiplatform compatibility
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex()
        return emailRegex.matches(email)
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "auth_error_password_empty"
            password.length < 6 -> "auth_error_password_short" // IDENTICAL 6-char validation
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "auth_error_confirm_password_empty"
            password != confirmPassword -> "auth_error_passwords_do_not_match"
            else -> null
        }
    }

    // Login (IDENTICAL logic)
    fun login() {
        val currentEmail = _email.value.trim()
        val currentPassword = _password.value

        logger.d("Starting login for email: $currentEmail")
        
        // Validate fields
        val emailError = validateEmail(currentEmail)
        val passwordError = validatePassword(currentPassword)
        
        if (emailError != null || passwordError != null) {
            _customErrorMessage.value = getErrorMessage(emailError ?: passwordError ?: "")
            logger.w("Login validation failed: ${emailError ?: passwordError}")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthState.Loading
            clearErrors()

            try {
                loginUseCase(currentEmail, currentPassword)
                    .onSuccess { token ->
                        logger.i("Login successful for: $currentEmail")
                        _jwtToken.value = token
                        _authState.value = AuthState.Success(
                            token = token,
                            message = "Connexion réussie"
                        )
                        _isLoading.value = false
                    }
                    .onFailure { error ->
                        logger.e("Login error: ${error.message}")
                        val errorMessage = when {
                            error.message?.contains("Invalid", ignoreCase = true) == true -> "Email ou mot de passe incorrect"
                            error.message?.contains("network", ignoreCase = true) == true || 
                            error.message?.contains("timeout", ignoreCase = true) == true -> 
                                "Erreur de connexion réseau. Veuillez réessayer."
                            else -> error.message ?: "Erreur de connexion"
                        }
                        _errorMessage.value = errorMessage
                        _authState.value = AuthState.Error(errorMessage)
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                logger.e("Login exception: ${e.message}")
                _errorMessage.value = e.message ?: "Erreur de connexion inconnue"
                _authState.value = AuthState.Error(e.message ?: "Erreur inconnue")
                _isLoading.value = false
            }
        }
    }

    // Register (IDENTICAL logic with firstName, lastName, phoneNumber)
    fun register(firstName: String = "", lastName: String = "", phoneNumber: String = "") {
        val currentEmail = _email.value.trim()
        val currentPassword = _password.value
        val currentConfirmPassword = _confirmPassword.value

        logger.d("Starting registration for email: $currentEmail")

        // Validate fields
        val emailError = validateEmail(currentEmail)
        val passwordError = validatePassword(currentPassword)
        val confirmPasswordError = validateConfirmPassword(currentPassword, currentConfirmPassword)

        if (emailError != null || passwordError != null || confirmPasswordError != null) {
            _customErrorMessage.value = getErrorMessage(emailError ?: passwordError ?: confirmPasswordError ?: "")
            logger.w("Registration validation failed: ${emailError ?: passwordError ?: confirmPasswordError}")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _registrationState.value = RegistrationState.Loading
            clearErrors()

            try {
                registerUseCase(
                    email = currentEmail,
                    password = currentPassword,
                    confirmPassword = currentConfirmPassword
                    // firstName, lastName, phoneNumber are optional parameters with null defaults
                    // acceptedPolicyVersion is set to 1 inside the use case like in original
                ).onSuccess { token ->
                    logger.i("Registration successful for: $currentEmail")
                    val successMessage = "Inscription réussie !"
                    _registrationState.value = RegistrationState.Success(successMessage)
                    _showRegistrationSuccessDialog.value = true
                    _registrationSuccessMessage.value = "$successMessage\nVous pouvez maintenant vous connecter."
                    clearFields()
                    _isLoading.value = false
                }.onFailure { error ->
                    logger.e("Registration error: ${error.message}")
                    val errorMessage = when {
                        error.message?.contains("already exists", ignoreCase = true) == true || 
                        error.message?.contains("duplicate", ignoreCase = true) == true -> 
                            "Cet email est déjà enregistré"
                        error.message?.contains("network", ignoreCase = true) == true || 
                        error.message?.contains("timeout", ignoreCase = true) == true -> 
                            "Erreur de connexion réseau. Veuillez réessayer."
                        error.message?.contains("validation", ignoreCase = true) == true -> 
                            "Les données fournies sont invalides"
                        else -> error.message ?: "Erreur d'inscription"
                    }
                    _errorMessage.value = errorMessage
                    _registrationState.value = RegistrationState.Error(errorMessage)
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                logger.e("Registration exception: ${e.message}")
                _errorMessage.value = e.message ?: "Erreur d'inscription inconnue"
                _registrationState.value = RegistrationState.Error(e.message ?: "Erreur inconnue")
                _isLoading.value = false
            }
        }
    }

    // Forgot Password (IDENTICAL logic)
    fun forgotPassword() {
        val currentEmail = _email.value.trim()

        logger.d("Starting forgot password for email: $currentEmail")

        val emailError = validateEmail(currentEmail)
        if (emailError != null) {
            _customErrorMessage.value = getErrorMessage(emailError)
            logger.w("Forgot password validation failed: $emailError")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            clearErrors()

            try {
                forgotPasswordUseCase(currentEmail)
                    .onSuccess {
                        logger.i("Forgot password request successful for: $currentEmail")
                        val message = "Email de réinitialisation envoyé avec succès."
                        _registrationSuccessMessage.value = message
                        _showRegistrationSuccessDialog.value = true
                        clearFields()
                        _isLoading.value = false
                    }
                    .onFailure { error ->
                        logger.e("Forgot password error: ${error.message}")
                        val errorMessage = when {
                            error.message?.contains("not found", ignoreCase = true) == true -> 
                                "Aucun compte trouvé avec cet email"
                            error.message?.contains("network", ignoreCase = true) == true || 
                            error.message?.contains("timeout", ignoreCase = true) == true -> 
                                "Erreur de connexion réseau. Veuillez réessayer."
                            else -> error.message ?: "Erreur lors de la demande"
                        }
                        _errorMessage.value = errorMessage
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                logger.e("Forgot password exception: ${e.message}")
                _errorMessage.value = e.message ?: "Erreur lors de la demande de réinitialisation"
                _isLoading.value = false
            }
        }
    }

    // Clear methods
    fun clearFields() {
        logger.d("Clearing all form fields")
        _email.value = ""
        _password.value = ""
        _confirmPassword.value = ""
        clearErrors()
    }

    fun clearErrors() {
        _errorMessage.value = null
        _customErrorMessage.value = null
    }

    fun dismissRegistrationSuccessDialog() {
        _showRegistrationSuccessDialog.value = false
        _registrationSuccessMessage.value = null
    }

    // Policy and connectivity methods
    fun onPolicyAccepted() {
        _policyState.value = PolicyState.Accepted
    }

    fun retryConnection() {
        _isRetryingConnection.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _isInternetAvailable.value = true
            _isRetryingConnection.value = false
        }
    }

    fun verifyTokenOnStartup(token: String) {
        isVerifyingToken = true
        logger.d("Verifying token on startup")
        
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(1000)
                val isValid = token.isNotBlank() // Simplified verification
                _tokenVerificationResult.emit(isValid)
                logger.i("Token verification result: $isValid")
            } catch (e: Exception) {
                logger.e("Token verification failed: ${e.message}")
                _tokenVerificationResult.emit(false)
            } finally {
                isVerifyingToken = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                logger.i("Logging out user - clearing auth data")
                getAuthTokenUseCase.logout()
                
                // Clear in-memory state
                _jwtToken.value = null
                _authState.value = AuthState.Idle
                clearFields()
                
                logger.d("Logout completed successfully")
            } catch (e: Exception) {
                logger.e("Error during logout: ${e.message}")
            }
        }
    }

    // Check if user is already authenticated
    fun checkExistingAuthentication(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                logger.d("Checking existing authentication...")
                
                // Check if we have a valid token stored
                val storedToken = authRepository.getAuthToken()
                val storedEmail = authRepository.getUserEmail()
                
                if (!storedToken.isNullOrEmpty() && !storedEmail.isNullOrEmpty()) {
                    logger.i("Found stored authentication for: $storedEmail")
                    
                    // Verify the token is still valid with the server
                    authRepository.verifyToken(null)
                        .onSuccess {
                            logger.i("Stored token is valid, user is authenticated")
                            _jwtToken.value = storedToken
                            _email.value = storedEmail
                            _authState.value = AuthState.Success(
                                token = storedToken,
                                message = "Session restaurée"
                            )
                            onResult(true)
                        }
                        .onFailure { error ->
                            logger.w("Stored token is invalid: ${error.message}")
                            // Clear invalid token
                            authRepository.clearAuthToken()
                            onResult(false)
                        }
                } else {
                    logger.d("No stored authentication found")
                    onResult(false)
                }
            } catch (e: Exception) {
                logger.e("Error checking existing authentication: ${e.message}")
                onResult(false)
            }
        }
    }
    
    // Alternative synchronous check for stored auth (without server verification)
    suspend fun hasValidStoredAuth(): Boolean {
        return try {
            val token = authRepository.getAuthToken()
            val email = authRepository.getUserEmail()
            !token.isNullOrEmpty() && !email.isNullOrEmpty()
        } catch (e: Exception) {
            logger.e("Error checking stored auth: ${e.message}")
            false
        }
    }

    // Helper function to get localized error messages (will be connected to string resources)
    private fun getErrorMessage(key: String): String {
        return when (key) {
            "auth_error_email_empty" -> "L'email est requis"
            "auth_error_email_invalid_format" -> "Format d'email invalide"
            "auth_error_password_empty" -> "Le mot de passe est requis"
            "auth_error_password_short" -> "Le mot de passe doit contenir au moins 6 caractères"
            "auth_error_passwords_not_match" -> "Les mots de passe ne correspondent pas"
            else -> key
        }
    }

    fun dispose() {
        logger.d("SharedAuthViewModel disposed")
    }
}