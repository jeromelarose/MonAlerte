package org.jelarose.monalerte.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jelarose.monalerte.features.auth.data.api.AuthException
import org.jelarose.monalerte.features.auth.domain.models.ApiErrorCode
import org.jelarose.monalerte.features.auth.domain.usecases.LoginUseCase
import org.jelarose.monalerte.features.auth.domain.usecases.RegisterUseCase
import org.jelarose.monalerte.features.auth.domain.usecases.ForgotPasswordUseCase
import org.jelarose.monalerte.features.auth.domain.usecases.GetAuthTokenUseCase

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentScreen: AuthScreen = AuthScreen.LOGIN,
    val email: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val confirmPassword: String = "",
    val errorMessage: String? = null,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val showSuccessMessage: String? = null
)

enum class AuthScreen {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val getAuthTokenUseCase: GetAuthTokenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkExistingAuth()
    }

    private fun checkExistingAuth() {
        viewModelScope.launch {
            val hasValidToken = getAuthTokenUseCase.hasValidToken()
            _uiState.value = _uiState.value.copy(
                isAuthenticated = hasValidToken
            )
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            isEmailValid = isValidEmail(email),
            errorMessage = null
        )
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            isPasswordValid = password.length >= 8,
            errorMessage = null
        )
    }

    fun onFirstNameChanged(firstName: String) {
        _uiState.value = _uiState.value.copy(
            firstName = firstName,
            errorMessage = null
        )
    }

    fun onLastNameChanged(lastName: String) {
        _uiState.value = _uiState.value.copy(
            lastName = lastName,
            errorMessage = null
        )
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(
            phoneNumber = phoneNumber,
            errorMessage = null
        )
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            errorMessage = null
        )
    }

    fun switchToScreen(screen: AuthScreen) {
        _uiState.value = _uiState.value.copy(
            currentScreen = screen,
            errorMessage = null,
            showSuccessMessage = null
        )
    }

    fun login() {
        if (!validateLoginInputs()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            loginUseCase(_uiState.value.email, _uiState.value.password)
                .onSuccess { token ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    val errorMessage = when (error) {
                        is AuthException -> getErrorMessage(error.authError.code)
                        else -> error.message ?: "Une erreur est survenue"
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
        }
    }

    fun register() {
        if (!validateRegisterInputs()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            registerUseCase(
                email = _uiState.value.email,
                password = _uiState.value.password,
                confirmPassword = _uiState.value.confirmPassword
            ).onSuccess { token ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    errorMessage = null
                )
            }.onFailure { error ->
                val errorMessage = when (error) {
                    is AuthException -> getErrorMessage(error.authError.code)
                    else -> error.message ?: "Une erreur est survenue"
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    fun forgotPassword() {
        if (!isValidEmail(_uiState.value.email)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Veuillez entrer un email valide"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            forgotPasswordUseCase(_uiState.value.email)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showSuccessMessage = "Un email de réinitialisation a été envoyé",
                        currentScreen = AuthScreen.LOGIN
                    )
                }
                .onFailure { error ->
                    val errorMessage = when (error) {
                        is AuthException -> getErrorMessage(error.authError.code)
                        else -> error.message ?: "Une erreur est survenue"
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            getAuthTokenUseCase.logout()
            _uiState.value = AuthUiState() // Reset to initial state
        }
    }

    private fun validateLoginInputs(): Boolean {
        val email = _uiState.value.email
        val password = _uiState.value.password

        when {
            email.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "L'email est requis")
                return false
            }
            !isValidEmail(email) -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Format d'email invalide")
                return false
            }
            password.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Le mot de passe est requis")
                return false
            }
        }
        return true
    }

    private fun validateRegisterInputs(): Boolean {
        val email = _uiState.value.email
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword
        val firstName = _uiState.value.firstName
        val lastName = _uiState.value.lastName
        val phoneNumber = _uiState.value.phoneNumber

        when {
            email.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "L'email est requis")
                return false
            }
            !isValidEmail(email) -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Format d'email invalide")
                return false
            }
            password.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Le mot de passe est requis")
                return false
            }
            password.length < 8 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Le mot de passe doit contenir au moins 8 caractères")
                return false
            }
            password != confirmPassword -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Les mots de passe ne correspondent pas")
                return false
            }
            firstName.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Le prénom est requis")
                return false
            }
            lastName.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Le nom est requis")
                return false
            }
            phoneNumber.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Le numéro de téléphone est requis")
                return false
            }
        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun getErrorMessage(code: ApiErrorCode): String {
        return when (code) {
            ApiErrorCode.EMAIL_ALREADY_EXISTS -> "Cette adresse email est déjà utilisée"
            ApiErrorCode.INVALID_CREDENTIALS -> "Email ou mot de passe incorrect"
            ApiErrorCode.EMAIL_NOT_FOUND -> "Adresse email non trouvée"
            ApiErrorCode.EMAIL_NOT_VERIFIED -> "Votre email n'est pas encore vérifié"
            ApiErrorCode.MISSING_FIELDS -> "Tous les champs sont requis"
            ApiErrorCode.OLD_PASSWORD_INCORRECT -> "Ancien mot de passe incorrect"
            ApiErrorCode.NETWORK_ERROR -> "Erreur de connexion réseau"
            ApiErrorCode.TIMEOUT_ERROR -> "Délai d'attente dépassé"
            else -> "Une erreur est survenue"
        }
    }
}