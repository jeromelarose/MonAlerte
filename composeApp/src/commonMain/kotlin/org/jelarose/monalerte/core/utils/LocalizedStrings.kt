package org.jelarose.monalerte.core.utils

import androidx.compose.runtime.*

/**
 * Localized strings manager for dynamic language switching in KMP
 */
object LocalizedStrings {
    
    fun getString(key: String, language: String): String {
        return when (language) {
            LanguageManager.LANGUAGE_ENGLISH -> getEnglishString(key)
            LanguageManager.LANGUAGE_FRENCH -> getFrenchString(key)
            else -> getFrenchString(key)
        }
    }
    
    private fun getFrenchString(key: String): String {
        return when (key) {
            "auth_label_email" -> "Email"
            "auth_label_password" -> "Mot de passe"
            "auth_label_confirm_password" -> "Confirmer le mot de passe"
            "auth_logo_description_content" -> "Logo de l'application"
            "auth_title_login" -> "Connexion"
            "auth_button_connect" -> "Se connecter"
            "auth_link_forgot_password" -> "Mot de passe oublié ?"
            "auth_link_no_account_register" -> "Pas de compte ? S'enregistrer"
            "auth_welcome_back" -> "Content de vous revoir !"
            "auth_welcome_join_us" -> "Rejoignez-nous pour votre sécurité"
            "auth_welcome_forgot_password" -> "Récupérez votre accès"
            "auth_title_register" -> "Inscription"
            "auth_button_register" -> "S'enregistrer"
            "auth_link_already_account_login" -> "Déjà un compte ? Se connecter"
            "auth_title_reset_password" -> "Réinitialiser le mot de passe"
            "auth_button_send" -> "Envoyer"
            "auth_button_return" -> "Retour"
            "auth_error_email_empty" -> "L'email ne peut pas être vide"
            "auth_error_email_invalid_format" -> "Format d'email invalide"
            "auth_error_password_empty" -> "Le mot de passe ne peut pas être vide"
            "auth_error_password_short" -> "Le mot de passe doit contenir au moins 6 caractères"
            "auth_error_password_short_hint" -> "Au moins 6 caractères."
            "auth_error_confirm_password_empty" -> "Veuillez confirmer votre mot de passe"
            "auth_error_passwords_do_not_match" -> "Les mots de passe ne correspondent pas"
            "auth_content_desc_hide_password" -> "Cacher mot de passe"
            "auth_content_desc_show_password" -> "Afficher mot de passe"
            "language_selection_title" -> "Choisir la langue"
            "language_french" -> "Français"
            "language_english" -> "English"
            "registration_success_dialog_title" -> "Inscription réussie"
            "registration_success_dialog_ok" -> "Compris"
            "privacy_policy_title" -> "Politique de confidentialité"
            "privacy_policy_subtitle" -> "Veuillez lire et accepter notre politique de confidentialité pour continuer"
            "privacy_policy_content" -> "En utilisant cette application, vous acceptez notre politique de confidentialité. Nous collectons et traitons vos données personnelles dans le respect de la réglementation en vigueur.\n\nLes données collectées incluent :\n- Informations de connexion\n- Données de localisation pour les alertes\n- Préférences utilisateur\n\nVos données sont protégées et ne sont jamais partagées avec des tiers sans votre consentement explicite."
            "privacy_policy_accept" -> "Accepter et continuer"
            "privacy_policy_accept_and_continue" -> "Accepter et Continuer"
            "common_cancel" -> "Annuler"
            "auth_api_forgot_password_success" -> "Un email de réinitialisation a été envoyé à votre adresse."
            else -> key // Fallback to key if not found
        }
    }
    
    private fun getEnglishString(key: String): String {
        return when (key) {
            "auth_label_email" -> "Email"
            "auth_label_password" -> "Password"
            "auth_label_confirm_password" -> "Confirm password"
            "auth_logo_description_content" -> "Application logo"
            "auth_title_login" -> "Sign In"
            "auth_button_connect" -> "Sign In"
            "auth_link_forgot_password" -> "Forgot password?"
            "auth_link_no_account_register" -> "No account? Sign Up"
            "auth_welcome_back" -> "Good to see you again!"
            "auth_welcome_join_us" -> "Join us for your safety"
            "auth_welcome_forgot_password" -> "Recover your access"
            "auth_title_register" -> "Sign Up"
            "auth_button_register" -> "Sign Up"
            "auth_link_already_account_login" -> "Already have an account? Sign In"
            "auth_title_reset_password" -> "Reset Password"
            "auth_button_send" -> "Send"
            "auth_button_return" -> "Back"
            "auth_error_email_empty" -> "Email cannot be empty"
            "auth_error_email_invalid_format" -> "Invalid email format"
            "auth_error_password_empty" -> "Password cannot be empty"
            "auth_error_password_short" -> "Password must contain at least 6 characters"
            "auth_error_password_short_hint" -> "At least 6 characters."
            "auth_error_confirm_password_empty" -> "Please confirm your password"
            "auth_error_passwords_do_not_match" -> "Passwords do not match"
            "auth_content_desc_hide_password" -> "Hide password"
            "auth_content_desc_show_password" -> "Show password"
            "language_selection_title" -> "Choose Language"
            "language_french" -> "Français"
            "language_english" -> "English"
            "registration_success_dialog_title" -> "Registration successful"
            "registration_success_dialog_ok" -> "Got it"
            "privacy_policy_title" -> "Privacy Policy"
            "privacy_policy_subtitle" -> "Please read and accept our privacy policy to continue"
            "privacy_policy_content" -> "By using this application, you agree to our privacy policy. We collect and process your personal data in accordance with current regulations.\n\nData collected includes:\n- Login information\n- Location data for alerts\n- User preferences\n\nYour data is protected and never shared with third parties without your explicit consent."
            "privacy_policy_accept" -> "Accept and continue"
            "privacy_policy_accept_and_continue" -> "Accept and Continue"
            "common_cancel" -> "Cancel"
            "auth_api_forgot_password_success" -> "A reset email has been sent to your address."
            else -> key // Fallback to key if not found
        }
    }
}

/**
 * Composable function to get localized string
 */
@Composable
fun localizedString(key: String): String {
    val languageManager: LanguageManager = org.jelarose.monalerte.core.di.koinInject()
    val currentLanguage by languageManager.currentLanguage.collectAsState()
    return LocalizedStrings.getString(key, currentLanguage)
}

@Composable
fun localizedString(key: String, language: String): String {
    return LocalizedStrings.getString(key, language)
}