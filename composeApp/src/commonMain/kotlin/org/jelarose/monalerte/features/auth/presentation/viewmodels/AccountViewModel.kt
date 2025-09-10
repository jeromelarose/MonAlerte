package org.jelarose.monalerte.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jelarose.monalerte.features.auth.data.repository.AuthRepositoryImpl
import org.jelarose.monalerte.core.utils.SharedDataStore
/**
 * ViewModel pour l'écran de gestion du compte
 * Gère le changement de mot de passe et la déconnexion
 */
class AccountViewModel(
    private val authRepository: AuthRepositoryImpl,
    private val sharedDataStore: SharedDataStore
) : ViewModel() {

    // États pour l'email de l'utilisateur
    private val _userEmail = MutableStateFlow("Chargement…")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()
    
    init {
        // Charger l'email de l'utilisateur au démarrage
        viewModelScope.launch {
            val email = sharedDataStore.getString("user_email")
            _userEmail.value = email?.ifBlank { "Utilisateur non connecté" } ?: "Utilisateur non connecté"
        }
    }

    // États pour les champs de changement de mot de passe
    private val _oldPassword = MutableStateFlow("")
    val oldPassword: StateFlow<String> = _oldPassword.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmNewPassword = MutableStateFlow("")
    val confirmNewPassword: StateFlow<String> = _confirmNewPassword.asStateFlow()

    // États pour les erreurs de validation
    private val _oldPasswordError = MutableStateFlow<String?>(null)
    val oldPasswordError: StateFlow<String?> = _oldPasswordError.asStateFlow()

    private val _newPasswordError = MutableStateFlow<String?>(null)
    val newPasswordError: StateFlow<String?> = _newPasswordError.asStateFlow()

    private val _confirmNewPasswordError = MutableStateFlow<String?>(null)
    val confirmNewPasswordError: StateFlow<String?> = _confirmNewPasswordError.asStateFlow()

    // États pour les opérations API
    private val _isLoadingChangePassword = MutableStateFlow(false)
    val isLoadingChangePassword: StateFlow<Boolean> = _isLoadingChangePassword.asStateFlow()

    private val _changePasswordApiError = MutableStateFlow<String?>(null)
    val changePasswordApiError: StateFlow<String?> = _changePasswordApiError.asStateFlow()

    private val _changePasswordSuccess = MutableStateFlow<String?>(null)
    val changePasswordSuccess: StateFlow<String?> = _changePasswordSuccess.asStateFlow()

    private val _isLoadingLogout = MutableStateFlow(false)
    val isLoadingLogout: StateFlow<Boolean> = _isLoadingLogout.asStateFlow()

    // Validité du formulaire de changement de mot de passe
    val isChangePasswordFormValid: StateFlow<Boolean> = combine(
        oldPassword,
        newPassword,
        confirmNewPassword,
        _oldPasswordError,
        _newPasswordError,
        _confirmNewPasswordError
    ) { values: Array<*> ->
        val oldPass = values[0] as String
        val newPass = values[1] as String
        val confirmPass = values[2] as String
        val oldError = values[3] as String?
        val newError = values[4] as String?
        val confirmError = values[5] as String?
        
        oldPass.isNotBlank() &&
                newPass.isNotBlank() &&
                confirmPass.isNotBlank() &&
                oldError == null &&
                newError == null &&
                confirmError == null &&
                newPass == confirmPass
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    // Fonctions pour mettre à jour les champs
    fun onOldPasswordChange(value: String) {
        _oldPassword.value = value
        validateOldPassword()
        clearApiMessages()
    }

    fun onNewPasswordChange(value: String) {
        _newPassword.value = value
        validateNewPassword()
        validateConfirmNewPassword()
        clearApiMessages()
    }

    fun onConfirmNewPasswordChange(value: String) {
        _confirmNewPassword.value = value
        validateConfirmNewPassword()
        clearApiMessages()
    }

    // Fonctions de validation
    private fun validateOldPassword() {
        _oldPasswordError.value = when {
            _oldPassword.value.isBlank() -> "Le mot de passe ne peut pas être vide"
            _oldPassword.value.length < 6 -> "Le mot de passe doit contenir au moins 6 caractères"
            else -> null
        }
    }

    private fun validateNewPassword() {
        _newPasswordError.value = when {
            _newPassword.value.isBlank() -> "Le nouveau mot de passe ne peut pas être vide"
            _newPassword.value.length < 6 -> "Le nouveau mot de passe doit contenir au moins 6 caractères"
            else -> null
        }
    }

    private fun validateConfirmNewPassword() {
        _confirmNewPasswordError.value = when {
            _confirmNewPassword.value.isBlank() -> "Veuillez confirmer votre mot de passe"
            _newPassword.value != _confirmNewPassword.value -> "Les mots de passe ne correspondent pas"
            else -> null
        }
    }

    // Fonction pour changer le mot de passe
    fun changePassword() {
        if (!isChangePasswordFormValid.value || _isLoadingChangePassword.value) {
            return
        }

        viewModelScope.launch {
            _isLoadingChangePassword.value = true
            _changePasswordApiError.value = null
            _changePasswordSuccess.value = null

            try {
                val result = authRepository.changePassword(
                    oldPassword = _oldPassword.value,
                    newPassword = _newPassword.value,
                    confirmNewPassword = _confirmNewPassword.value
                )

                result.fold(
                    onSuccess = {
                        _changePasswordSuccess.value = "Mot de passe changé avec succès!"
                        clearPasswordFields()
                    },
                    onFailure = { exception ->
                        _changePasswordApiError.value = exception.message ?: "Erreur lors du changement de mot de passe"
                    }
                )
            } catch (e: Exception) {
                _changePasswordApiError.value = e.message ?: "Erreur lors du changement de mot de passe"
            } finally {
                _isLoadingChangePassword.value = false
            }
        }
    }

    // Fonction pour se déconnecter
    fun logout(onSuccess: () -> Unit) {
        if (_isLoadingLogout.value) {
            return
        }

        viewModelScope.launch {
            _isLoadingLogout.value = true

            try {
                // Effacer les données d'authentification
                authRepository.clearAuthToken()
                sharedDataStore.putString("user_email", "")
                sharedDataStore.putString("jwt_token", "")
                onSuccess()
            } catch (e: Exception) {
                // En cas d'erreur, on procède quand même à la déconnexion locale
                onSuccess()
            } finally {
                _isLoadingLogout.value = false
            }
        }
    }

    // Fonctions utilitaires
    fun clearPasswordFields() {
        _oldPassword.value = ""
        _newPassword.value = ""
        _confirmNewPassword.value = ""
        _oldPasswordError.value = null
        _newPasswordError.value = null
        _confirmNewPasswordError.value = null
    }

    fun clearApiMessages() {
        _changePasswordApiError.value = null
        _changePasswordSuccess.value = null
    }

    fun clearChangePasswordFieldsAndErrors() {
        clearPasswordFields()
        clearApiMessages()
    }
}