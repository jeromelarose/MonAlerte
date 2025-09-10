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
            
            // Interface Menu
            "interface_menu_title" -> "Menu des Interfaces"
            "interface_menu_description" -> "Choisissez votre mode de fonctionnement"
            "interface_menu_watch_mode_title" -> "Mode Veille"
            "interface_menu_watch_mode_2_description" -> "Surveillance avancée avec détection automatique"
            "interface_menu_position_tracking_title" -> "Suivi Position"
            "interface_menu_position_tracking_description" -> "Partage votre position en temps réel"
            "interface_menu_address_selection_title" -> "Gérer les lieux"
            "interface_menu_address_selection_description" -> "Configurez vos adresses favorites"
            "interface_menu_location_history_title" -> "Historique Position"
            "interface_menu_location_history_description" -> "Consultez vos déplacements"
            "interface_menu_old_watch_mode_title" -> "Ancien Mode Veille"
            "mode_standby_description" -> "Mode de surveillance classique"
            "interface_menu_content_desc" -> "Menu principal des interfaces de l'application"
            
            // Settings
            "settings_title" -> "Paramètres"
            "auth_my_account" -> "Mon compte"
            "settings_account_description" -> "Gérer votre compte et vos informations"
            "settings_emergency_contacts" -> "Contacts d'urgence"
            "settings_emergency_contacts_description" -> "Gérer vos contacts d'urgence"
            "settings_shortcuts" -> "Raccourcis"
            "settings_shortcuts_description" -> "Configurer les raccourcis rapides"
            "settings_permissions" -> "Autorisations"
            "settings_permissions_description" -> "Gérer les autorisations"
            "settings_sms_alert" -> "Alerte SMS"
            "settings_sms_alert_description" -> "Configuration des alertes SMS"
            "settings_widget_config" -> "Configuration Widget"
            "settings_widget_config_description" -> "Paramètres du widget"
            "settings_notifications" -> "Notifications"
            "settings_notifications_description" -> "Paramètres des notifications"
            "settings_privacy_policy" -> "Politique de confidentialité"
            "settings_privacy_policy_description" -> "Consulter la politique de confidentialité"
            
            // Account Screen
            "account_title" -> "Mon Compte"
            "account_information" -> "Informations du compte"
            "account_language_description" -> "Choisir la langue de l'application"
            "account_change_password" -> "Changer le mot de passe"
            "account_language_selection" -> "Langue"
            "account_logout" -> "Se déconnecter"
            "account_change_password_dialog_title" -> "Changer le mot de passe"
            "account_old_password" -> "Ancien mot de passe"
            "account_new_password" -> "Nouveau mot de passe"
            "account_confirm_new_password" -> "Confirmer le nouveau mot de passe"
            "account_change_password_button" -> "Modifier"
            "account_logout_dialog_title" -> "Déconnexion"
            "account_logout_dialog_message" -> "Êtes-vous sûr de vouloir vous déconnecter ?"
            "account_logout_dialog_confirm" -> "Oui, me déconnecter"
            "account_logout_dialog_cancel" -> "Annuler"
            "common_close" -> "Fermer"
            
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
            
            // Interface Menu
            "interface_menu_title" -> "Interface Menu"
            "interface_menu_description" -> "Choose your operating mode"
            "interface_menu_watch_mode_title" -> "Watch Mode"
            "interface_menu_watch_mode_2_description" -> "Advanced monitoring with automatic detection"
            "interface_menu_position_tracking_title" -> "Position Tracking"
            "interface_menu_position_tracking_description" -> "Share your location in real time"
            "interface_menu_address_selection_title" -> "Manage Locations"
            "interface_menu_address_selection_description" -> "Configure your favorite addresses"
            "interface_menu_location_history_title" -> "Location History"
            "interface_menu_location_history_description" -> "View your movements"
            "interface_menu_old_watch_mode_title" -> "Legacy Watch Mode"
            "mode_standby_description" -> "Classic monitoring mode"
            "interface_menu_content_desc" -> "Main menu of application interfaces"
            
            // Settings
            "settings_title" -> "Settings"
            "auth_my_account" -> "My Account"
            "settings_account_description" -> "Manage your account and information"
            "settings_emergency_contacts" -> "Emergency Contacts"
            "settings_emergency_contacts_description" -> "Manage your emergency contacts"
            "settings_shortcuts" -> "Shortcuts"
            "settings_shortcuts_description" -> "Configure quick shortcuts"
            "settings_permissions" -> "Permissions"
            "settings_permissions_description" -> "Manage permissions"
            "settings_sms_alert" -> "SMS Alert"
            "settings_sms_alert_description" -> "SMS alert configuration"
            "settings_widget_config" -> "Widget Configuration"
            "settings_widget_config_description" -> "Widget settings"
            "settings_notifications" -> "Notifications"
            "settings_notifications_description" -> "Notification settings"
            "settings_privacy_policy" -> "Privacy Policy"
            "settings_privacy_policy_description" -> "View privacy policy"
            
            // Account Screen
            "account_title" -> "My Account"
            "account_information" -> "Account Information"
            "account_language_description" -> "Choose the application language"
            "account_change_password" -> "Change Password"
            "account_language_selection" -> "Language"
            "account_logout" -> "Sign Out"
            "account_change_password_dialog_title" -> "Change Password"
            "account_old_password" -> "Current Password"
            "account_new_password" -> "New Password"
            "account_confirm_new_password" -> "Confirm New Password"
            "account_change_password_button" -> "Update"
            "account_logout_dialog_title" -> "Sign Out"
            "account_logout_dialog_message" -> "Are you sure you want to sign out?"
            "account_logout_dialog_confirm" -> "Yes, sign me out"
            "account_logout_dialog_cancel" -> "Cancel"
            "common_close" -> "Close"
            
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